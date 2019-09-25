(function( editor, document ) {
    //Private Property
    var editors = [];

    editor.register = function (editor) {
        editors.push(editor);
    };

    editor.hasEditor = function ($element) {
        var $editedParent = $element.closest("[data-editor]");
        if ($editedParent && $element !== $editedParent) {
            return false;
        } 
        return editors.filter(function (editor) {
            return editor.matchs($element);
        }).length >= 1;
    };

    editor.getEditorById = function (editorId) {
        var matchingEditors = editors.filter(function (editor) {
            return editor.id === editorId;
        });
        if (matchingEditors.length >= 1) {
            return matchingEditors[0];
        }
        return false;
    };

    editor.getEditor = function ($element) {
        var matchingEditors = editors.filter(function (editor) {
            return editor.matchs($element);
        });
        if (matchingEditors.length >= 1) {
            return matchingEditors[0];
        }
        return false;
    };

    editor.decorate = function ($element) {
        var editor;
        if ($element.dataset.editor) {
            var matchingEditors = editors.filter(function (editor) {
                return editor.id === $element.dataset.editor;
            }); 
            if (matchingEditors.length >= 1) {
                editor = matchingEditors[0];
            }   
        } else {
            editor = this.getEditor($element);
        }
        if (editor) {
            editor.decorate($element);
        }

    };
    editor.undecorate = function ($element) {
        var editor;
        if (!$element){
            return;
        }
        if ($element.dataset.editor) {
            var matchingEditors = editors.filter(function (editor) {
                return editor.id === $element.dataset.editor;
            }); 
            if (matchingEditors.length >= 1) {
                editor = matchingEditors[0];
            }   
        } else {
            editor = this.getEditor($element);
        }
        if (editor) {
            editor.undecorate();
        }

    };

    editor.addEditorAttributes = function (content) {
        if (!content) {
            return;
        }
        content.forEach(function (item) {
            document.querySelector(item.selector).dataset.editor = item.editor;
        });
    };
    editor.removeEditorAttributes = function (content) {
        content.forEach(function (item) {
            editor.removeEditorAttributesFromElement(document.querySelector(item.selector));
        });
    };
    editor.removeEditorAttributesBySelector = function(selector) {
        editor.removeEditorAttributesFromElement(document.querySelector(selector));
    }
    editor.removeEditorAttributesFromElement = function ($element) {
        if ($element) {
            delete $element.dataset.editor;
        }
    };
    editor.removeAllEditorAttributes = function () {
        document.querySelectorAll("[data-editor]").forEach(function ($item) {
            editor.removeEditorAttributesFromElement($item);
        });
    }

}( window.webtools.Editor = window.webtools.Editor || {}, document));