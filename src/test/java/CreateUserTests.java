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

public class CreateUserTests extends BaseTest {

    private String tokenToDelete;

    @After
    public void clean() {
        if (tokenToDelete != null) {
            UserSteps.deleteUser(tokenToDelete);
            tokenToDelete = null;
        }
    }

    @Test
    @DisplayName("Создание пользователя")
    @Description("Пользователь создан")
    public void shouldCreateUser() {
        User user = User.random();

        RegisterResponse response = create(user)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract().as(RegisterResponse.class);

        assertTrue(response.isSuccess());
        assertEquals(user.getEmail(), response.getUser().getEmail());
        assertEquals(user.getName(), response.getUser().getName());
        assertNotNull(response.getAccessToken());
    }

    @Test
    @DisplayName("Создание пользователя, который уже зарегистрироан")
    @Description("Нельзя создать пользователя, который уже существует")
    public void shouldNotCreateSameUser() {
        User user = User.random();

        create(user)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);

        RegisterResponse response = create(user)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .extract().as(RegisterResponse.class);

        assertFalse(response.isSuccess());
        assertEquals("User already exists", response.getMessage());
    }

    @Test
    @DisplayName("Создание пользователя без email")
    @Description("Нельзя создать пользователя без email")
    public void shouldNotCreateUserWithoutEmail() {
        User user = User.random();
        user.setEmail(null);

        RegisterResponse response = create(user)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .extract().as(RegisterResponse.class);

        assertFalse(response.isSuccess());
        assertEquals("Email, password and name are required fields", response.getMessage());
    }

    @Test
    @DisplayName("Создание пользователя без password")
    @Description("Нельзя создать пользователя без password")
    public void shouldNotCreateUserWithoutPassword() {
        User user = User.random();
        user.setPassword(null);

        RegisterResponse response = create(user)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .extract().as(RegisterResponse.class);

        assertFalse(response.isSuccess());
        assertEquals("Email, password and name are required fields", response.getMessage());
    }

    @Test
    @DisplayName("Создание пользователя без name")
    @Description("Нельзя создать пользователя без name")
    public void shouldNotCreateUserWithoutName() {
        User user = User.random();
        user.setName(null);

        RegisterResponse response = create(user)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .extract().as(RegisterResponse.class);

        assertFalse(response.isSuccess());
        assertEquals("Email, password and name are required fields", response.getMessage());
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
