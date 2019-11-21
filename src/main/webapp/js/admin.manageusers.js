(function(){

  init = () => {
    document.addEventListener('DOMContentLoaded', function() {
      const select = document.querySelectorAll('select');
      const instance = M.FormSelect.init(select, {});

      // Rather not have done this but solves an issue with options
      // having loads of white space around their text and glitching out.
      const options = Array.from(document.querySelectorAll("option"));
      options.map(o => {
        o.textContent = o.textContent.trim();
      })

      const dd = instance[0].dropdown;
      const selectableOptions = Array.from(dd.el.nextSibling.children);
      const selectedIndex = selectableOptions.findIndex(x => x.classList.contains("selected"))
      selectableOptions[selectedIndex === 0 ? 1 : 0].click();
      selectableOptions[selectedIndex].click();
    });
  }

  return {
    init
  };

})().init();