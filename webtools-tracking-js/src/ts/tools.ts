(function (tools, document) {
    tools.uuid = function () {
        return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
            var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
            return v.toString(16);
        });
    };

    tools.isArray = function (obj: any) {
        return !!obj && Array === obj.constructor;
    };

    tools.insertStyle = function (linkUrl: string) {
        var ss = document.createElement("link");
        ss.type = "text/css";
        ss.rel = "stylesheet";
        ss.href = linkUrl;
        document.getElementsByTagName("head")[0].appendChild(ss);
    };
    tools.insertScript = function (scriptUrl: string) {
        var ss = document.createElement("script");
        ss.type = "text/javascript";
        ss.src = scriptUrl;
        document.getElementsByTagName("head")[0].appendChild(ss);
    };
    tools.on = function ($element: any, type: string, callback: any) {
        $element.addEventListener(type, callback);
    };

    tools.wait = function (millis: number) {
        return new Promise((r, j)=>setTimeout(r, millis));
    };

    tools.is = function ($element: any, tags: []) {
        if (tags && Array.isArray(tags)) {
            return tags.some(function (tagname) {
                return $element.tagName === tagname;
            });
        }

        return false;
    };

    tools.getPageInfo = function () {
        var filenames = location.pathname.split("/");
        var filename = filenames[filenames.length - 1];

        return {
            domain: location.hostname,
            path: location.pathname,
            page: filename,
            href: location.href,
            id: location.href
        }
    };


    tools.createElement = function (name : string, attributes: [string, string]) {
        var element = document.createElement(name);
        for (var key in attributes) {
            element.setAttribute(key, attributes[key]);
        }
        return element;
    };


}(window.webtools.Tools = window.webtools.Tools || {}, document));