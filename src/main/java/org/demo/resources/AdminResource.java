package org.demo.resources;

import io.quarkus.hibernate.orm.panache.Panache;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.demo.model.User;
import org.jboss.logging.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

@Path("/admin")
@ApplicationScoped
public class AdminResource {
  final private Logger logger = Logger.getLogger(UserResource.class);

  @Transactional
  @POST
  @Path("/users:load")
  public Response load() throws IOException {
    logger.info("Loading SQL script ...");

    InputStream in = getClass().getResourceAsStream("/import.sql");
    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
    List<String> queries = reader.lines().toList();

    queries.forEach(query ->
      Panache.getEntityManager().createNativeQuery(query).executeUpdate()
    );

    logger.infof("Processed [%d] SQL statements", queries.size());
    reader.close();
    in.close();
    return Response.ok().build();
  }

  @Transactional
  @DELETE
  @Path("/users")
  public Response clear() {
    logger.warn("Deleting all users");
    User.deleteAll();
    return Response.noContent().build();
  }
}
