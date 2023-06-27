import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class IngredientSteps {
    @Step("Получение Ингредиентов")
    public static Response get() {
        return given()
                .header("Content-type", "application/json")
                .when()
                .get("api/ingredients");
    }
}
