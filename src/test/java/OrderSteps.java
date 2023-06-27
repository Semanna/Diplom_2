import io.qameta.allure.Step;
import io.restassured.response.Response;
import model.CreateOrder;
import model.Ingredient;
import model.Ingredients;

import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;

public class OrderSteps {
    @Step("Создание Заказа")
    public static Response create(CreateOrder createOrder, String token) {
        return given()
                .header("Content-type", "application/json")
                .header("authorization", token)
                .and()
                .body(createOrder)
                .when()
                .post("api/orders");
    }

    @Step("Получение Заказов")
    public static Response get(String token) {
        return given()
                .header("Content-type", "application/json")
                .header("authorization", token)
                .when()
                .get("api/orders");
    }

    @Step("Подготовка запроса на создание заказа")
    public static CreateOrder getOrderWithSomeIngredients() {
        Ingredients ingredients = IngredientSteps.get()
                .then()
                .assertThat()
                .statusCode(200)
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
