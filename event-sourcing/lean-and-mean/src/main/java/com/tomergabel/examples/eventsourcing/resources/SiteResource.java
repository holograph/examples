package com.tomergabel.examples.eventsourcing.resources;

import com.tomergabel.examples.eventsourcing.model.SiteSnapshot;
import com.tomergabel.examples.eventsourcing.service.SiteService;
import io.dropwizard.jersey.PATCH;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.UUID;

@Path("/sites/{id}")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SiteResource {

    private SiteService service;

    public SiteResource(SiteService service) {
        this.service = service;
    }

    @GET
    public Response getSnapshot(@PathParam("id") UUID siteId) throws IOException {
        Optional<SiteSnapshot> result = service.getSnapshot(siteId, null);
        return result
                .filter(snapshot -> !snapshot.getDeleted())
                .map(snapshot -> Response.ok(snapshot).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/versions/{version}")
    public Response getSnapshotAtVersion(@PathParam("id") UUID siteId, @PathParam("version") long version)
            throws IOException
    {
        Optional<SiteSnapshot> result = service.getSnapshot(siteId, version);
        return result
                .map(snapshot -> Response.ok(snapshot).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    public Response create(@PathParam("id") UUID siteId, CreateSiteRequest request)
        throws IOException
    {
        OptionalLong result = service.create(siteId, request.getOwner());
        if (result.isPresent())
            return Response
                    .created(UriBuilder.fromResource(SiteResource.class).build(siteId))
                    .entity(new VersionResponse(result.getAsLong()))
                    .build();
        else
            return Response.status(Response.Status.CONFLICT).build();
    }

    @PUT
    public Response restoreVersion(
            @PathParam("id") UUID siteId,
            RestoreSiteRequest request) throws IOException
    {
        OptionalLong result = service.restore(siteId, request.getUser(), request.getTargetVersion());
        if (result.isPresent())
            return Response.ok(new VersionResponse(result.getAsLong())).build();
        else
            return Response.status(Response.Status.NOT_FOUND).build();
    }

    @DELETE
    public Response deleteSite(
            @PathParam("id") UUID siteId,
            DeleteSiteRequest request) throws IOException
    {
        OptionalLong result = service.delete(siteId, request.getUser());
        if (result.isPresent())
            return Response.ok(new VersionResponse(result.getAsLong())).build();
        else
            return Response.status(Response.Status.NOT_FOUND).build();
    }

    @PATCH
    @Path("/versions/{version}")
    @Consumes("application/json-patch+json")
    public Response updateVersion(
            @PathParam("id") UUID siteId,
            @PathParam("version") long version,
            UpdateSiteRequest request) throws IOException
    {
        // TODO 404 in case site doesn't exist

        OptionalLong result = service.update(siteId, request.getUser(), version, request.getDelta());
        if (result.isPresent())
            return Response.ok(new VersionResponse(result.getAsLong())).build();
        else
            return Response.status(Response.Status.CONFLICT).build();
    }
}
