package apiTests;

import io.qameta.allure.Description;
import org.junit.Test;
import io.restassured.response.Response;

public class UserLoginTest extends MethodsUserCreation {

    private MethodsUserCreation methodsUserLogin = new MethodsUserCreation();

    @Test
    @Description("Логин под существующим пользователем с проверкой успешного ответа")
    public void loginWithExistingUserTest() {
        // Генерация случайных данных для пользователя
        String email = generateUniqueEmail();
        String password = generateUniquePassword();
        String name = generateUniqueName();
        // Создаем пользователя
        Response response = createUniqueUser(email, password, name);
        verifyUserCreation(response, email, name);
        // Выполнение логина с созданным пользователем
        Response loginResponse = methodsUserLogin.loginWithUser(email, password, name);
        // Проверяем ответ и сохраняем токен для последующего удаления
        String accessToken = verifyLoginSuccess(loginResponse);
        // Используем явно вызываемое удаление пользователя внутри теста
        deleteUserByToken(accessToken);
    }

    @Test
    @Description("Логин с верным именем, но неверным email и паролем")
    public void loginWithInvalidCredentials() {
        // Генерация данных для случайного пользователя
        String email = generateUniqueEmail();
        String password = generateUniquePassword();
        String name = generateUniqueName();
        // Создание пользователя с уникальными данными
        Response createUserResponse = createUniqueUser(email, password, name);
        // Проверка успешного создания пользователя
        verifyUserCreation(createUserResponse, email, name);
        // Получаем accessToken для удаления пользователя
        String accessToken = createUserResponse.jsonPath().getString("accessToken");
        // Генерация неверных данных для логина (неверный email и неверный пароль)
        String invalidEmail = "invalid" + email;
        String invalidPassword = "wrongPassword";
        // Выполнение логина с неверным email и паролем, но правильным именем
        Response loginResponse = methodsUserLogin.loginWithUser(invalidEmail, invalidPassword, name);
        // Использование вспомогательного класса для проверки неверных данных при логине
        MethodsUserCreation.verifyLoginWithInvalidCredentials(loginResponse);
        // Удаление пользователя по токену
        deleteUserByToken(accessToken);
    }

}