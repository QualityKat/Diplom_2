package apiTests;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

// Общие методы для тестовых классов
public class MethodsUserCreation {

    protected String accessToken;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }

    @After
    public void tearDown() {
        if (accessToken != null && !accessToken.isEmpty()) {
            deleteUserByToken(accessToken);
        }
    }

    @Step("Удалить пользователя по токену")
    public void deleteUserByToken(String token) {
        String cleanToken = token.replace("Bearer ", "");
        // Отправляем запрос на удаление пользователя
        Response deleteResponse = given()
                .header("Authorization", "Bearer " + cleanToken)
                .when()
                .delete("/api/auth/user");
        // Вывод тела и кода ответа
        System.out.println("Delete Response Code: " + deleteResponse.getStatusCode());
        System.out.println("Delete Response Body:\n" + deleteResponse.getBody().prettyPrint());
        // Проверка успешного удаления
        assertThat(deleteResponse.getStatusCode(), is(202));
        assertThat(deleteResponse.jsonPath().getBoolean("success"), is(true));
        // Ожидаемое сообщение
        String expectedMessage = "User successfully removed";
        // Проверяем правильное сообщение
        assertThat(deleteResponse.jsonPath().getString("message"), is(expectedMessage));
        System.out.println("Пользователь успешно удален");
    }

    @Step("Создать уникального пользователя")
    public Response createUniqueUser(String email, String password, String name) {
        String body = String.format(
                "{\n" +
                        "  \"email\": \"%s\",\n" +
                        "  \"password\": \"%s\",\n" +
                        "  \"name\": \"%s\"\n" +
                        "}", email, password, name);
        Response respons = given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/api/auth/register");
        System.out.println("Пользователь успешно создан: " + email);
        return respons;
    }

    @Step("Проверить ответ на создание уникального пользователя")
    public String verifyUserCreation(Response response, String email, String name) {
        // Выводим код и тело ответа в консоль
        System.out.println("Response Code: " + response.getStatusCode());
        System.out.println("Response body:\n" + response.getBody().prettyPrint());
        // Проверяем код ответа 200
        assertThat(response.getStatusCode(), is(200));
        // Проверяем, что поле "success" равно true
        assertThat(response.jsonPath().getBoolean("success"), is(true));
        // Проверяем email и имя пользователя соответствуют ожиданиям
        assertThat(response.jsonPath().getString("user.email"), is(email));
        assertThat(response.jsonPath().getString("user.name"), is(name));
        // Проверяем, что accessToken и refreshToken не пустые
        String accessToken = "Bearer " + response.jsonPath().getString("accessToken");
        assertThat(accessToken, not(isEmptyOrNullString()));
        assertThat(response.jsonPath().getString("refreshToken"), not(isEmptyOrNullString()));
        // Возвращаем accessToken для последующего использования
        return accessToken;
    }

    @Step("Проверка успешного создания пользователя")
    public void verifyUserCreationSuccess(Response response) {
        // Вывод тела и кода ответа на консоль в формате JSON с переносами строк
        System.out.println("Response Code: " + response.getStatusCode());
        System.out.println("Response body:\n" + response.getBody().prettyPrint());
        // Проверка кода ответа и успешного статуса
        assertThat(response.getStatusCode(), is(200));
        assertThat(response.jsonPath().getBoolean("success"), is(true));
    }

    @Step("Проверка ошибки при повторной регистрации пользователя")
    public void verifyDuplicateUserError(Response response) {
        // Вывод тела и кода ответа на консоль в формате JSON с переносами строк
        System.out.println("Response Code: " + response.getStatusCode());
        System.out.println("Response body:\n" + response.getBody().prettyPrint());
        // Проверка кода ответа и сообщения об ошибке
        assertThat(response.getStatusCode(), is(403));
        assertThat(response.jsonPath().getString("message"), equalTo("User already exists"));
    }

    // Создание пользователя без данных
    // Password
    @Step("Создать уникального пользователя без password")
    public Response createUniqueUserWithoutPassword(String email, String name) {
        String body = String.format(
                "{\n" +
                        "  \"email\": \"%s\",\n" +
                        "  \"name\": \"%s\"\n" +
                        "}", email, name);
        return given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/api/auth/register");
    }

    @Step("Проверка ответа на неудачное создание пользователя без password")
    public void verifyUserCreationFailurePassword(Response response) {
        System.out.println("Response Code: " + response.getStatusCode());
        System.out.println("Response body:\n" + response.getBody().prettyPrint());
        assertThat(response.getStatusCode(), is(403));
        assertThat(response.jsonPath().getBoolean("success"), is(false));
        assertThat(response.jsonPath().getString("message"), is("Email, password and name are required fields"));
    }

    // Email
    @Step("Создать уникального пользователя без email")
    public Response createUniqueUserWithoutEmail(String password, String name) {
        String body = String.format(
                "{\n" +
                        "  \"password\": \"%s\",\n" +
                        "  \"name\": \"%s\"\n" +
                        "}", password, name);
        return given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/api/auth/register");
    }

    @Step("Проверка ответа на неудачное создание пользователя без email")
    public void verifyUserCreationFailureEmail(Response response, String expectedMessage) {
        System.out.println("Response Code: " + response.getStatusCode());
        System.out.println("Response body:\n" + response.getBody().prettyPrint());
        assertThat(response.getStatusCode(), is(403));
        assertThat(response.jsonPath().getBoolean("success"), is(false));
        assertThat(response.jsonPath().getString("message"), is(expectedMessage));
    }

    // Name
    @Step("Создать уникального пользователя без name")
    public Response createUniqueUserWithoutName(String password, String email) {
        String body = String.format(
                "{\n" +
                        "  \"password\": \"%s\",\n" +
                        "  \"email\": \"%s\"\n" +
                        "}", password, email);
        return given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/api/auth/register");
    }

    @Step("Проверка ответа на неудачное создание пользователя без name")
    public void verifyUserCreationFailureName(Response response, String expectedMessage) {
        System.out.println("Response Code: " + response.getStatusCode());
        System.out.println("Response body:\n" + response.getBody().prettyPrint());
        assertThat(response.getStatusCode(), is(403));
        assertThat(response.jsonPath().getBoolean("success"), is(false));
        assertThat(response.jsonPath().getString("message"), is(expectedMessage));
    }

    public String generateUniqueEmail() {
        String uniqueId = UUID.randomUUID().toString().substring(0, 6);
        return "user" + uniqueId + "@yandex.ru";
    }

    public String generateUniquePassword() {
        return "pass" + UUID.randomUUID().toString().substring(0, 6);
    }

    public String generateUniqueName() {
        return "User" + UUID.randomUUID().toString().substring(0, 6);
    }



    // Методы для тестового класса UserLoginTest
    @Step("Логин под существующим пользователем")
    public Response loginWithUser(String email, String password, String name ) {
        System.out.println("Логин с данными - email: " + email + ", password: " + password + ", name: " + name);
        String body = String.format(
                "{\n" +
                        "  \"email\": \"%s\",\n" +
                        "  \"password\": \"%s\",\n" +
                        "  \"name\": \"%s\"\n" +
                        "}", email, password, name);
        Response response = given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/api/auth/login");
        System.out.println("Пользователь успешно вошел в систему");
        return response;
    }

    @Step("Проверка ответа успешного логина пользователя")
    public String verifyLoginSuccess(Response response) {
        System.out.println("Response Code: " + response.getStatusCode());
        System.out.println("Response Body:\n" + response.getBody().prettyPrint());
        assertThat(response.getStatusCode(), equalTo(200));
        assertThat(response.jsonPath().getBoolean("success"), is(true));
        // Извлечение токена из ответа и возврат его
        return response.jsonPath().getString("accessToken");
    }

    @Step("Проверка ответа выполнения логина с неверными email и паролем")
    public static void verifyLoginWithInvalidCredentials(Response loginResponse) {
        loginResponse
                .then()
                .statusCode(401)
                .body("message", equalTo("email or password are incorrect"));
    }



    // Методы для тестового класса UserDataUpdateTest
    // C авторизацией
    // Email
    @Step ("Проверка обновления email пользователя с авторизацией")
    public static Response updateUserEmail(String accessToken, String newEmail, String password, String name) {
        String requestBody = "{\"email\":\"" + newEmail + "\", \"name\":\"" + name + "\", \"password\":\"" + password + "\"}";
        return given()
                .contentType(ContentType.JSON)
                .header("Authorization", accessToken)
                .body(requestBody)
                .when()
                .patch("/api/auth/user");
    }

    @Step ("Логирование данных запроса обновления email пользователя с авторизацией")
    public static void logRequest(String accessToken, String requestBody) {
        System.out.println("Запрос на обновление email:");
        System.out.println("Токен авторизации: " + accessToken);
        System.out.println("Тело запроса: " + requestBody);
    }

    @Step ("Логирование данных ответа обновления email пользователя с авторизацией")
    public static void logResponse(Response response) {
        System.out.println("Ответ после обновления email:");
        System.out.println(response.prettyPrint());
    }

    @Step ("Проверка что статус код 200 и данные email пользователя с авторизацией обновлены")
    public static void validateUpdateResponse(Response response, String newEmail, String name) {
        response
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(newEmail))
                .body("user.name", equalTo(name)); // Проверяеми что имя осталось прежним
    }


    // Name
    @Step ("Проверка обновления name пользователя с авторизацией")
    public static Response updateUserName(String accessToken, String email, String password, String newName) {
        String requestBody = "{\"email\":\"" + email + "\", \"name\":\"" + newName + "\", \"password\":\"" + password + "\"}";
        return given()
                .contentType(ContentType.JSON)
                .header("Authorization", accessToken)
                .body(requestBody)
                .when()
                .patch("/api/auth/user");
    }

    @Step ("Логирование данных запроса обновления name пользователя с авторизацией")
    public static void logRequestName(String accessToken, String requestBody) {
        System.out.println("Запрос на обновление name:");
        System.out.println("Токен авторизации: " + accessToken);
        System.out.println("Тело запроса: " + requestBody);
    }

    @Step ("Логирование данных ответа обновления name пользователя с авторизацией")
    public static void logResponseName(Response response) {
        System.out.println("Ответ после обновления name:");
        System.out.println(response.prettyPrint());
    }

    @Step("Проверка, что статус код 200 и name пользователя с авторизацией обновлено")
    public static void validateUpdateNameResponse(Response response, String newName, String email) {
        response
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.name", equalTo(newName)) // Проверяем что name обновилось
                .body("user.email", equalTo(email)); // Проверяем что email остался прежним
    }


    // Password
    @Step("Проверка обновления password пользователя с авторизацией")
    public static Response updateUserPassword(String accessToken, String newPassword) {
        // Отправляем только новый пароль, имя и email остаются прежними
        String requestBody = "{\"password\":\"" + newPassword + "\"}";
        return given()
                .contentType(ContentType.JSON)
                .header("Authorization", accessToken)
                .body(requestBody)
                .when()
                .patch("/api/auth/user");
    }

    @Step ("Логирование данных запроса обновления password пользователя с авторизацией")
    public static void logRequestPassword(String accessToken, String requestBody) {
        System.out.println("Запрос на обновление password:");
        System.out.println("Токен авторизации: " + accessToken);
        System.out.println("Тело запроса: " + requestBody);
    }

    @Step ("Логирование данных ответа обновления password пользователя с авторизацией")
    public static void logResponsePassword(Response response) {
        System.out.println("Ответ после обновления password:");
        System.out.println(response.prettyPrint());
    }

    @Step("Проверка кода и ответа обновления password пользователя с авторизацией")
    public static void validateUpdatePasswordResponse(Response response, String newPassword) {
        response
                .then()
                .statusCode(200)
                .body("success", equalTo(true)); // Проверяем только успешность обновления пароля
    }


    // Все поля
    @Step("Проверка обновления всех полей пользователя с авторизацией")
    public static Response updateUserAllFields(String accessToken, String newEmail, String newPassword, String newName) {
        String requestBody = "{\"email\":\"" + newEmail + "\", \"name\":\"" + newName + "\", \"password\":\"" + newPassword + "\"}";
        return given()
                .contentType(ContentType.JSON)
                .header("Authorization", accessToken)
                .body(requestBody)
                .when()
                .patch("/api/auth/user");
    }

    @Step("Логирование данных запроса на обновление всех полей пользователя с авторизацией")
    public static void logRequestAllFields(String accessToken, String requestBody) {
        System.out.println("Запрос на обновление всех полей:");
        System.out.println("Токен авторизации: " + accessToken);
        System.out.println("Тело запроса: " + requestBody);
    }

    @Step ("Логирование данных ответа обновления всех данных пользователя с авторизацией")
    public static void logResponseAll(Response response) {
        System.out.println("Ответ после обновления всех полей:");
        System.out.println(response.prettyPrint());
    }

    @Step("Проверка обновления всех данных пользователя с авторизацией")
    public static void validateUpdateAllFieldsResponse(Response response, String newEmail, String newName) {
        response
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(newEmail)) // Проверяем что email обновился
                .body("user.name", equalTo(newName));  // Проверяем что name обновилось
    }


    // Без авторизации
    // Email
    @Step("Логирование данных запроса на обновление email без авторизации")
    public static void logRequestWithoutAuth(String requestBody) {
        System.out.println("Запрос на обновление email без авторизации:");
        System.out.println("Тело запроса: " + requestBody);
    }

    @Step("Обновление email пользователя без авторизации")
    public static Response updateUserEmailWithoutAuth(String newEmail, String password, String name) {
        String requestBody = "{\"email\":\"" + newEmail + "\", \"name\":\"" + name + "\", \"password\":\"" + password + "\"}";
        return given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .patch("/api/auth/user");
    }

    @Step("Проверка ошибки доступа без авторизации")
    public static void validateUnauthorizedResponse(Response response) {
        response
                .then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    // Name
    @Step("Обновление name пользователя без авторизации")
    public static Response updateUserNameWithoutAuth(String email, String password, String newName) {
        String requestBody = "{\"email\":\"" + email + "\", \"name\":\"" + newName + "\", \"password\":\"" + password + "\"}";
        return given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .patch("/api/auth/user");
    }

    @Step("Логирование данных запроса на обновление name без авторизации")
    public static void logRequestWithoutAuthName(String requestBody) {
        System.out.println("Запрос на обновление name без авторизации:");
        System.out.println("Тело запроса: " + requestBody);
    }

    // Password
    @Step("Обновление password пользователя без авторизации")
    public static Response updateUserPasswordWithoutAuth(String email, String newPassword, String name) {
        String requestBody = "{\"email\":\"" + email + "\", \"name\":\"" + name + "\", \"password\":\"" + newPassword + "\"}";
        return given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .patch("/api/auth/user");
    }

    @Step("Логирование данных запроса на обновление password без авторизации")
    public static void logRequestWithoutAuthPassword(String requestBody) {
        System.out.println("Запрос на обновление password без авторизации:");
        System.out.println("Тело запроса: " + requestBody);
    }

    // Все поля
    @Step("Обновление всех данных пользователя без авторизации")
    public static Response updateUserAllWithoutAuth(String newEmail, String newPassword, String newName) {
        String requestBody = "{\"email\":\"" + newEmail + "\", \"name\":\"" + newName + "\", \"password\":\"" + newPassword + "\"}";
        return given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .patch("/api/auth/user");
    }
    @Step("Логирование данных запроса на обновление всех данных пользователя без авторизации")
    public static void logRequestWithoutAuthAll(String requestBody) {
        System.out.println("Запрос на обновление всех данных без авторизации:");
        System.out.println("Тело запроса: " + requestBody);
    }




    // Методы для тестового класса OrderCreationTest
    // С авторизацией и с ингредиенами
    @Step("Метод для запроса создания заказа с ингредиентами и авторизацией")
    public static Response createOrderWithIngredients(String accessToken) {
        System.out.println("Проверка успешного создания заказа...");
        // Формирование тела запроса для создания заказа
        String orderRequestBody = "{\n" +
                "  \"ingredients\": [\"61c0c5a71d1f82001bdaaa6d\", \"61c0c5a71d1f82001bdaaa77\"]\n" +
                "}";
        // Отправка запроса на создание заказа с токеном авторизации
        return given()
                .contentType(ContentType.JSON)
                .header("Authorization", accessToken)
                .body(orderRequestBody)
                .when()
                .post("/api/orders");
    }

    @Step("Метод для проверки кода и тела ответа успешности создания заказа с ингредиентами и атворизацией")
    public static void verifyOrderCreation(Response orderResponse) {
        System.out.println("Проверка ответа успешного создания заказа...");
        orderResponse.then()
                .statusCode(200)
                .body("success", equalTo(true));
        System.out.println("Создание заказа проверено успешно");
    }


    // С авторизацией но без ингредиентов
    @Step("Метод для запроса создания заказа с авторизацией но без ингредиентов")
    public static Response createOrderWitNoIngredients(String accessToken) {
        // Формирование тела запроса для создания заказа без ингредиентов
        String orderRequestBody = "{\n" +
                "  \"ingredients\": []\n" +
                "}";
        // Отправка запроса на создание заказа с токеном авторизации
        return given()
                .contentType(ContentType.JSON)
                .header("Authorization", accessToken)
                .body(orderRequestBody)
                .when()
                .post("/api/orders");
    }

    @Step("Метод для проверки кода и тела ответа ошибки создания заказа без ингредиентов но с авторизацией")
    public static void verifyOrderCreationNoIngredients(Response orderResponse) {
        orderResponse.then()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }


    // Без авторизации но с ингредиентами
    @Step("Метод для проверки ошибки запроса создания заказа с ингредиентами но без авторизации")
    public static Response createOrderWithoutAuthorization() {
        System.out.println("Выполняется метод для проверки ошибки запроса создания заказа без авторизации, с ингредиентами...");
        // Формирование тела запроса для создания заказа с ингредиентами
        String orderRequestBody = "{\n" +
                "  \"ingredients\": [\"61c0c5a71d1f82001bdaaa6d\", \"61c0c5a71d1f82001bdaaa77\"]\n" +
                "}";
        // Отправка запроса на создание заказа без авторизации
        return given()
                .contentType(ContentType.JSON)
                .body(orderRequestBody)
                .when()
                .post("/api/orders");
    }

    @Step("Метод для проверки кода и тела ответа при отсутствии авторизации создания заказа с ингредиентами")
    public static void verifyOrderCreationUnauthorized(Response orderResponse) {
        orderResponse.then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
        System.out.println("Метод для проверки ошибки кода и тела ответа при отсутствии авторизации создания заказа с ингредиентами отработал успешно");
    }


    // Без авторизации и без ингредиентов
    @Step("Метод для запроса создания заказа без авторизации и без ингредиентов")
    public static Response createOrderWithoutAuthorizationAndIngredients() {
        // Формирование тела запроса для создания заказа без ингредиентов
        String orderRequestBody = "{\n" +
                "  \"ingredients\": []\n" +
                "}";
        // Отправка запроса на создание заказа без авторизации
        return given()
                .contentType(ContentType.JSON)
                .body(orderRequestBody)
                .when()
                .post("/api/orders");
    }

    @Step("Метод для проверки кода и тела ответа при отсутствии авторизации")
    public static void verifyOrderCreationNoIngredientsUnauthorized(Response orderResponse) {
        orderResponse.then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @Step("Метод для проверки кода и тела ответа при отсутствии ингредиентов")
    public static void verifyOrderCreationNoAuthorizedAndNoIngredients(Response orderResponse) {
        orderResponse.then()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }


    // С авторизацией но с неверным хешем ингредиентов
    @Step("Метод для запроса создания заказа с неверным хешем ингредиентов")
    public static Response createOrderWithInvalidIngredientsHash(String accessToken) {
        // Формирование тела запроса для создания заказа с неверным хешем ингредиентов
        String orderRequestBody = "{\n" +
                "  \"ingredients\": [\"invalidHash1\", \"invalidHash2\"]\n" +
                "}";
        // Отправка запроса на создание заказа с токеном авторизации
        return given()
                .contentType(ContentType.JSON)
                .header("Authorization", accessToken)
                .body(orderRequestBody)
                .when()
                .post("/api/orders");
    }

    @Step("Метод для проверки кода и тела ответа при неверном хеше ингредиентов")
    public static void verifyOrderCreationInvalidIngredientsHash(Response orderResponse) {
        orderResponse.then()
                .statusCode(500)
                .body("success", equalTo(false));
    }



    // Методы для тестового класса UserOrdersTest
    // С авторизацией
    @Step("Метод для получения списка заказов авторизованного пользователя")
    public static Response getUserOrders(String accessToken) {
        System.out.println("Выполняется метод для получения списка заказов авторизованного пользователя...");
        // Отправка запроса на получение заказов с токеном авторизации
        Response response = given()
                .header("Authorization", accessToken)
                .log().all() // Логирование запроса
                .when()
                .get("/api/orders")
                .then()
                .log().all() // Логирование ответа
                .extract().response();
        System.out.println("Метод получения списка заказов пользователя отработал");
        return response;
    }

    @Step("Метод для проверки ответа успешного получения списка заказов")
    public static void verifyUserOrdersRetrieval(Response ordersResponse) {
        System.out.println("Проверка успешного получения списка заказов....");
        ordersResponse.then()
                .statusCode(200)
                .body("success", equalTo(true));
        // Дополнительная проверка что хотя бы один заказ содержится в ответе
        List<Map<String, Object>> orders = ordersResponse.jsonPath().getList("orders");
        assertThat("Список заказов должен быть не пустым", orders, not(empty()));
        System.out.println("Список заказов проверен успешно");
    }


    // Без авторизации
    @Step("Метод проверки ошибки для получения списка заказов без авторизации")
    public static Response getUserOrdersWithoutAuthorization() {
        System.out.println("Выполняется метод проверки ошибки для получения списка заказов без авторизации...");
        // Отправка запроса на получение заказов без токена авторизации
        Response response = given()
                .log().all() // Логирование запроса
                .when()
                .get("/api/orders")
                .then()
                .log().all() // Логирование ответа
                .extract().response();
        System.out.println("Метод проверки ошибки для получения списка заказов без авторизации отработал");
        return response;
    }

    @Step("Метод для проверки ответа ошибки получения списка заказов пользователя без авторизации")
    public static void verifyUnauthorizedResponse(Response response) {
        System.out.println("Проверка ответа об ошибке получения списка заказов без авторизации....");
        response.then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
        System.out.println("Проверка ответа об ошибке получения списка заказов без авторизации прошла успешно");
    }
}
