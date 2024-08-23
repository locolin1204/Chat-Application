// data type extracted from DB
export interface Chat {
  _id: string
  lastMessage?: string
  lastMessageDate?: Date
  userIds: string[]
}
