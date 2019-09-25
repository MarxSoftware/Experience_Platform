(function (webtools, document) {
    webtools.Context = {};
}(window.webtools = window.webtools || {}, document));
(function (cookie, document) {
    cookie.setCookieDomain = function (domain) {
        webtools.Context.cookieDomain = domain;
    };
    cookie.setCookie = function (cname, cvalue, expire) {
        var d = new Date();
        d.setTime(d.getTime() + expire);
        var expires = "expires=" + d.toUTCString();
        var domain = "";
        if (webtools.Context.cookieDomain) {
            domain = ";domain=" + webtools.Context.cookieDomain;
        }
        document.cookie = cname + "=" + cvalue + "; " + expires + ";path=/" + domain;
    };

    cookie.getCookie = function (cname) {
        if (document.cookie.length > 0) {
            var c_start = document.cookie.indexOf(cname + "=");
            if (c_start !== -1) {
                c_start = c_start + cname.length + 1;
                var c_end = document.cookie.indexOf(";", c_start);
                if (c_end === -1) {
                    c_end = document.cookie.length;
                }
                return unescape(document.cookie.substring(c_start, c_end));
            }
        }
        return "";
    };
}(window.webtools.Cookie = window.webtools.Cookie || {}, document));
(function (tools, document) {
    tools.uuid = function () {
        return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
            var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
            return v.toString(16);
        });
    };

    tools.isArray = function (obj) {
        return !!obj && Array === obj.constructor;
    };

    tools.insertStyle = function (linkUrl) {
        var ss = document.createElement("link");
        ss.type = "text/css";
        ss.rel = "stylesheet";
        ss.href = linkUrl;
        document.getElementsByTagName("head")[0].appendChild(ss);
    };
    tools.insertScript = function (scriptUrl) {
        var ss = document.createElement("script");
        ss.type = "text/javascript";
        ss.src = scriptUrl;
        document.getElementsByTagName("head")[0].appendChild(ss);
    };
    tools.on = function (element, type, callback) {
        element.addEventListener(type, callback);
    };

    tools.is = function ($element, tags) {
        if (tags && Array.isArray(tags)) {
            return tags.some(function (tagname) {
                return $element.tagName === tagname;
            });
        }

        return false;
    };

    tools.getPageInfo = function () {
        var filename = location.pathname.split("/");
        filename = filename[filename.length - 1];

        return {
            domain: location.hostname,
            path: location.pathname,
            page: filename,
            href: location.href,
            id: location.href
        }
    };


    tools.createElement = function (name, attributes) {
        var element = document.createElement(name);
        for (var key in attributes) {
            element.setAttribute(key, attributes[key]);
        }
        return element;
    };


}(window.webtools.Tools = window.webtools.Tools || {}, document));
(function (exports, d) {
	function domReady(fn, context) {

		function onReady(event) {
			d.removeEventListener("DOMContentLoaded", onReady);
			fn.call(context || exports, event);
		}

		function onReadyIe(event) {
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
(function (tracking, document) {

    webtools.Context.isDNT = navigator.doNotTrack == "yes" || navigator.doNotTrack == "1"
        || navigator.msDoNotTrack == "1" || window.doNotTrack == "1";
    webtools.Context.DAY = 24 * 60 * 60 * 1000;
    webtools.Context.HOUR = 60 * 60 * 1000;
    webtools.Context.MINUTE = 60 * 1000;

    tracking.init = function (host, site, page) {
        webtools.Context.site = site;
        webtools.Context.page = page;
        webtools.Context.host = host;
        webtools.Context.uid = "";			// the userid
        webtools.Context.rid = "";			// the requestid
        webtools.Context.vid = "";			// the visitid
        webtools.Context.pixelImage = new Image();
    };
    tracking.page = function (page) {
        webtools.Context.page = page;
    };
    tracking.customParameters = function (customParameters) {
        webtools.Context.custom_parameter = customParameters;
    };
    tracking.setCookieDomain = function (domain) {
        webtools.Context.cookieDomain = domain;
    };
    tracking.optOut = function () {
        webtools.Context.setCookie('_tma_trackingcookie', "opt-out", 365 * TMA.DAY);
    };
    tracking.dnt = function () {
        return webtools.Context.isDNT || document.cookie.indexOf("_tma_trackingcookie=opt-out") !== -1;
    }

    createDefaultParameters = function () {
        webtools.Context.rid = getUniqueID("_tma_rid", 3 * webtools.MINUTE);
        webtools.Context.vid = getUniqueID("_tma_vid", 1 * webtools.HOUR);
        webtools.Context.uid = getUniqueID("_tma_uid", 365 * webtools.DAY);
        var currentDate = new Date();
        return "&site=" + webtools.Context.site
            + "&page=" + webtools.Context.page
            + "&uid=" + webtools.Context.uid
            + "&reqid=" + webtools.Context.rid
            + "&vid=" + webtools.Context.vid
            + "&referrer=" + escape(document.referrer)
            + "&offset=" + currentDate.getTimezoneOffset()
            + "&_t=" + currentDate.getTime();
    };

    createCustomParameters = function () {
        var customParameterString = "";
        //var name = arguments.length === 1 ? arguments[0] + "_" : "";

        if (webtools.Context.custom_parameter) {
            var customParameters = webtools.Context.custom_parameter;
            if (customParameters !== null && typeof customParameters === 'object') {
                for (var p in customParameters) {
                    if (customParameters.hasOwnProperty(p)) {
                        var value = customParameters[p]
                        if (Array.isArray(value)) {
                            for (var item in value) {
                                customParameterString += "&c_" + p + '=' + value[item];
                            }
                        } else {
                            customParameterString += "&c_" + p + '=' + customParameters[p];
                        }
                    }
                }
            }
        }
        return customParameterString;
    };
    getUniqueID = function (cookiename, expire) {
        var aid = webtools.Cookie.getCookie(cookiename);
        if (aid === null || aid === "") {
            aid = webtools.Tools.uuid();
        }
        // update cookie on every request
        webtools.Cookie.setCookie(cookiename, aid, expire);
        return aid;
    };
    

    tracking.register = function () {
        // opt-out cookie is not set
        // incude tracking pixle
        // user id
        //webtools.Context.uid = getUniqueID("_tma_uid", 365 * webtools.Context.DAY);
        // visit id
        //webtools.Context.vid = getUniqueID("_tma_vid", 1 * webtools.Context.HOUR);
        // new requestid for every request
        //webtools.Context.rid = webtools.Tools.uuid();
        //webtools.Cookie.setCookie("_tma_rid", webtools.Context.rid, 3 * webtools.Context.MINUTE);

        tracking.track("pageview");
    };

    send = function (url, data) {
        if (navigator.sendBeacon) {
            navigator.sendBeacon(url, data);
        } else {
            webtools.Context.pixelImage.src = url + "?" + data;
        }
    }

    tracking.track = function (event) {
        if (!tracking.dnt()) {
            // opt-out cookie is not set
            var data = "event=" + event + createDefaultParameters() + createCustomParameters();
            send(webtools.Context.host + "/pixel", data);
            /*if (navigator.sendBeacon) {
                var data = "event=" + event + createDefaultParameters() + createCustomParameters();
                navigator.sendBeacon(webtools.Context.host + "/pixel", data);
            } else {
                webtools.Context.pixelImage.src = webtools.Context.host + "/pixel?event=" + event + createDefaultParameters() + createCustomParameters();
            }*/
        }
    };

    tracking.score = function (scores) {
        if (!tracking.dnt()) {
            var scoreParameters = "";
            for (var key in scores) {
                scoreParameters += "&score_" + key + "=" + scores[key];
            }
            // opt-out cookie is not set
            //webtools.Context.pixelImage.src = webtools.Context.host + "/pixel?event=score" + scoreParameters + createDefaultParameters() + createCustomParameters();

            var data = "event=score" + scoreParameters + createDefaultParameters() + createCustomParameters();
            send(webtools.Context.host + "/pixel", data);
        }
    };

}(window.webtools.Tracking = window.webtools.Tracking || {}, document));