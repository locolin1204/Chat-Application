import { Injectable } from '@angular/core';
import { User } from "@app/model/User";
import { map, Observable, switchMap, take, tap } from "rxjs";
import { UsersService } from "@app/service/users.service";
import { Chat } from "@app/model/Chat";
import { environment } from "../../environments/environment.development";
import { HttpClient } from "@angular/common/http";
import { ChatItem } from "@app/model/ChatItem";

@Injectable({
  providedIn: 'root'
})
export class ChatService {

  constructor(
    private usersService: UsersService,
    private http: HttpClient
  ) {
  }

  private backendUrl = environment.backendUrl;

  createChat(receiver: User): Observable<Chat> {
    return this.usersService.getCurrentUser().pipe(
      switchMap(user => {
          const requestBody = { userIds: [user._id, receiver._id] }
          return this.http.post<Chat>(`${this.backendUrl}/chat`, requestBody)
        }
      )
    )
  }

  getAllChats(): Observable<ChatItem[]> {
    return this.usersService.getCurrentUser().pipe(
      switchMap(user => {
        return this.http.get<ChatItem[]>(`${this.backendUrl}/chats/${user._id}`).pipe(
          tap(_ => console.log(`fetched chats`)))
      })
    )
  }

  getReceiver(chat: Chat, senderId: string): Observable<User> {
    const result = chat.userIds.filter((id) => senderId != id)
    return this.usersService.getUserById(result[0])
  }

  isExistingChat(receiverId: string): Observable<string | null> {
    return this.getAllChats().pipe(
      take(1),
      map(chatItems => {
        const existingChat = chatItems.find(chatItem => chatItem.chat.userIds.includes(receiverId))
        return existingChat ? existingChat.chat._id : null;
      })
    )
  }


}
