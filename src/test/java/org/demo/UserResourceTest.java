package org.demo;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.vertx.RunOnVertxContext;
import io.quarkus.test.vertx.UniAsserter;
import jakarta.inject.Inject;
import org.demo.model.User;
import org.demo.resources.UserResource;
import org.hibernate.reactive.mutiny.Mutiny;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
@TestHTTPEndpoint(UserResource.class)
class UserResourceTest {
  @Inject
  Mutiny.SessionFactory sessionFactory;

  @Test
  @RunOnVertxContext
  void getAll(UniAsserter asserter) {
    // Ser till att iaf 1 user finns
    User expected = createUser();

    User[] result = when().get()
        .then()
        .statusCode(200)
        .assertThat()
        .body("size()", not(0))
        .extract()
        .as(User[].class);

    assertThat("User finns inte i svaret",
        Arrays.stream(result).map(x -> x.id).toList().contains(expected.id));
  }

  @Test
  void getOne() {
    when().get("/1")
        .then()
        .statusCode(200)
        .assertThat()
        .body("id", equalTo(1));
  }

  @Test
  void getOneNotFound() {
    // Vill ha 404 egentligen, inte hunnit undersöka vidare
    when().get("/9999")
        .then()
        .statusCode(204);
  }

  @Test
  void create() {
      User user = getStaticUser();
      given().header("Content-type", "application/json")
          .and()
          .body(user).when().post()
          .then()
          .statusCode(201);
  }

  // Om man nu vill slänga när id är angett, man kan också bara ignorera det. Vissa gör olika.
  @Test
  void createWithId() {
    User user = getStaticUser();
    user.id = 9999L;
    given().header("Content-type", "application/json")
        .and()
        .body(user).when().post()
        .then()
        .statusCode(400);
  }

  /*
     ... Lite fler tester på mandatory/optional fält
   */

  @Test
  @RunOnVertxContext
  void delete(UniAsserter asserter) {
    User user = createUser();
    //asserter.execute(() -> sessionFactory.withSession(s -> User.persist(user)));

    when().delete("/" + user.id)
        .then()
        .statusCode(204);

    asserter.assertThat(
        () -> sessionFactory.withSession(s -> User.findById(user.id)),
        result -> assertThat("User not deleted", result == null));
  }

  @Test
  @RunOnVertxContext
  void deleteNotFound(UniAsserter asserter) {
    when().delete("/777")
        .then()
        .statusCode(404);
  }

  private User getStaticUser() {
    User user = new User();
    user.name = "Lisa Lisdotter";
    user.address = "Lugna gatan 1, Lugnköping";
    user.email = "lisa@organisationen.org";
    user.phone = "081-123844";

    return user;
  }

  // Hade helst sparat direkt i databasen men har inte listat ut hur man får tag i id...
  private User createUser() {
    User user = getStaticUser();

    int id = given().header("Content-type", "application/json")
        .and()
        .body(user).when().post()
        .then()
        .statusCode(201)
        .extract()
        .path("id");

    user.id = (long) id;
    return user;
  }
}