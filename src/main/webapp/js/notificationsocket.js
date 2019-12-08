class NotificationSocket {

  constructor(endpoint, userId){
    this.endpoint = endpoint;
    this.userId = userId;
    this.socket = new WebSocket(this.endpoint + this.userId);
    this.socket.onmessage = this.onmessage;
  }

  onmessage(event){
    const notification = JSON.parse(event.data);
    let notificationColour = "";
    switch(notification.type.toLowerCase()){
      case "success":
        notificationColour = "green";
        break;
      case "alert":
        notificationColour = "yellow accent-3";
        break;
      case "warning":
        notificationColour = "red accent-3";
        break;
      default:
        notificationColour = "blue";
        break
    }

    M.toast({
      html: notification.message,
      classes:  `${notificationColour} black-text`,
      displayLength: 9999,
      activationPercent: 0.3
    });
  }

  send(notification){
    this.socket.send(JSON.stringify(notification.toJSON()));
  }
}