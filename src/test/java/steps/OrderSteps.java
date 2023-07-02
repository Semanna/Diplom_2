package steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import model.CreateOrder;
import model.Ingredient;
import model.Ingredients;
import org.apache.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;

public class OrderSteps {
    @Step("Создание Заказа")
    public static Response createOrder(CreateOrder createOrder, String token) {
        return given()
                .header("Content-type", "application/json")
                .header("authorization", token)
                .and()
                .body(createOrder)
                .when()
                .post("api/orders");
    }

    @Step("Получение Заказов")
    public static Response getOrder(String token) {
        return given()
                .header("Content-type", "application/json")
                .header("authorization", token)
                .when()
                .get("api/orders");
    }

    @Step("Подготовка запроса на создание заказа")
    public static CreateOrder getOrderWithSomeIngredients() {
        Ingredients ingredients = IngredientSteps.getIngredients()
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract().as(Ingredients.class);

        CreateOrder createOrder = new CreateOrder();
        List<String> ids = ingredients.getData().stream()
                .limit(3)
                .map(Ingredient::get_id)
                .collect(Collectors.toList());
        createOrder.setIngredients(ids);
        return createOrder;
    }


}
