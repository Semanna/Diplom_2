import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import model.CreateOrder;
import model.CreateOrderResponse;
import model.RegisterResponse;
import model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;


public class CreateOrderTests {

    private String tokenToDelete;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }

    @After
    public void clean() {
        if (tokenToDelete != null) {
            UserSteps.delete(tokenToDelete);
            tokenToDelete = null;
        }
    }

    @Test
    @DisplayName("Создание заказа с авторизацией")
    @Description("Заказ создан")
    public void shouldCreateOrder() {
        User user = User.random();

        RegisterResponse createUserResponse = create(user)
                .then()
                .assertThat()
                .statusCode(200)
                .extract().as(RegisterResponse.class);

        CreateOrder createOrder = OrderSteps.getOrderWithSomeIngredients();

        CreateOrderResponse response = OrderSteps.create(createOrder, createUserResponse.getAccessToken())
                .then()
                .assertThat()
                .statusCode(200)
                .extract().as(CreateOrderResponse.class);

        assertTrue(response.isSuccess());
        assertNotNull(response.getOrder().getNumber());
        assertNotNull(response.getName());
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    @Description("Ошибка создания заказа")
    public void shouldNotCreateOrderWithoutAuthorization() {
        CreateOrder createOrder = OrderSteps.getOrderWithSomeIngredients();

        CreateOrderResponse response = OrderSteps.create(createOrder, "")
                .then()
                .assertThat()
                .statusCode(401)
                .extract().as(CreateOrderResponse.class);
        // Баг. Создание заказа без авторизации проходит успешно. Должен вернуться статус 401
        assertFalse(response.isSuccess());
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    @Description("Ошибка создания заказа")
    public void shouldNotCreateOrderWithoutIngredients() {
        User user = User.random();

        RegisterResponse createUserResponse = create(user)
                .then()
                .assertThat()
                .statusCode(200)
                .extract().as(RegisterResponse.class);

        CreateOrder createOrder = new CreateOrder();
        createOrder.setIngredients(Collections.emptyList());

        CreateOrderResponse response = OrderSteps.create(createOrder, createUserResponse.getAccessToken())
                .then()
                .assertThat()
                .statusCode(400)
                .extract().as(CreateOrderResponse.class);

        assertFalse(response.isSuccess());
        assertEquals("Ingredient ids must be provided", response.getMessage());
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингридиентов")
    @Description("Ошибка создания заказа")
    public void shouldNotCreateOrderWhenIngredientsAreInvalid() {
        User user = User.random();

        RegisterResponse createUserResponse = create(user)
                .then()
                .assertThat()
                .statusCode(200)
                .extract().as(RegisterResponse.class);

        CreateOrder createOrder = new CreateOrder();
        createOrder.setIngredients(List.of("invalid id"));

        OrderSteps.create(createOrder, createUserResponse.getAccessToken())
                .then()
                .assertThat()
                .statusCode(500);
    }

    private Response create(User user) {
        Response response = UserSteps.create(user);

        if (response.getStatusCode() == 200) {
            tokenToDelete = response
                    .then()
                    .extract().as(RegisterResponse.class).getAccessToken();
        }
        return response;
    }


}
