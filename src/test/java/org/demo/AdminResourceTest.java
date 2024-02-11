package org.demo;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.vertx.RunOnVertxContext;
import io.quarkus.test.vertx.UniAsserter;
import jakarta.inject.Inject;
import org.demo.model.User;
import org.demo.resources.AdminResource;
import org.hibernate.reactive.mutiny.Mutiny;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@QuarkusTest
@TestHTTPEndpoint(AdminResource.class)
class AdminResourceTest {
  @Inject
  Mutiny.SessionFactory sessionFactory;

  @Test
  @RunOnVertxContext
  void load(UniAsserter asserter) {
    // Tömmer först
    when().delete("/clear")
        .then()
        .statusCode(200);

    when().put("/load")
        .then()
        .statusCode(200)
        .body(is("OK"));

    asserter.assertThat(
        () -> sessionFactory.withSession(s -> User.findAll().list()),
        result -> assertThat("Failed to load", result.size() != 0));
  }


  @Test
  @RunOnVertxContext
  void deleteAll(UniAsserter asserter) {
    when().delete("/clear")
        .then()
        .statusCode(200);

    asserter.assertThat(
        () -> sessionFactory.withSession(s -> User.findAll().list()),
        result -> assertThat("Users not deleted", result.size() == 0));
  }
}