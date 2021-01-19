package io.quarkus.registry.app.endpoints;

import javax.validation.Valid;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.quarkus.registry.app.model.Extension;
import io.quarkus.registry.app.model.Platform;
import io.smallrye.mutiny.Uni;

@Path("/registry")
public class RegistryEndpoint {

    @POST
    @Path("/platform")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Uni<Response> addPlatform(@Valid @BeanParam ArtifactCoords coords) {
        return Platform
                .find("#Platform.findByGroupIdAndArtifactId", coords.groupId, coords.artifactId)
                .singleResult()
                .onItem().transform(e -> Response.status(Response.Status.CONFLICT).build())
                .onFailure().recoverWithItem(() -> Response.noContent().build());

    }

    @POST
    @Path("/extension")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Uni<Response> addExtension(@Valid @BeanParam ArtifactCoords coords) {
        return Uni.createFrom().nothing();
        //        return Extension.find("")
    }

}
