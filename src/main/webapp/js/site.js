const notificationSocket = new NotificationSocket(
    "ws://localhost:8080/app/notifications/", userId);

(function () {
  document.addEventListener('DOMContentLoaded', function () {
    let elems = document.querySelectorAll('.dropdown-trigger');
    let modalElm = document.querySelectorAll('.modal');
    elems.forEach((e, i) => {
      try {
        M.Dropdown.init(e, {
          hover: true,
          closeOnClick: true,
          coverTrigger: false,
          constrainWidth: false
        });
        M.Modal.init(modalElm);
      } catch (e) {
        console.warn("Unable to instantiate Dropdown.");
      }
    });
  });
})();



