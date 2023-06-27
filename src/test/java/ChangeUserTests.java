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

public class ChangeUserTests {

    private String tokenToDelete;

    private static final String NEW_NAME = "New Name";
    private static final String NEW_EMAIL = "new@email";

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
    @DisplayName("Изменение email пользователя")
    @Description("Email пользователя изменен")
    public void shouldChangeEmail() {
        User user = User.random();

        RegisterResponse createResponse = create(user)
                .then()
                .assertThat()
                .statusCode(200)
                .extract().as(RegisterResponse.class);

        user.setEmail(NEW_EMAIL);

        RegisterResponse response = UserSteps.change(user, createResponse.getAccessToken())
                .then()
                .assertThat()
                .statusCode(200)
                .extract().as(RegisterResponse.class);

        assertTrue(response.isSuccess());
        assertEquals(NEW_EMAIL, response.getUser().getEmail());
    }

    @Test
    @DisplayName("Изменение имени пользователя")
    @Description("Имя пользователя изменено")
    public void shouldChangeName() {
        User user = User.random();

        RegisterResponse createResponse = create(user)
                .then()
                .assertThat()
                .statusCode(200)
                .extract().as(RegisterResponse.class);

        user.setName(NEW_NAME);

        RegisterResponse response = UserSteps.change(user, createResponse.getAccessToken())
                .then()
                .assertThat()
                .statusCode(200)
                .extract().as(RegisterResponse.class);

        assertTrue(response.isSuccess());
        assertEquals(NEW_NAME, response.getUser().getName());
    }

    @Test
    @DisplayName("Изменение email неавторизованного пользователя")
    @Description("Ошибка авторизации")
    public void shouldNotChangeUser() {
        User user = User.random();

        RegisterResponse response = UserSteps.change(user, "")
                .then()
                .assertThat()
                .statusCode(401)
                .extract().as(RegisterResponse.class);

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
