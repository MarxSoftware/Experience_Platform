interface Context {
    DAY : number;
    HOUR : number;
    MINUTE : number;
    isDNT : boolean;

    site : string;
    page : string;
    host : string;
    uid : string;
    rid : string;
    vid : string;
    pixelImage : any;
    custom_parameter : string[];
    cookieDomain : string;
}
declare namespace webtools {
    let Context: Context;
    let Cookie: any;
    let Tools: any;
}

interface Navigator  {
    msDoNotTrack : string;
}
interface Window {
    webtools: any;
}

(function (webtools, document) {
    webtools.Context = {};
}(window.webtools = window.webtools || {}, document));