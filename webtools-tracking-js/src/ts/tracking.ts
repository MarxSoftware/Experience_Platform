(function (tracking: any, document: any) {

    webtools.Context.isDNT = navigator.doNotTrack == "yes" || navigator.doNotTrack == "1"
        || navigator.msDoNotTrack == "1" || window.doNotTrack == "1";
    webtools.Context.DAY = 24 * 60 * 60 * 1000;
    webtools.Context.HOUR = 60 * 60 * 1000;
    webtools.Context.MINUTE = 60 * 1000;

    tracking.init = function (host : string, site : string, page : string) {
        webtools.Context.site = site;
        webtools.Context.page = page;
        webtools.Context.host = host;
        webtools.Context.uid = "";			// the userid
        webtools.Context.rid = "";			// the requestid
        webtools.Context.vid = "";			// the visitid
        webtools.Context.pixelImage = new Image();
    };
    tracking.page = function (page: string) {
        webtools.Context.page = page;
    };
    tracking.customParameters = function (customParameters : string []) {
        webtools.Context.custom_parameter = customParameters;
    };
    tracking.setCookieDomain = function (domain: string) {
        webtools.Cookie.setCookieDomain(domain);
    };
    tracking.optOut = function () {
        webtools.Cookie.set('_tma_trackingcookie', "opt-out", 365 * webtools.Context.DAY);
    };
    tracking.dnt = function () {
        return webtools.Context.isDNT || document.cookie.indexOf("_tma_trackingcookie=opt-out") !== -1;
    }

    let createDefaultParameters = function () {
        webtools.Context.rid = getUniqueID("_tma_rid", 3 * webtools.Context.MINUTE);
        webtools.Context.vid = getUniqueID("_tma_vid", 1 * webtools.Context.HOUR);
        webtools.Context.uid = getUniqueID("_tma_uid", 365 * webtools.Context.DAY, window.localStorage);
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

    let createCustomParameters = function () {
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
    let getUniqueID = function (cookiename : string, expire : number, uidStorage?: any) {
        var aid = webtools.Cookie.get(cookiename);
        if (aid === null || aid === "") {  
            if (uidStorage && uidStorage.getItem(cookiename) !== null) {
                aid = localStorage.getItem(cookiename);
            } else {
                aid = webtools.Tools.uuid();
            }
        }
        if (uidStorage) {
            uidStorage.setItem(cookiename, aid);
        }
        // update cookie on every request
        webtools.Cookie.set(cookiename, aid, expire);
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

    let send = function (url: string, data: any) {
        if (navigator.sendBeacon) {
            navigator.sendBeacon(url, data);
        } else if (XMLHttpRequest) {
            var xhr = new XMLHttpRequest();
            xhr.open("POST", url, true);
            xhr.send(data);
        } else {
            webtools.Context.pixelImage.src = url + "?" + data;
        }
    }

    tracking.track = function (event : string) {
        if (!tracking.dnt()) {
            // opt-out cookie is not set
            var data = "event=" + event + createDefaultParameters() + createCustomParameters();
            send(webtools.Context.host + "/tracking/pixel", data);
        }
    };

    tracking.score = function (scores : any) {
        if (!tracking.dnt()) {
            var scoreParameters = "";
            for (var key in scores) {
                scoreParameters += "&score_" + key + "=" + scores[key];
            }
            var data = "event=score" + scoreParameters + createDefaultParameters() + createCustomParameters();
            send(webtools.Context.host + "/tracking/pixel", data);
        }
    };

}(window.webtools.Tracking = window.webtools.Tracking || {}, document));