(function (exports, d: any) {
	function domReady(fn: any, context: any) {

		function onReady(event: string) {
			d.removeEventListener("DOMContentLoaded", onReady);
			fn.call(context || exports, event);
		}

		function onReadyIe(event: string) {
			if (d.readyState === "complete") {
				d.detachEvent("onreadystatechange", onReadyIe);
				fn.call(context || exports, event);
			}
		}

		d.addEventListener && d.addEventListener("DOMContentLoaded", onReady) ||
			d.attachEvent && d.attachEvent("onreadystatechange", onReadyIe);
	}

	exports.domReady = domReady;
})(window.webtools = window.webtools || {}, document);