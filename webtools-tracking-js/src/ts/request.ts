(function (request : any, document : any) {

    /**
     * @returns A Promise
     */
    request.get = function (url: string, options: any) {
        options = options || { method: "GET" };
        if (typeof fetch !== "undefined") {
            return fetch(url, options);
        } else {
            return new Promise((resolve : any, reject : any) => {
                let request = new XMLHttpRequest();

                request.open(options.method || 'get', url, true);

                for (let i in options.headers) {
                    request.setRequestHeader(i, options.headers[i]);
                }

                request.withCredentials = options.credentials == 'include';

                request.onload = () => {
                    resolve(response());
                };

                request.onerror = reject;

                request.send(options.body || null);

                function response() {
                    let keys : any[] = [],
                        all : any[] = [],
                        headers : any = {},
                        header : any [];

                    request.getAllResponseHeaders().replace(/^(.*?):[^\S\n]*([\s\S]*?)$/gm, (m, key, value) : any => {
                        keys.push(key = key.toLowerCase());
                        all.push([key, value]);
                        header = headers[key];
                        headers[key] = header ? `${header},${value}` : value;
                    });

                    return {
                        ok: (request.status / 100 | 0) == 2,		// 200-299
                        status: request.status,
                        statusText: request.statusText,
                        url: request.responseURL,
                        clone: response,
                        text: () => Promise.resolve(request.responseText),
                        json: () => Promise.resolve(request.responseText).then(JSON.parse),
                        blob: () => Promise.resolve(new Blob([request.response])),
                        headers: {
                            keys: () => keys,
                            entries: () => all,
                            get: (n : string) => headers[n.toLowerCase()],
                            has: (n : string) => n.toLowerCase() in headers
                        }
                    };
                }
            });
        }
    };


}(window.webtools.Request = window.webtools.Request || {}, document));