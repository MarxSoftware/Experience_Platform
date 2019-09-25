window.webtools.Editor.register({
	id: "textblock",
	matchs: function ($element) {
		return $element && webtools.Tools.is($element, ["DIV", "P", "H1", "H2", "H3", "H4", "H5", "H6"]);
	},
	decorate: function ($element) {
		webtools.Context.currentElement = $element;
		$element.dataset.editor = "textblock";

		tinymce.init({
			selector: $element.getSelector(),
			height: 500,
			// toolbar: 'insertfile undo redo | styleselect | bold italic | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | link image',
			inline: true,
			//theme: 'inlite',
			menubar: false,
			forced_root_block: false,
			skin: true
		}).then(function (editor) {
			webtools.Context.currentEditor = tinymce.EditorManager.activeEditor;
		});


	},
	undecorate: function () {
		$element.setAttribute("contenteditable", false);
		webtools.Context.currentEditor.destroy();
	},

	/**
	 * Updates the content created with this editor and returns the previous content
	 */
	updateContent: function (variant) {
		var $element = document.querySelector(variant.selector);
		var originalContent = {
			selector: variant.selector,
			content: $element.innerHTML,
			editor: "textblock"
		};
		//webtools.Context.original.push(originalContent);
		$element.innerHTML = variant.content;

		return originalContent;
	},

	originalContent : function ($element) {
		return {
			selector: $element.getSelector(),
			content: $element.innerHTML,
			editor: "textblock"
		};
	}
});