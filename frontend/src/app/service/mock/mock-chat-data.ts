import { Chat } from "@app/model/Chat";
import { users } from "@app/service/mock-user-data";

export const sampleChat: Chat[] = [{
  _id: 'c1',
  userIds: ['3', '1'],
  // users: [users[2], users[0]],
  lastMessage: 'Hello!',
  lastMessageDate: new Date(),
  // chatName: 'Tay'
},
  {
    _id: 'c2',
    userIds: ['3', '2'],
    // users: [users[2], users[1]],
    lastMessage: 'Bye!',
    lastMessageDate: new Date(),
    // chatName: 'Giroud'
  }
]
