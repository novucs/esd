(function(){

  const init = () => {
    document.addEventListener('DOMContentLoaded', function() {
      const actionButton = document.querySelectorAll('.fixed-action-btn');
      M.FloatingActionButton.init(actionButton, {
        toolbarEnabled: true
      });

      const tooltips = document.querySelectorAll('.tooltipped');
      M.Tooltip.init(tooltips, {
        margin: 20,
        outDuration: 50
      });
    });
  }

  return {
    init
  }
})().init()