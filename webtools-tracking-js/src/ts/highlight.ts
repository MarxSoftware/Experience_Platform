/**
 * https://codepen.io/malyw/pen/zxKJQQ
 */
(function (highlight: any, document: any) {

    // VARS
    var HIGHLIGHT_CLASS = "webtools-highlight";
    var HIGHLIGHT_ACTIVE_CLASS = "webtools-highlight-is-active";
    var highlightIsActive = false;
    var $highlightedElements : any[] = [];

    var $highlightCanvas : any = null;

    // HIGHLIGHT HELPERS
    highlight.activate = function ($element: any) {
        if (Array.isArray($element)) {
            $highlightedElements = $element
        } else {
            $highlightedElements.push($element);    
        }
        if ($highlightedElements.length === 0) {
            return;
        }
        highlightIsActive = true;
       

        $highlightCanvas = document.createElement("canvas");
        $highlightCanvas.style.position = "absolute";
        $highlightCanvas.id = "webtools_canvas";
        $highlightCanvas.style.top = 0;
        $highlightCanvas.style.left = 0;
        $highlightCanvas.style.width = document.body.clientWidth;
        $highlightCanvas.style.height = document.body.clientHeight;
        $highlightCanvas.style.zIndex = 10000; 
        $highlightCanvas.width  = document.body.clientWidth;
        $highlightCanvas.height = document.body.clientHeight;
        document.body.appendChild($highlightCanvas);

        var context = $highlightCanvas.getContext("2d");
        context.fillStyle = 'black';
        context.globalAlpha = 0.7;
        context.fillRect(0, 0, $highlightCanvas.width, $highlightCanvas.height);
        context.globalAlpha = 1.0;
        context.globalCompositeOperation = 'destination-out';
        $highlightedElements.forEach (function ($e) {
            var elementRect = $e.getBoundingClientRect();
            var offset = getOffset($e);
            // translate to fit into document.body.style.width and document.body.style.height;
            var rect   =  $highlightCanvas.getBoundingClientRect();
            var xMouse =  elementRect.left  - rect.left;
            var yMouse =  elementRect.top  - rect.top;
            context.fillRect(offset.left, offset.top, elementRect.width, elementRect.height);
        });
    }

    function getOffset(el: any) {
        el = el.getBoundingClientRect();
        return {
          left: el.left + window.scrollX,
          top: el.top + window.scrollY
        }
      }

    highlight.deactivate = function () {
        if ($highlightCanvas) {
            $highlightCanvas.remove();
        }

        $highlightedElements = [];
        highlightIsActive = false;
    }
    highlight.is = function () {
        return $highlightedElements.length > 0;
    }

    /*
    webtools.domReady(function () {
        webtools.Tools.on(document, "click", function () {
            if (highlightIsActive > 0) {
                highlight.deactivate();
            }
        })
    });
    */


}(window.webtools.Highlight = window.webtools.Highlight || {}, document));