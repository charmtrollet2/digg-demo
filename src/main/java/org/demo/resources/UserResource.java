package org.demo.resources;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.quarkus.hibernate.reactive.panache.PanacheQuery;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.*;
import org.demo.model.User;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestQuery;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.List;

import static jakarta.ws.rs.core.Response.Status.*;

@Path("/digg/users")
@ApplicationScoped
public class UserResource {
    final private Logger logger = Logger.getLogger(UserResource.class);
    final static private int DefaultSize = 10;
    final static private int DefaultPage = 0;

    @GET
    public Uni<List<User>> getAll(@RestQuery int page, @RestQuery int size) {
        PanacheQuery<PanacheEntityBase> users = User.findAll();
        users.page(
            page <= 0 ? DefaultPage : page,
            size <= 0 ? DefaultSize : size);

        return users.list();
    }

    @GET
    @Path("/{id}")
    public Uni<User> getOne(@RestPath Long id) {
        return User.findById(id);
    }

    @POST
    public Uni<RestResponse<User>> create(User user) {
        if(user.id != null)
            return Uni.createFrom().failure(new BadRequestException("id not allowed in body"));
        return Panache.withTransaction(user::persist).replaceWith(RestResponse.status(CREATED, user));
    }

    @PATCH
    @Path("/{id}")
    public Uni<RestResponse<User>> update(@RestPath Long id, User user) {
        logger.debugf("Request to update entity with id [%d]", id);
        return Panache
            .withTransaction(() -> User.<User> findById(id)
                .onItem().ifNotNull().invoke(entity -> User.merge(entity, user))
            )
            .onItem().ifNotNull().transform(entity -> RestResponse.status(OK, entity))
            .onItem().ifNull().continueWith(RestResponse.status(NOT_FOUND));
    }

    @DELETE
    @Path("/{id}")
    public Uni<RestResponse<Void>> delete(@RestPath Long id) {
        logger.debugf("Request to delete user with id [%d]", id);
        return Panache.withTransaction(() -> User.deleteById(id))
            .map(deleted -> deleted
            ? RestResponse.status(NO_CONTENT)
            : RestResponse.status(NOT_FOUND));
    }
}
