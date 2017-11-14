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

    it("respects the version parameter on GET", function() {
        const id = resource.random();
        const owner = resource.random();

        const patch = [{ op: "add", path: "/test", value: "ok" }];

        var version;
        return resource.createSite(id, owner)
            .then(function(v0) {
                version = v0;
                return resource.updateSite(id, owner, v0, patch);
            })
            .then(function(ignored) { return resource.getSite(id, version); })
            .then(function(snapshot) {
                expect(snapshot.siteId).toBe(id);
                expect(snapshot.version).toBe(version);
                expect(snapshot.owner).toBe(owner);
                expect(snapshot.blob).toEqual({});
                expect(snapshot.deleted).toBe(false);
            });
    });

    it("falls back to the latest available snapshot if a nonexistent version is specified on GET", function() {
        const id = resource.random();
        const owner = resource.random();

        const patch = [{ op: "add", path: "/test", value: "ok" }];

        var version;
        return resource.createSite(id, owner)
            .then(function(v0) { return resource.updateSite(id, owner, v0, patch); })
            .then(function(v1) {
                version = v1;
                return resource.getSite(id, 500 /* Some ridiculous future version */);
            })
            .then(function(snapshot) {
                expect(snapshot.siteId).toBe(id);
                expect(snapshot.version).toBe(version);
                expect(snapshot.owner).toBe(owner);
                expect(snapshot.blob).toEqual({ "test": "ok" });
                expect(snapshot.deleted).toBe(false);
            });
    });

    it("returns a snapshot reflecting deleted status on GET with version parameter", function() {
        const id = resource.random();
        const owner = resource.random();

        var version;
        return resource.createSite(id, owner)
            .then(function(ignored) { return resource.deleteSite(id, owner); })
            .then(function(v1) {
                version = v1;
                return resource.getSite(id, version);
            })
            .then(function(snapshot) {
                expect(snapshot.siteId).toBe(id);
                expect(snapshot.version).toBe(version);
                expect(snapshot.owner).toBe(owner);
                expect(snapshot.blob).toEqual({});
                expect(snapshot.deleted).toBe(true);
            });
    });

    it("returns 409 on conflicting PATCH", function() {
        const id = resource.random();
        const owner = resource.random();

        const patch1 = [{ op: "add", path: "/test", value: "ok" }];
        const patch2 = [{ op: "add", path: "/test", value: "ko" }];

        var base;
        return resource.createSite(id, owner)
            .then(function(v0) {
                base = v0;
                return resource.updateSite(id, owner, v0, patch1);
            })
            .then(function(ignored) {
                return resource.updateSiteRaw(id, owner, base, patch2);
            })
            .then(expectStatus(409));
    });

    it("returns 409 on POST when site already exists", function() {
        const id = resource.random();
        const owner = resource.random();

        return resource.createSite(id, owner)
            .then(function(ignored) { return resource.createSiteRaw(id, owner); })
            .then(expectStatus(409));
    });

    it("returns 400 on POST with a restore request against nonexistent target version", function() {
        // const id = resource.random();
        // const owner = resource.random();
        //
        // return resource.createSite(id, owner)
        //     .then(function(ignored) {
        //         return resource.restoreSiteRaw(id, owner, 500 /* Some ridiculous future version */);
        //     })
        //     .then(expectStatus(400));

        pending("Not actually implemented by the server yet. Boo!")
    });

    it("correctly restores site to specified version on well-formed POST restore request", function() {
        const id = resource.random();
        const owner = resource.random();

        const patch = [{ op: "add", path: "/test", value: "ok" }];

        var version;
        return resource.createSite(id, owner)
            .then(function(v0) {
                version = v0;
                return resource.updateSite(id, owner, v0, patch);
            })
            .then(function(ignored) {
                return resource.restoreSite(id, owner, version);
            })
            .then(function(v2) {
                version = v2;
                return resource.getSite(id)
            })
            .then(function(snapshot) {
                expect(snapshot.siteId).toBe(id);
                expect(snapshot.version).toBe(version);
                expect(snapshot.owner).toBe(owner);
                expect(snapshot.blob).toEqual({} /* Prior to initial patch */);
                expect(snapshot.deleted).toBe(false);
            });
    });

    // TODO
    //   - Introduce proper response matchers (instead of the mess above)
    //   - Introduce consistent data variables (patch, expected snapshot...) for clarity
});