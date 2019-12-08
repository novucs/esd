(function() {
  let copyText = (text) => {
    let ele = document.createElement("textarea");
    ele.value = text;
    document.body.appendChild(ele);
    ele.select();
    document.execCommand("copy");
    document.body.removeChild(ele);
  };

  let selectElement = (e) => {
    if (e.target.className === "c-pointer") {
      copyText(e.target.children[0].innerText);
    } else {
      copyText(e.target.innerText);
    }
    M.toast({html: 'Copied!'});
  };

  document.addEventListener('DOMContentLoaded', () => {
    let copyAreas = document.querySelectorAll(".c-pointer");
    copyAreas.forEach((e, i) =>
        e.addEventListener("click", (e) => selectElement(e)));
  });
})();