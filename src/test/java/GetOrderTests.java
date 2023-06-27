import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import model.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GetOrderTests {

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
    @DisplayName("Позучение заказов с авторизацией")
    @Description("Заказы получены")
    public void shouldReturnOrders() {
        User user = User.random();

        RegisterResponse createUserResponse = create(user)
                .then()
                .assertThat()
                .statusCode(200)
                .extract().as(RegisterResponse.class);

        CreateOrder createOrder = OrderSteps.getOrderWithSomeIngredients();

        OrderSteps.create(createOrder, createUserResponse.getAccessToken())
                .then()
                .assertThat()
                .statusCode(200);

        GetOrdersResponse response = OrderSteps.get(createUserResponse.getAccessToken())
                .then()
                .assertThat()
                .statusCode(200)
                .extract().as(GetOrdersResponse.class);

        assertTrue(response.isSuccess());
        assertEquals(1, response.getOrders().size());
        GetOrdersResponse.Order order = response.getOrders().get(0);
        assertNotNull(order.get_id());
        assertNotNull(order.getNumber());
        assertNotNull(order.getStatus());
        assertNotNull(order.getCreatedAt());
        assertNotNull(order.getUpdatedAt());
        assertArrayEquals(createOrder.getIngredients().toArray(), order.getIngredients().toArray());

        // Баг. Общее количество заказов у пользователя должно быть 1, а приходит большее количество
        assertEquals(1, response.getTotal());
        assertEquals(1, response.getTotalToday());
    }

    @Test
    @DisplayName("Позучение заказов без авторизации")
    @Description("Ошибка получения заказов")
    public void shouldNotReturnOrderWithoutAuthorization() {
        GetOrdersResponse response = OrderSteps.get("")
                .then()
                .assertThat()
                .statusCode(401)
                .extract().as(GetOrdersResponse.class);

        assertFalse(response.isSuccess());
        assertEquals("You should be authorised", response.getMessage());
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
