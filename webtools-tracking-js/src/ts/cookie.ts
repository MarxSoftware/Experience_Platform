(function (cookie: any, document : any) {
    cookie.setCookieDomain = function (domain : string) {
        webtools.Context.cookieDomain = domain;
    };
    cookie.set = function (cname : string, cvalue : string, expire : number) {
        var d : Date = new Date();
        d.setTime(d.getTime() + expire);
        var expires : string = "expires=" + d.toUTCString();
        var domain :string = "";
        if (webtools.Context.cookieDomain) {
            domain = ";domain=" + webtools.Context.cookieDomain;
        }
        document.cookie = cname + "=" + cvalue + "; " + expires + ";path=/" + domain;
    };

    cookie.get = function (cname : string) {
        if (document.cookie.length > 0) {
            var c_start : number = document.cookie.indexOf(cname + "=");
            if (c_start !== -1) {
                c_start = c_start + cname.length + 1;
                var c_end : number = document.cookie.indexOf(";", c_start);
                if (c_end === -1) {
                    c_end = document.cookie.length;
                }
                return unescape(document.cookie.substring(c_start, c_end));
            }
        }
        return "";
    };

    cookie.remove = function (name : string) {   
        document.cookie = name+'=; Max-Age=-99999999;';  
    }
}(window.webtools.Cookie = window.webtools.Cookie || {}, document));