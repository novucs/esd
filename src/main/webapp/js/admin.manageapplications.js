(function () {

  let init = () => {
    document.addEventListener('DOMContentLoaded', function () {
      M.FloatingActionButton.init(
          document.querySelectorAll('.fixed-action-btn'), {});

      let registerButton = (buttonId) => {
        const button = document.getElementById(buttonId);
        button.onclick = () => {
          const form = document.getElementById('process-form');
          const element = document.createElement("input");
          element.name = "method";
          element.value = buttonId;
          element.hidden = true;
          form.appendChild(element);
          form.submit();
        };
      };

      registerButton('approve-all');
      registerButton('approve-selection');
      registerButton('deny-selection');
      registerButton('deny-all');
    });
  };

  return {
    init
  };

})().init();
