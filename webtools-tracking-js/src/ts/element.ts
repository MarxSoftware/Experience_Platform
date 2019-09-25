(function (exports, d: any) {
	exports.waitFor = function (selector: string) {
		return new Promise((resolve) => {
			var waitForElement = function () {
				let $element = document.querySelector(selector);
				if ($element) {
					resolve($element);
				} else {
					window.requestAnimationFrame(waitForElement);
				}
			};
			waitForElement();
		})
	};
})(window.webtools.Element = window.webtools.Element || {}, document);