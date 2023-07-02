import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import model.CreateOrder;
import model.CreateOrderResponse;
import model.RegisterResponse;
import model.User;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Test;
import steps.OrderSteps;
import steps.UserSteps;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;


public class CreateOrderTests extends BaseTest {

    private String tokenToDelete;

    @After
    public void clean() {
        if (tokenToDelete != null) {
            UserSteps.deleteUser(tokenToDelete);
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
                .statusCode(HttpStatus.SC_OK)
                .extract().as(RegisterResponse.class);

        CreateOrder createOrder = OrderSteps.getOrderWithSomeIngredients();

        CreateOrderResponse response = OrderSteps.createOrder(createOrder, createUserResponse.getAccessToken())
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
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

        CreateOrderResponse response = OrderSteps.createOrder(createOrder, "")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
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
                .statusCode(HttpStatus.SC_OK)
                .extract().as(RegisterResponse.class);

        CreateOrder createOrder = new CreateOrder();
        createOrder.setIngredients(Collections.emptyList());

        CreateOrderResponse response = OrderSteps.createOrder(createOrder, createUserResponse.getAccessToken())
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
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
                .statusCode(HttpStatus.SC_OK)
                .extract().as(RegisterResponse.class);

        CreateOrder createOrder = new CreateOrder();
        createOrder.setIngredients(List.of("invalid id"));

        OrderSteps.createOrder(createOrder, createUserResponse.getAccessToken())
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

    private Response create(User user) {
        Response response = UserSteps.createUser(user);

        if (response.getStatusCode() == HttpStatus.SC_OK) {
            tokenToDelete = response
                    .then()
                    .extract().as(RegisterResponse.class).getAccessToken();
        }
        return response;
    }


}
