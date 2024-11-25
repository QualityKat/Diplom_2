package apiTests;

import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.Test;

public class UserDataUpdateTest extends MethodsUserCreation {

    private MethodsUserCreation methodsUserLogin = new MethodsUserCreation();

    // Для авторизованного пользователя
    @Test
    @Description("Изменение email пользователя с авторизацией")
    public void updateUserWithAuthorizationEmail() {
        // Генерация случайных данных для пользователя
        String email = generateUniqueEmail();
        String password = generateUniquePassword();
        String name = generateUniqueName();
        // Создаем пользователя
        Response createUserResponse = createUniqueUser(email, password, name);
        verifyUserCreation(createUserResponse, email, name);
        // Получаем токен для авторизации пользователя
        String accessToken = createUserResponse.jsonPath().getString("accessToken");
        // Генерация нового email для обновления
        String newEmail = generateUniqueEmail();
        // Подготовка данных для обновления email
        String requestBody = "{\"email\":\"" + newEmail + "\", \"name\":\"" + name + "\", \"password\":\"" + password + "\"}";
        // Логируем данные запроса
        logRequest(accessToken, requestBody);
        // Отправляем запрос на обновление данных пользователя
        Response updateResponse = updateUserEmail(accessToken, newEmail, password, name);
        // Логируем ответ
        logResponse(updateResponse);
        // Проверяем что статус код 200 и данные обновлены
        validateUpdateResponse(updateResponse, newEmail, name);
        // Удаление пользователя по токену
        deleteUserByToken(accessToken);
    }

    @Test
    @Description("Изменение имени пользователя с авторизацией")
    public void updateUserWithAuthorizationName() {
        String email = generateUniqueEmail();
        String password = generateUniquePassword();
        String name = generateUniqueName();
        Response createUserResponse = createUniqueUser(email, password, name);
        verifyUserCreation(createUserResponse, email, name);
        String accessToken = createUserResponse.jsonPath().getString("accessToken");
        // Генерация нового имени для обновления
        String newName = generateUniqueName();
        // Подготовка данных для обновления имени
        String requestBody = "{\"email\":\"" + email + "\", \"name\":\"" + newName + "\", \"password\":\"" + password + "\"}";
        logRequestName(accessToken, requestBody);
        Response updateResponse = updateUserName(accessToken, email, password, newName);
        logResponseName(updateResponse);
        validateUpdateNameResponse(updateResponse, newName, email);
        deleteUserByToken(accessToken);
    }

    @Test
    @Description("Изменение пароля пользователя с авторизацией")
    public void updateUserWithAuthorizationPassword() {
        String email = generateUniqueEmail();
        String password = generateUniquePassword();
        String name = generateUniqueName();
        Response createUserResponse = createUniqueUser(email, password, name);
        verifyUserCreation(createUserResponse, email, name);
        // Получаем токен для авторизации пользователя для последующего удаления
        String accessToken = createUserResponse.jsonPath().getString("accessToken");
        String newPassword = generateUniquePassword();
        String requestBody = "{\"email\":\"" + email + "\", \"name\":\"" + name + "\", \"password\":\"" +newPassword + "\"}";
        logRequestPassword(accessToken, requestBody);
        Response updateResponse = updateUserPassword(accessToken, newPassword);
        logResponsePassword(updateResponse);
        validateUpdatePasswordResponse(updateResponse, newPassword);
        deleteUserByToken(accessToken);
    }

    @Test
    @Description("Изменение всех данных пользователя с авторизацией")
    public void updateUserWithAuthorizationAllFields() {
        String email = generateUniqueEmail();
        String password = generateUniquePassword();
        String name = generateUniqueName();
        Response createUserResponse = createUniqueUser(email, password, name);
        verifyUserCreation(createUserResponse, email, name);
        String accessToken = createUserResponse.jsonPath().getString("accessToken");
        String newEmail = generateUniqueEmail();
        String newPassword = generateUniquePassword();
        String newName = generateUniqueName();
        String requestBody = "{\"email\":\"" + newEmail + "\", \"name\":\"" + newName + "\", \"password\":\"" + newPassword + "\"}";
        logRequestAllFields(accessToken, requestBody);
        Response updateResponse = updateUserAllFields(accessToken, newEmail, newPassword, newName);
        logResponseAll(updateResponse);
        validateUpdateAllFieldsResponse(updateResponse, newEmail, newName);
        deleteUserByToken(accessToken);
    }

