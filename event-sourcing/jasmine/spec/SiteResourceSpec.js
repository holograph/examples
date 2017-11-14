describe("Site API", function() {

    const resource = new SiteResource();

    function expectStatus(status) {
        return function(response) {
            expect(response.status).toBe(status);
        }
    }

    it("returns 404 on GET issued against a nonexistent site", function() {
        return resource
            .getSiteRaw(resource.random())
            .then(expectStatus(404));
    });

    it("returns 404 on GET issued against a deleted site", function() {
        const id = resource.random();
        const owner = resource.random();

        return resource.createSite(id, owner)
            .then(function(ignored) { return resource.deleteSite(id, owner); })
            .then(function(ignored) { return resource.getSiteRaw(id); })
            .then(expectStatus(404));
    });

    it("returns 404 on PATCH issued against a nonexistent site", function() {
        pending("This is not actually implemented this way on the server yet. Boo!");
    });

    it("returns 404 on DELETE issued against a nonexistent site", function() {
        return resource
            .deleteSiteRaw(resource.random(), resource.random())
            .then(function(response) {
                expect(response.status).toBe(404); 
            });
    });

    it("returns 404 on PUT issued against a nonexistent site", function() {
        return resource
            .restoreSiteRaw(resource.random(), resource.random(), 15)
            .then(expectStatus(404));
    });

    it("returns a correct snapshot on GET", function() {
        const id = resource.random();
        const owner = resource.random();

        const patch = [{ op: "add", path: "/test", value: "ok" }];

        var version;
        return resource.createSite(id, owner)
            .then(function(v0) {
                return resource.updateSite(id, owner, v0, patch);
            })
            .then(function(v1) {
                version = v1;
                return resource.getSite(id);
            })
            .then(function(snapshot) {
               expect(snapshot.siteId).toBe(id);
               expect(snapshot.version).toBe(version);
               expect(snapshot.owner).toBe(owner);
               expect(snapshot.blob).toEqual({ "test": "ok" });
               expect(snapshot.deleted).toBe(false);
            });
    });

    // TODO --

    it("respects the version parameter on GET", function() {
        pending();
    });
    it("falls back to the latest available snapshot if a nonexistent version is specified on GET", function() {
        pending();
    });
    it("returns a snapshot reflecting deleted status on GET with version parameter", function() {
        pending();
    });
    it("returns 409 on conflicting PATCH", function() {
        pending();
    });
    it("returns 409 on POST when site already exists", function() {
        pending();
    });
    it("returns 400 on POST with a restore request against nonexistent target version", function() {
        pending();
    });
    it("correctly restores site to specified version on well-formed POST restore request", function() {
        pending();
    });
});