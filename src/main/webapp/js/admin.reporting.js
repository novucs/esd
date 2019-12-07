(function(){
  document.addEventListener('DOMContentLoaded', function() {
    var elems = document.querySelectorAll('.datepicker');

    setDefaultDate = true;
    if (elems[0].value && elems[1].value) {
      console.log(elems[0].value);
      setDefaultDate = false;
    }

    var instances = M.Datepicker.init(elems, {
      format: "dd-mm-yy",
      maxDate: new Date(),
      autoClose: true,
      defaultDate: new Date(),
      setDefaultDate: setDefaultDate
    });
  });

})()