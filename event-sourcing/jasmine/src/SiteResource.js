function SiteResource() {

    var base = "http://localhost:8080/sites/";

    function call(resource, method, body, callback) {
        var xhr = new XMLHttpRequest();
        xhr.open(method, base + resource);
        if (body) xhr.setRequestHeader("Content-type", "application/json");
        xhr.addEventListener("load", function() { callback(this.status, this.responseText); });
        xhr.addEventListener("error", function() { callback("network error"); });
        xhr.send(body);
        return xhr;
    }

    this.get = function(resource, callback) {
        call(resource, "GET", null, callback);
    }
    this.post = function(resource, body, callback) {
        call(resource, "POST", JSON.stringify(body), callback);
    }
    this.put = function(resource, body, callback) {
        call(resource, "PUT", JSON.stringify(body), callback);
    }
    this.delete = function(resource, callback) {
        call(resource, "DELETE", null, callback);
    }
    this.patch = function(resource, body, callback) {
        call(resource, "PATCH", JSON.stringify(body, callback));
    }

    this.random = function() {
        // Source: https://stackoverflow.com/a/2117523/11558
        return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
            var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
            return v.toString(16);
        });
    }
}
