(function () {

  init = () => {
    document.addEventListener("DOMContentLoaded", function () {
      const tabElement = document.querySelectorAll(".tabs");
      M.Tabs.init(tabElement, { });

      const elems = document.querySelectorAll('select');
      M.FormSelect.init(elems, {});
    });
  };

  return {
    init
  };

}()).init();