    @Test
    @Description("Изменение email пользователя без авторизации")
    public void updateUserWithoutAuthorizationEmail() {
        String email = generateUniqueEmail();
        String password = generateUniquePassword();
        String name = generateUniqueName();
        Response createUserResponse = createUniqueUser(email, password, name);
        verifyUserCreation(createUserResponse, email, name);
        String newEmail = generateUniqueEmail();
        String requestBody = "{\"email\":\"" + newEmail + "\", \"name\":\"" + name + "\", \"password\":\"" + password + "\"}";
        logRequestWithoutAuth(requestBody);
        Response updateResponse = updateUserEmailWithoutAuth(newEmail, password, name);
        logResponse(updateResponse);
        // Проверяем что статус код 401 (Unauthorized)
        validateUnauthorizedResponse(updateResponse);
        // Удаление пользователя по токену (необходимо передать токен авторизации)
        String accessToken = createUserResponse.jsonPath().getString("accessToken");
        deleteUserByToken(accessToken);
    }

    @Test
    @Description("Изменение имени пользователя без авторизации")
    public void updateUserWithoutAuthorizationName() {
        String email = generateUniqueEmail();
        String password = generateUniquePassword();
        String name = generateUniqueName();
        Response createUserResponse = createUniqueUser(email, password, name);
        verifyUserCreation(createUserResponse, email, name);
        String newName = generateUniqueName();
        // Подготовка данных для обновления имени без токена авторизации
        String requestBody = "{\"email\":\"" + email + "\", \"name\":\"" + newName + "\", \"password\":\"" + password + "\"}";
        // Логируем данные запроса (без авторизации)
        logRequestWithoutAuthName(requestBody);
        // Отправляем запрос на обновление данных пользователя без авторизации
        Response updateResponse = updateUserNameWithoutAuth(email, password, newName);
        // Логируем ответ
        logResponse(updateResponse);
        // Проверяем, что статус код 401 (Unauthorized)
        validateUnauthorizedResponse(updateResponse);
        // Удаление пользователя по токену (необходимо передать токен авторизации)
        String accessToken = createUserResponse.jsonPath().getString("accessToken");
        deleteUserByToken(accessToken);
    }

    @Test
    @Description("Изменение пароля пользователя без авторизации")
    public void updateUserWithoutAuthorizationPassword() {
        String email = generateUniqueEmail();
        String password = generateUniquePassword();
        String name = generateUniqueName();
        Response createUserResponse = createUniqueUser(email, password, name);
        verifyUserCreation(createUserResponse, email, name);
        String newPassword = generateUniquePassword();
        String requestBody = "{\"email\":\"" + email + "\", \"name\":\"" + name + "\", \"password\":\"" + newPassword + "\"}";
        logRequestWithoutAuthPassword(requestBody);
        Response updateResponse = updateUserPasswordWithoutAuth(email, newPassword, name);
        logResponse(updateResponse);
        validateUnauthorizedResponse(updateResponse);
        String accessToken = createUserResponse.jsonPath().getString("accessToken");
        deleteUserByToken(accessToken);
    }

    @Test
    @Description("Изменение всех данных пользователя без авторизации")
    public void updateUserWithoutAuthorizationAllData() {
        String email = generateUniqueEmail();
        String password = generateUniquePassword();
        String name = generateUniqueName();
        Response createUserResponse = createUniqueUser(email, password, name);
        verifyUserCreation(createUserResponse, email, name);
        String newEmail = generateUniqueEmail();
        String newPassword = generateUniquePassword();
        String newName = generateUniqueName();
        String requestBody = "{\"email\":\"" + newEmail + "\", \"name\":\"" + newName + "\", \"password\":\"" + newPassword + "\"}";
        logRequestWithoutAuthAll(requestBody);
        Response updateResponse = updateUserAllWithoutAuth(email, newPassword, name);
        logResponse(updateResponse);
        validateUnauthorizedResponse(updateResponse);
        String accessToken = createUserResponse.jsonPath().getString("accessToken");
        deleteUserByToken(accessToken);
    }
}
