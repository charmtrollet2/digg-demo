package org.demo.resources;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.demo.model.User;
import org.demo.services.UserService;
import org.jboss.logging.Logger;

import java.util.List;

import static jakarta.ws.rs.core.Response.Status.CREATED;

@Path("/digg/users")
@ApplicationScoped
public class UserResource {
    final private Logger logger = Logger.getLogger(UserResource.class);

    @Inject
    UserService userService;

    @GET
    public Response getAll(@QueryParam("page") int page, @QueryParam("size") int size) {
        List<User> users = userService.getAll(page, size);
        return Response.ok(users).build();
    }

    @GET
    @Path("/{id}")
    public Response getOne(@PathParam("id")  Long id) {
        return Response.ok(User.findById(id)).build();
    }

    @POST
    public Response create(User user) {
        userService.create(user);
        return Response.status(CREATED).entity(user).build();
    }

    @PATCH
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, User user) {
        logger.debugf("Request to update entity with id [%d]", id);
        userService.update(id, user);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id")  Long id) {
        logger.debugf("Request to delete user with id [%d]", id);
        userService.delete(id);
        return Response.noContent().build();
    }
}
