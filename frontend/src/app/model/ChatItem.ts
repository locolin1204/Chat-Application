import { Chat } from "@app/model/Chat";

export interface ChatItem {
  senderId: string,
  chat: Chat,
  chatName: string,
}
