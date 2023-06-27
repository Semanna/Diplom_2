import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import model.RegisterResponse;
import model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class LoginUserTests {

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
    @DisplayName("Логин существующего пользователя")
    @Description("Пользователь успешно авторизован")
    public void shouldLoginUser() {
        User user = User.random();

        create(user)
                .then()
                .assertThat()
                .statusCode(200);

        RegisterResponse response = UserSteps.login(user)
                .then()
                .assertThat()
                .statusCode(200)
                .extract().as(RegisterResponse.class);

        assertTrue(response.isSuccess());
        assertEquals(user.getEmail(), response.getUser().getEmail());
        assertEquals(user.getName(), response.getUser().getName());
        assertNotNull(response.getAccessToken());
        assertNotNull(response.getRefreshToken());
    }

    @Test
    @DisplayName("Логин c неверным логином и паролем")
    @Description("Пользователь не авторизован")
    public void shouldNotLoginUser() {
        User user = User.random();

        RegisterResponse response = UserSteps.login(user)
                .then()
                .assertThat()
                .statusCode(401)
                .extract().as(RegisterResponse.class);

        assertFalse(response.isSuccess());
        assertEquals("email or password are incorrect", response.getMessage());
        assertNull(response.getAccessToken());
        assertNull(response.getRefreshToken());
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
