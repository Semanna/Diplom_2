package steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import model.User;
import org.apache.http.HttpStatus;

import static io.restassured.RestAssured.given;

public class UserSteps {
    @Step("Создание Пользователя")
    public static Response createUser(User user) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(user)
                .when()
                .post("api/auth/register");
    }

    @Step("Логин Пользователя")
    public static Response login(User user) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(user)
                .when()
                .post("api/auth/login");
    }

    @Step("Изменение Пользователя")
    public static Response changeUser(User user, String token) {
        return given()
                .header("Content-type", "application/json")
                .header("authorization", token)
                .and()
                .body(user)
                .when()
                .patch("api/auth/user");
    }

    public static void deleteUser(String tokenToDelete) {
        given()
                .header("Content-type", "application/json")
                .header("authorization", tokenToDelete)
                .and()
                .body("")
                .when()
                .delete("api/auth/user")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_ACCEPTED);
    }
}
