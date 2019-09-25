/**
 * The user segments are in the global TMA_CONFIG Objekt
 * TMA_CONFIG.user_segments = ["seg1", "seg2"]
 * 
 * Elemente werden unter folgenden Bedingungen angezeigt.
 * 1. 
 */
(function (frontend: any, document : any) {
    var CLASS_HIDDEN : string = "tma-hide";
    var SELECTOR_HIDDEN : string = "." + CLASS_HIDDEN;
    let update = function (selectedSegments : any[]) {
        document.querySelectorAll(SELECTOR_HIDDEN).forEach(function (element: any) {
            element.classList.remove(CLASS_HIDDEN);
        });


        var groups = collectGroups();


        groups.forEach(function (group : string) {
            var matches : any[] = [];
            document.querySelectorAll("[data-tma-group=" + group + "]").forEach(function (element : any) {
                if (element.dataset.tmaPersonalization !== "enabled") {
                    return;
                }
                if (!matchs(element, selectedSegments)) {
                    element.classList.add(CLASS_HIDDEN);
                } else {
                    matches.push(element);
                }
            });
            //console.log(matches);
            // remove the default
            if (matches.length > 1) {
                matches.filter(function (item : any) {
                    return item.dataset.tmaDefault === "yes"
                }).forEach(function (item) {
                    item.classList.add(CLASS_HIDDEN);
                });
            }
        });

    };
    let matchs = function ($element : any, selectedSegments : string[]) {
        if ($element.dataset.tmaDefault === "yes") {
            return true;
        } else if ($element.dataset.tmaMatching === "all") {
            var segments : string[] = $element.dataset.tmaSegments.split(",");
            var matching : boolean = true;
            segments.forEach(function (s : string) {
                if (!selectedSegments.includes(s)) {
                    matching = false;
                }
            });
            return matching;
        } else if ($element.dataset.tmaMatching === "any") {
            var segments : string[] = $element.dataset.tmaSegments.split(",");
            var matching : boolean = false;
            segments.forEach(function (s : string) {
                if (selectedSegments.includes(s)) {
                    matching = true;
                }
            });
            return matching;
        } else if ($element.dataset.tmaMatching === "none") {
            var segments : string[] = $element.dataset.tmaSegments.split(",");
            var matching : boolean = false;
            segments.forEach(function (s : string) {
                if (selectedSegments.includes(s)) {
                    matching = false;
                }
            });
            return matching;
        }
        return false;
    };
    let collectGroups = function () {
        var groups : string[] = [];
        document.querySelectorAll("[data-tma-group]").forEach(function (element : any) {
            var group : string = element.getAttribute("data-tma-group").trim();
            if (!groups.includes(group) && group !== "") {
                groups.push(group);
            }
        });
        return groups;
    };

    frontend.update = update;
}(window.webtools.Frontend = window.webtools.Frontend || {}, document));