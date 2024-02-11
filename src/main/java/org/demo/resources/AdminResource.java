package org.demo.resources;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import org.demo.model.User;
import org.hibernate.reactive.mutiny.Mutiny;
import org.jboss.logging.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Path("/admin")
@ApplicationScoped
public class AdminResource {
  @Inject
  Mutiny.SessionFactory sf;

  final private Logger logger = Logger.getLogger(UserResource.class);

  @PUT
  @Path("/load")
  public Uni<String> load() {
    logger.info("Loading SQL script ...");
    return sf.withSession(Unchecked.function(session -> {
      InputStream in = getClass().getResourceAsStream("/import.sql");
      BufferedReader reader = new BufferedReader(new InputStreamReader(in));
      Stream<String> queries = reader.lines();

      List<Uni<Integer>> unis = queries.map(query ->
              session.createNativeQuery(query).executeUpdate()
          ).collect(Collectors.toList());

      return Uni.join().all(unis).andCollectFailures().map(Unchecked.function(i -> {
        logger.infof("Processed [%d] SQL statements", i.size());
        reader.close();
        in.close();
        return "OK";
      }));
    }));
  }

  @DELETE
  @Path("/clear")
  public Uni<Long> clear() {
    logger.warn("Deleting all users");
    return User.deleteAll();
  }
}
