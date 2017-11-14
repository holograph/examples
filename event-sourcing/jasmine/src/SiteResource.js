function SiteResource() {

    var base = "http://localhost:8080/sites/";

    // Low-level HTTP wrappers --

    this.get = function(resource) {
        return fetch(base + resource, { "method": "GET" });
    };

    this.post = function(resource, body) {
        return fetch(base + resource, {
            "method": "POST",
            "body": JSON.stringify(body),
            "headers": new Headers({ "Content-type": "application/json" })
        });
    };
    this.put = function(resource, body) {
        return fetch(base + resource, {
            "method": "PUT",
            "body": JSON.stringify(body),
            "headers": new Headers({ "Content-type": "application/json" })
        });
    };
    this.delete = function(resource, body) {
        return fetch(base + resource, {
            "method": "DELETE",
            "body": JSON.stringify(body),
            "headers": new Headers({ "Content-type": "application/json" })
        });
    };
    this.patch = function(resource, body) {
        return fetch(base + resource, {
            "method": "PATCH",
            "body": JSON.stringify(body),
            "headers": new Headers({ "Content-type": "application/json-patch+json" })
        });
    };

    // Helpers --

    this.random = function() {
        // Source: https://stackoverflow.com/a/2117523/11558
        return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
            var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
            return v.toString(16);
        });
    };

    // High-level driver --

    function retrieveVersion(response) {
        expect(response.ok).toBe(true);
        return response.json().version;
    }

    this.createSite = function(id, owner) {
        return this.post(id, { "owner": owner })
            .then(function(response) {
                expect(response.status).toBe(201);
                return response;
            })
            .then(retrieveVersion);
    };

    this.updateSite = function(id, user, baseVersion, patch) {
        return this.patch(id + "/versions/" + baseVersion, {
            "user": user,
            "delta": patch
        }).then(retrieveVersion);
    };

    this.deleteSite = function(id, user) {
        return this.delete(id, {
            "user": user
        }).then(retrieveVersion);
    };
}
