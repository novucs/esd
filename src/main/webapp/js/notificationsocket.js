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
      html: notification.message,
      classes: "yellow accent-3 black-text",
      displayLength: 999999999999999999999999999999,
      activationPercent: 0.3
    });
  }

  send(notification){
    this.socket.send(JSON.stringify(notification.toJSON()));
  }
}