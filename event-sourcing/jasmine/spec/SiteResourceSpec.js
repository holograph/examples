describe("Site API", function() {

    var resource = new SiteResource();

    it("returns 404 on GET issued against a nonexistent site", function() {
        resource.get(resource.random(), function(status) {
            expect(status).toBe(404);
            done();
        });
    });
    it("returns 404 on GET issued against a deleted site", function() {});
    it("returns 404 on PATCH issued against a nonexistent site", function() {});
    it("returns 404 on DELETE issued against a nonexistent site", function() {});
    it("returns 404 on PUT issued against a nonexistent site", function() {});
    it("returns a correct snapshot on GET", function() {});
    it("respects the version parameter on GET", function() {});
    it("falls back to the latest available snapshot if a nonexistent version is specified on GET", function() {});
    it("returns a snapshot reflecting deleted status on GET with version parameter", function() {});
    it("returns 409 on conflicting PATCH", function() {});
    it("returns 409 on POST when site already exists", function() {});
    it("returns 400 on POST with a restore request against nonexistent target version", function() {});
    it("correctly restores site to specified version on well-formed POST restore request", function() {});

});