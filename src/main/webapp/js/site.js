(function () {
  document.addEventListener('DOMContentLoaded', function () {
    let elems = document.querySelectorAll('.dropdown-trigger');
    elems.forEach((e, i) => {
      try {
        M.Dropdown.init(e, {
          hover: true,
          closeOnClick: true,
          coverTrigger: false,
          constrainWidth: false
        });
      } catch (e) {
        console.warn("Unable to instantiate Dropdown.");
      }
    });
  });
})();
