import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import model.RegisterResponse;
import model.User;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Test;
import steps.UserSteps;

import static org.junit.Assert.*;

public class ChangeUserTests extends BaseTest {

    private String tokenToDelete;

    private static final String NEW_NAME = "New Name";
    private static final String NEW_EMAIL = "new@email";

    @After
    public void clean() {
        if (tokenToDelete != null) {
            UserSteps.deleteUser(tokenToDelete);
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
                .statusCode(HttpStatus.SC_OK)
                .extract().as(RegisterResponse.class);

        user.setEmail(NEW_EMAIL);

        RegisterResponse response = UserSteps.changeUser(user, createResponse.getAccessToken())
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
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
                .statusCode(HttpStatus.SC_OK)
                .extract().as(RegisterResponse.class);

        user.setName(NEW_NAME);

        RegisterResponse response = UserSteps.changeUser(user, createResponse.getAccessToken())
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract().as(RegisterResponse.class);

        assertTrue(response.isSuccess());
        assertEquals(NEW_NAME, response.getUser().getName());
    }

    @Test
    @DisplayName("Изменение email неавторизованного пользователя")
    @Description("Ошибка авторизации")
    public void shouldNotChangeUser() {
        User user = User.random();

        RegisterResponse response = UserSteps.changeUser(user, "")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .extract().as(RegisterResponse.class);

        assertFalse(response.isSuccess());
        assertEquals("You should be authorised", response.getMessage());
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
