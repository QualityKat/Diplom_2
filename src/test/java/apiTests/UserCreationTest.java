package apiTests;

import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.Test;

public class UserCreationTest extends MethodsUserCreation {

    private final MethodsUserCreation userActions = new MethodsUserCreation();

    @Test
    @Description("Создание уникального пользователя и удаление после проверки")
    public void creationUniqueUser() {
        // Генерация случайных данных для пользователя
        String email = generateUniqueEmail();
        String password = generateUniquePassword();
        String name = generateUniqueName();
        // Создаем пользователя
        Response response = createUniqueUser(email, password, name);
        // Проверяем ответ и сохраняем токен для последующего удаления
        String accessToken = userActions.verifyUserCreation(response, email, name);
        // Можно использовать явно вызываемое удаление пользователя внутри теста
        deleteUserByToken(accessToken);
    }

    @Test
    @Description("Создание зарегистрированного пользователя и проверка на ошибку повторной регистрации")
    public void createExistingUser() {
        // Генерация случайных данных для пользователя
        String email = generateUniqueEmail();
        String password = generateUniquePassword();
        String name = generateUniqueName();
        // Первый запрос на создание пользователя
        Response firstResponse = userActions.createUniqueUser(email, password, name);
        // Проверка успешного создания пользователя
        userActions.verifyUserCreationSuccess(firstResponse);
        // Сохраняем accessToken для последующего удаления пользователя
        accessToken = firstResponse.jsonPath().getString("accessToken");

        // Второй запрос на создание того же пользователя
        Response secondResponse = userActions.createUniqueUser(email, password, name);
        // Проверка ошибки при повторной регистрации
        userActions.verifyDuplicateUserError(secondResponse);
    }

    @Test
    @Description("Создание пользователя без пароля и проверка на ошибку")
    public void createUserWithoutRequiredFieldPassword() {
        // Генерация случайных данных для пользователя без password
        String email = generateUniqueEmail();
        String name = generateUniqueName();
        // Создаем пользователя
        Response response = createUniqueUserWithoutPassword(email, name);
        // Проверяем, что создание пользователя без password завершилось ошибкой
        userActions.verifyUserCreationFailurePassword(response);
    }

    @Test
    @Description("Создание пользователя без email и проверка на ошибку")
    public void createUserWithoutRequiredFieldEmail() {
        // Генерация случайных данных для пользователя без email
        String password = generateUniquePassword();
        String name = generateUniqueName();
        // Создаем пользователя
        Response response = createUniqueUserWithoutEmail(password, name);
        // Проверяем, что создание пользователя без email завершилось ошибкой
        userActions.verifyUserCreationFailureEmail(response, "Email, password and name are required fields");
    }

    @Test
    @Description("Создание пользователя без имени и проверка на ошибку")
    public void createUserWithoutRequiredFieldName() {
        // Генерация случайных данных для пользователя без name
        String password = generateUniquePassword();
        String email = generateUniqueEmail();
        // Создаем пользователя
        Response response = createUniqueUserWithoutName(password, email);
        // Проверяем, что создание пользователя без name завершилось ошибкой
        userActions.verifyUserCreationFailureName(response, "Email, password and name are required fields");
    }
}