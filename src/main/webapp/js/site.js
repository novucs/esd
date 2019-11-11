(function () {
  document.addEventListener('DOMContentLoaded', function () {
    let elems = document.querySelectorAll('.dropdown-trigger');
    M.Dropdown.init(elems, {
      hover: true,
      closeOnClick: true,
      coverTrigger: false
    });
  });
})();
