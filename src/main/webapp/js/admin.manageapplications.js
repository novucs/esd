(function () {

  init = () => {
    document.addEventListener('DOMContentLoaded', function () {
      const select = document.querySelectorAll('select');
      const instance = M.FormSelect.init(select, {});

      // Rather not have done this but solves an issue with options
      // having loads of white space around their text and glitching out.
      const options = Array.from(document.querySelectorAll("option"));
      options.map(o => {
        o.textContent = o.textContent.trim();
      });

      const dd = instance[0].dropdown;
      const selectableOptions = Array.from(dd.el.nextSibling.children);
      const selectedIndex = selectableOptions.findIndex(
          x => x.classList.contains("selected"));
      selectableOptions[selectedIndex === 0 ? 1 : 0].click();
      selectableOptions[selectedIndex].click();
      M.FloatingActionButton.init(
          document.querySelectorAll('.fixed-action-btn'), options);

      function registerButton(buttonId) {
        const button = document.getElementById(buttonId);
        button.onclick = function () {
          const form = document.getElementById('process-form');
          const element = document.createElement("input");
          element.name = "method";
          element.value = buttonId;
          element.hidden = true;
          form.appendChild(element);
          form.submit();
        }
      }

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