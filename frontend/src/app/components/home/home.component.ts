import { Component, ElementRef, ViewChild } from '@angular/core';
import { FormControl } from "@angular/forms";
import { UsersService } from "@app/service/users.service";
import { combineLatest, map, mergeMap, of, startWith, switchMap, tap } from "rxjs";
import { User } from "@app/model/User";
import { ChatService } from "@app/service/chat.service";
import { MessageService } from "@app/service/message.service";
import { Message } from "@app/model/Message";
import { ChatItem } from "@app/model/ChatItem";
import { Chat } from "@app/model/Chat";

@Component({
    selector: 'app-home',
    templateUrl: './home.component.html',
    styleUrl: './home.component.scss',
})
export class HomeComponent {
    constructor(
        private userService: UsersService,
        private chatService: ChatService,
        private messageService: MessageService
    ) { }

    @ViewChild('endOfChat')
    endOfChat?: ElementRef;
    @ViewChild('chatScroll', { static: false })
    chatScroll?: ElementRef;

    isLoading = false;
    isInitialLoad = true;
    hasMore = true;
    messages: Message[] = []
    messageBatchSize = 40;
    selectedUser?: User;

    searchControl = new FormControl('')
    chatListControl = new FormControl()
    messageControl = new FormControl()

    availableUser$ = this.userService.getAllUsers()

    user$ = this.userService.getCurrentUser(this.selectedUser?._id)
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
                    const chatItem = chatItems.find(chatItem => chatItem.chat._id === (value?.[0] ?? "")) || null;
                    this.hasMore = true;
                    this.messages = []
                    if (chatItem){
                        this.connectAndSubscribeSocket()
                        this.fetchMessage(true)
                    }
                    return chatItem
                })
            )
        )
    );

    selectChatUser(curUser: User) {
        this.messages = []
        this.user$ = this.userService.getCurrentUser(curUser._id)
        this.chatItemList$ = this.chatService.getAllChats()
        this.users$ = combineLatest(
            [this.userService.getAllUsers(), this.user$, this.searchControl.valueChanges.pipe(startWith(''))])
            .pipe(
                map(([users, user, searchString]) => users.filter(
                    u => {
                        return u.username?.toLowerCase().includes(searchString ?? "".toLowerCase()) && u._id !== user?._id;
                    }))
            )
        this.chatListControl.setValue(null)
    }

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
            console.log("This is chatID: ", chatId);
            this.chatListControl.setValue([chatId]);
        })
    }

    connectAndSubscribeSocket() {
        this.disconnectWebSocket()
        const websocketSubject = this.messageService.initSocket(this.chatListControl.value[0]);
        console.log("connection is ", websocketSubject)
        websocketSubject.subscribe({
            next: connection => connection.subscribe({
                next: message => {
                    this.messages.push(message as Message)
                    this.chatItemList$ = this.chatItemList$.pipe(
                        map(chatItemList => {
                                return chatItemList.map(chatItem => {
                                    if (chatItem.chat._id === this.chatListControl.value[0]) {
                                        const chat: Chat = {
                                            _id: chatItem.chat._id,
                                            lastMessage: message.text,
                                            lastMessageDate: new Date(Date.now()),
                                            userIds: chatItem.chat.userIds
                                        }

                                        return <ChatItem>({
                                            senderId: chatItem.senderId,
                                            chat: chat,
                                            chatName: chatItem.chatName,
                                        })
                                    }
                                    return chatItem
                                })
                            }
                        )
                    )

                    console.log("message received ", message)
                    this.scrollToBottom()
                },
                error: error => console.log(error),
                complete: () => console.log("connection completed")
            }),
            error: err => console.error(err)
        })

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


    fetchMessage(initialLoad: boolean) {
        const lastMessageId = this.messages[0] ? this.messages[0]._id : null
        this.isInitialLoad = initialLoad

        this.isLoading = true;
        console.log("Fetching new message: Loading...")

        this.messageService.getMessages(this.chatListControl.value[0], this.messageBatchSize, lastMessageId).subscribe(messages => {
                if (messages.length === 0) { this.hasMore = false }

                this.messages = initialLoad ? messages : [...messages, ...this.messages]

                this.isLoading = false;
                console.log(this.messages)
                console.log("Fetched new message: Completed")

                // Scroll to bottom first time
                if (initialLoad) {
                    setTimeout(() => {
                        if (this.chatScroll) {
                            this.chatScroll.nativeElement.scrollTop = this.chatScroll.nativeElement.scrollHeight;
                        }
                        this.isInitialLoad = false
                    }, 1);
                }

            }
        )
    }


    onScrollFetchMessage($event: any) {
        if (!this.hasMore) { return; }

        const elem: HTMLElement = $event.srcElement;

        if (elem.scrollTop < 1) { elem.scrollTo(0, 1); }

        if (this.isLoading) { return; }

        if (elem.scrollTop < 50) {
            console.log("finally", elem.scrollTop)
            this.fetchMessage(false)
        }
    }

    scrollToBottom() {
        setTimeout(() => {
            if (this.endOfChat) {
                this.endOfChat.nativeElement.scrollIntoView({ behavior: "smooth" })
            }
        }, 100)
    }

}
