import { Injectable } from '@angular/core';
import { Observable, of, switchMap } from "rxjs";
import { Message } from "@app/model/Message";
import { HttpClient } from "@angular/common/http";
import { environment } from "../../environments/environment.development";
import { UsersService } from "@app/service/users.service";
import { webSocket, WebSocketSubject } from "rxjs/webSocket";

@Injectable({
    providedIn: 'root'
})
export class MessageService {
    connection: WebSocketSubject<any> | undefined
    private backendUrl = environment.backendUrl;

    constructor(
        private http: HttpClient,
        private userService: UsersService
    ) {
    }

    initSocket(chatId: string): Observable<WebSocketSubject<any>> {
        this.disconnect();

        return this.userService.getCurrentUser().pipe(
            switchMap (user => {
                const url = `${environment.websocketUrl}/chat-socket/${chatId}?userId=${user._id}`
                this.connection = webSocket({
                    url: url,
                    serializer: (message: string) => message,
                });
                return of(this.connection)
            })
        )
    }

    sendMessage(message: string) {
        if (this.connection) {
            console.log(`message sent: ${message}`)
            this.connection.next(message)
        }
    }

    disconnect() {
        if (this.connection) {
            this.connection.complete()
            console.log("websocket disconnected")
        }
    }

    getMessages(
        chatId: string,
        size: number = 10,
        lastMessageId?: string
    ): Observable<Message[]> {
        const requestUrl = new URL(`/messages/${chatId}`, this.backendUrl)

        requestUrl.searchParams.set("size", size.toString())
        if (lastMessageId) requestUrl.searchParams.set("lastMessageId", lastMessageId)

        return this.http.get<Message[]>(requestUrl.toString())
    }
}
