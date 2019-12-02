class Notification{

  constructor(message, recipientId, senderId){
    this.message = message;
    this.recipientId = recipientId;
    this.senderId = senderId;
  }

  toJSON(){
    return {
      message: this.message,
      recipientId: this.recipientId,
      senderId: this.senderId
    }
  }
}
