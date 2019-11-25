class NotificationSocket {

  constructor(endpoint, userId){
    this.endpoint = endpoint;
    this.userId = userId;
    this.socket = new WebSocket(this.endpoint + this.userId);
    this.socket.onmessage = this.onmessage;
  }

  onmessage(event){
    const notification = JSON.parse(event.data);
    M.toast({
      html: notification.message
    });
  }

  send(notification){
    this.socket.send(JSON.stringify(notification.toJSON()));
  }
}