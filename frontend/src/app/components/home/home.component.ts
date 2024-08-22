import { Component, ElementRef, ViewChild } from '@angular/core';
import { FormControl } from "@angular/forms";
import { UsersService } from "@app/service/users.service";
import { combineLatest, connect, map, mergeMap, Observable, of, startWith, switchMap, tap } from "rxjs";
import { User } from "@app/model/User";
import { ChatService } from "@app/service/chat.service";
import { MessageService } from "@app/service/message.service";
import { Message } from "@app/model/Message";

@Component({
    selector: 'app-home',
    templateUrl: './home.component.html',
    styleUrl: './home.component.scss'
})
export class HomeComponent {
    constructor(
        private userService: UsersService,
        private chatService: ChatService,
        private messageService: MessageService
    ) {
    }

    @ViewChild('endOfChat')
    endOfChat: ElementRef | undefined;

    searchControl = new FormControl('')
    chatListControl = new FormControl()
    messageControl = new FormControl()

    user$ = this.userService.getCurrentUser()
    users$ = combineLatest(
        [this.userService.getAllUsers(), this.user$, this.searchControl.valueChanges.pipe(startWith(''))])
        .pipe(
            map(([users, user, searchString]) => users.filter(
                u => {
                    return u.username?.toLowerCase().includes(searchString ?? "".toLowerCase()) && u._id !== user?._id;
                }))
        )
    chatItemList$ = this.chatService.getAllChats()
    selectedChatItem$ = this.chatListControl.valueChanges.pipe(
        switchMap(value =>
            this.chatItemList$.pipe(
                map(chatItems => {
                    const chatItem = chatItems.find(chatItem => chatItem.chat._id === (value?.[0] ?? ""));
                    this.connectAndSubscribeSocket()
                    this.fetchInitMessages()
                    return chatItem
                })
            )
        )
    );

    messages: Message[] = []

    createChat(receiver: User) {
        this.chatService.isExistingChat(receiver._id).pipe(
            switchMap(chatId => {
                if (chatId) return of(chatId)
                else {
                    // fetch user again
                    return this.chatService.createChat(receiver).pipe(map(chat => chat._id))
                }
            }),
            mergeMap(chatId => {
                return this.chatService.getAllChats().pipe(
                    tap(chats => {
                        this.chatItemList$ = of(chats);
                    }),
                    map(() => chatId)
                )
            })
        ).subscribe(chatId => {
            console.log("2: this is chatID", chatId);
            this.chatListControl.setValue([chatId]);
        })
    }

    connectAndSubscribeSocket() {
        if (this.selectedChatItem$) {
            this.disconnectWebSocket()
            const websocketSubject = this.messageService.initSocket(this.chatListControl.value[0]);
            console.log("connection is ", websocketSubject)
            websocketSubject.subscribe({
                next: connection => connection.subscribe({
                    next: message => {
                        this.messages.push(message as Message)
                        console.log("message received ", message)
                        this.scrollToBottom()
                    },
                    error: error => console.log(error),
                    complete: () => console.log("connection completed")
                }),
                error: err => console.error(err)
            })
        }
    }

    disconnectWebSocket() {
        this.messageService.disconnect()
    }

    sendMessage() {
        const message = this.messageControl.value;
        const selectedChatId = this.chatListControl.value[0];
        if (message && selectedChatId) {
            this.messageService.sendMessage(message);
            this.messageControl.setValue("")
        }
    }


    fetchInitMessages() {
        this.messageService.getMessages(this.chatListControl.value[0], 10).subscribe(messages => {
                this.messages = messages
            }
        )
    }

    fetchMoreMessages() {
        this.messageService.getMessages(this.chatListControl.value[0], 10, this.messages[0]._id).subscribe(messages => {
                this.messages = messages.concat(this.messages)
            }
        )
    }


    scrollToBottom() {
        setTimeout(() => {
            if (this.endOfChat) {
                this.endOfChat.nativeElement.scrollIntoView({ behavior: "smooth" })
            }
        }, 100)
    }

}
