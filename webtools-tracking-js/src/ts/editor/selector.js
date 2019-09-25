(function (selector, document) {

    var elements;
    var selectedElement;

    var eventTarget = document.createTextNode(null);

    function init() {
        var elemDiv = document.createElement('div');
        elemDiv.setAttribute("id", "webtools-selector");

        var selector = document.createElement('div');
        selector.setAttribute("id", "webtools-selector-top");
        elemDiv.appendChild(selector);
        selector = document.createElement('div');
        selector.setAttribute("id", "webtools-selector-left");
        elemDiv.appendChild(selector);
        selector = document.createElement('div');
        selector.setAttribute("id", "webtools-selector-right");
        elemDiv.appendChild(selector);
        selector = document.createElement('div');
        selector.setAttribute("id", "webtools-selector-bottom");
        elemDiv.appendChild(selector);

        document.body.appendChild(elemDiv);

        elements = {
            top: document.getElementById('webtools-selector-top'),
            left: document.getElementById('webtools-selector-left'),
            right: document.getElementById('webtools-selector-right'),
            bottom: document.getElementById('webtools-selector-bottom')
        };
    }

    function destroy() {
        document.querySelector("div#webtools-selector").remove();
        elements = null;
        selectedElement = null;
    }

    function select_element_mouseover(event) {
        if (!isSelectable(event.target)) {
            return;
        }

        var target = event.target;
        targetOffset = target.getBoundingClientRect(),
            targetHeight = targetOffset.height,
            targetWidth = targetOffset.width;

        elements.top.style.left = targetOffset.left - 4 + "px";
        elements.top.style.top = targetOffset.top - 4 + "px";
        elements.top.style.width = targetWidth + 5 + "px";

        elements.bottom.style.top = (targetOffset.top + targetHeight + 1) + "px";
        elements.bottom.style.left = (targetOffset.left - 3) + "px";
        elements.bottom.style.width = (targetWidth + 4) + "px";

        elements.left.style.left = (targetOffset.left - 5) + "px";
        elements.left.style.top = (targetOffset.top - 4) + "px";
        elements.left.style.height = (targetHeight + 8) + "px";

        elements.right.style.left = (targetOffset.left + targetWidth + 1) + "px";
        elements.right.style.top = (targetOffset.top - 4) + "px";
        elements.right.style.height = (targetHeight + 8) + "px";
    }
    function select_element_click(event) {
        event.preventDefault();
        if (!isSelectable(event.target)) {
            return;
        }
        var $target = event.target;
        $target.dataset.editor = webtools.Editor.getEditor($target).id;
        var selectEvent = new CustomEvent("select", {
            detail: {
                selected: $target,
                editor: webtools.Editor.getEditor($target)
            }
        });
        eventTarget.dispatchEvent(selectEvent);
        // console.log("try ot dispatch: ", selectEvent);
        console.log($target.getSelector());
    }

    function checkInternalElements($element) {

    }

    function findInTree($e, filter) {
        if (!$e) {
            return false;
        }
        if (filter($e)) {
            return true;
        }
        return findInTree($e.parentElement, filter);
    }

    function isExtJsElement($element) {
        var filter = function ($e) {
            var id = $e.getAttribute("id");
            /*
             && (
                id.startsWith("variantWindow")
                || id.startsWith("experienceWindow")
                || id.startsWith("messagebox-")
                || id.startsWith("ext-element")
            )
            */
            if (id && id === "webtools-ui") {
                return true;
            }
            return false;
        }
        return findInTree($element, filter);
    }

    function isAlreadySelected ($element) {
        var filter = function ($e) {
            return $e.hasAttribute("data-editor");
        };

        return findInTree($element, filter);
    }
    function hasAlreadySelectedChildren ($element) {
        return $element.querySelectorAll("[data-editor]").length > 0;
    }
    /**
     * check if the element is selectable
     * 1. no extjs element
     * 2. not menu
     * 3. has registered editor
     * 4. not already selected
     * 
     * @param {*}  
     */
    function isSelectable($element) {
        return ($element.id.indexOf('webtools-selector') === -1)
            && webtools.Editor.hasEditor($element)
            && !isAlreadySelected($element)
            && !hasAlreadySelectedChildren($element)
            && !isExtJsElement($element);
    }

    function handleEscape(evt) {
        evt = evt || window.event;
        if (evt.keyCode == 27) {
            window.webtools.Selector.stopInspector();
            var selectEvent = new CustomEvent("cancel-selection", {});
            eventTarget.dispatchEvent(selectEvent);
        }
    }


    selector.addEventListener = eventTarget.addEventListener.bind(eventTarget);
    selector.dispatchEvent = eventTarget.dispatchEvent.bind(eventTarget);
    selector.removeEventListener = eventTarget.removeEventListener.bind(eventTarget);

    selector.startInspector = function () {
        init();
        window.addEventListener("mousemove", select_element_mouseover);
        window.addEventListener("click", select_element_click);
        window.addEventListener("keydown", handleEscape);

        var THAT = this;
        return new Promise(function (resolve, reject) {
            THAT.addEventListener("select", function (e) {
                resolve(e.detail);
            });
            THAT.addEventListener("cancel-selection", function (e) {
                reject();
            });
        });
    };
    selector.stopInspector = function () {
        destroy();
        window.removeEventListener("mousemove", select_element_mouseover);
        window.removeEventListener("click", select_element_click);
        window.removeEventListener("keydown", handleEscape);
    };

    selector.getSelectedElement = function () {
        return selectedElement;
    };

    selector.getSelector = function (element) {
        /*
        requires css-selector-tools
        */
        return element.getSelector();
    };
    selector.getElement = function (selector) {
        return document.querySelector(selector);
    };


}(window.webtools.Selector = window.webtools.Selector || {}, document));