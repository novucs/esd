(function () {
  document.addEventListener('DOMContentLoaded', function () {
    let elems = document.querySelectorAll('.dropdown-trigger');
    let modalElm = document.querySelectorAll('.modal');
    let datePickerElm = document.querySelectorAll('.datepicker');
    elems.forEach((e, i) => {
      try {
        M.Dropdown.init(e, {
          hover: true,
          closeOnClick: true,
          coverTrigger: false,
          constrainWidth: false
        });
        M.Modal.init(modalElm);
        M.Datepicker.init(datePickerElm);
      } catch (e) {
        console.warn("Unable to instantiate Dropdown.");
      }
    });
  });
})();
