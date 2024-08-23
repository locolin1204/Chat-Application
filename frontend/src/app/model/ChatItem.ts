import { Chat } from "@app/model/Chat";

// type returned from backend for more info (sender id, chatName)
export interface ChatItem {
  senderId: string,
  chat: Chat,
  chatName: string,
}
