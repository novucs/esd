(function () {

  init = () => {
    document.addEventListener("DOMContentLoaded", function () {
      const tabElement = document.querySelectorAll(".tabs");
      M.Tabs.init(tabElement, { });
    })
  }

  return {
    init
  };

}()).init();