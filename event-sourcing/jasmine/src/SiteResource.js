function SiteResource() {

    var base = "http://localhost:8080/sites/";

    // Low-level HTTP wrappers --

    function _get(resource) {
        return fetch(base + resource, { "method": "GET" });
    }
    function _post(resource, body) {
        return fetch(base + resource, {
            "method": "POST",
            "body": JSON.stringify(body),
            "headers": new Headers({ "Content-type": "application/json" })
        });
    }
    function _put(resource, body) {
        return fetch(base + resource, {
            "method": "PUT",
            "body": JSON.stringify(body),
            "headers": new Headers({ "Content-type": "application/json" })
        });
    }
    function _delete(resource, body) {
        return fetch(base + resource, {
            "method": "DELETE",
            "body": JSON.stringify(body),
            "headers": new Headers({ "Content-type": "application/json" })
        });
    }
    function _patch(resource, body) {
        return fetch(base + resource, {
            "method": "PATCH",
            "body": JSON.stringify(body),
            "headers": new Headers({ "Content-type": "application/json-patch+json" })
        });
    }

    // Helpers --

    this.random = function() {
        // Source: https://stackoverflow.com/a/2117523/11558
        return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
            // noinspection EqualityComparisonWithCoercionJS
            var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
            return v.toString(16);
        });
    };

    // High-level driver --

    function assertStatus(status) {
        return function(response) {
            expect(response.status).toBe(status);
            return response;
        }
    }

    function assertOk(response) {
        expect(response.ok).toBe(true);
        return response;

    }

    function retrieveVersion(response) {
        return response.json().then(function(json) { return json.version; });
    }

    this.getSiteRaw = function(id, version) {
        return _get(id + (version ? "/versions/" + version : ""));
    };

    this.getSite = function(id, version) {
        return this.getSiteRaw(id, version).then(function(response) { return response.json(); });
    };

    this.createSiteRaw = function(id, owner) {
        return _post(id, { "owner": owner });
    };

    this.createSite = function(id, owner) {
        return this.createSiteRaw(id, owner)
            .then(assertStatus(201))
            .then(retrieveVersion);
    };

    this.updateSiteRaw = function(id, user, baseVersion, patch) {
        return _patch(id + "/versions/" + baseVersion, {
            "user": user,
            "delta": patch
        });
    };

    this.updateSite = function(id, user, baseVersion, patch) {
        return this.updateSiteRaw(id, user, baseVersion, patch)
            .then(assertOk)
            .then(retrieveVersion);
    };

    this.restoreSiteRaw = function(id, user, targetVersion) {
        return _put(id, {
            "user": user,
            "version": targetVersion
        });
    };

    this.restoreSite = function(id, user, targetVersion) {
        return this.restoreSiteRaw(id, user, targetVersion)
            .then(assertOk)
            .then(retrieveVersion);
    };

    this.deleteSiteRaw = function(id, user) {
        return _delete(id, {
            "user": user
        });
    };

    this.deleteSite = function(id, user) {
        return this.deleteSiteRaw(id, user)
            .then(assertOk)
            .then(retrieveVersion);
    };
}
