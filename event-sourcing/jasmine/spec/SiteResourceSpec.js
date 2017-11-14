describe("Site API", function() {

    var resource = new SiteResource();

    it("returns 404 on GET issued against a nonexistent site", function() {
        return resource
            .get(resource.random())
            .then(function(response) {
                expect(response.status).toBe(404);
            });
    });
    it("returns 404 on GET issued against a deleted site", function() {
        var id = resource.random();
        var owner = resource.random();

        return resource.createSite(id, owner)
            .then(function(ignored) { return resource.deleteSite(id, owner); })
            .then(function(ignored) { return resource.get(id); })
            .then(function(response) {
                expect(response.status).toBe(404);
            });
    });
    it("returns 404 on PATCH issued against a nonexistent site", function(done) {
        pending();
    });
    it("returns 404 on DELETE issued against a nonexistent site", function(done) {
        pending();
    });
    it("returns 404 on PUT issued against a nonexistent site", function(done) {
        pending();
    });
    it("returns a correct snapshot on GET", function(done) {
        pending();
    });
    it("respects the version parameter on GET", function(done) {
        pending();
    });
    it("falls back to the latest available snapshot if a nonexistent version is specified on GET", function(done) {
        pending();
    });
    it("returns a snapshot reflecting deleted status on GET with version parameter", function(done) {
        pending();
    });
    it("returns 409 on conflicting PATCH", function(done) {
        pending();
    });
    it("returns 409 on POST when site already exists", function(done) {
        pending();
    });
    it("returns 400 on POST with a restore request against nonexistent target version", function(done) {
        pending();
    });
    it("correctly restores site to specified version on well-formed POST restore request", function(done) {
        pending();
    });
});