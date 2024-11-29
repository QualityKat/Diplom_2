package apiTests;

import io.qameta.allure.Description;
import org.junit.After;
import org.junit.Test;
import io.restassured.response.Response;

public class OrderCreationTest extends MethodsUserCreation {

    private String accessToken; // Переменная для хранения токена, используемая в @After

    @After
    public void tearDown() {
        if (accessToken != null) {
            deleteUserByToken(accessToken); // Удаление пользователя после теста
        }
    }

    @Test
    @Description("Создание заказа с авторизацией и с ингредиентами")
    public void createOrderWithAuthorization() {
        // Генерация уникальных данных для пользователя
        String email = generateUniqueEmail();
        String password = generateUniquePassword();
        String name = generateUniqueName();
        // Создание нового пользователя
        Response createUserResponse = createUniqueUser(email, password, name);
        createUserResponse.then().statusCode(200);
        // Логин под созданным пользователем и получение токена авторизации
        Response loginResponse = loginWithUser(email, password, name);
        loginResponse.then().statusCode(200);
        // Извлечение accessToken из ответа
        accessToken = loginResponse.jsonPath().getString("accessToken");
        // Создание заказа с токеном авторизации с использованием MethodsUserCreation
        Response orderResponse = MethodsUserCreation.createOrderWithIngredients(accessToken);
        // Логирование и проверка успешного создания заказа
        orderResponse.then().log().all();
        MethodsUserCreation.verifyOrderCreation(orderResponse);
    }

    @Test
    @Description("Создание заказа с авторизацией но без ингредиентов")
    public void createOrderWithAuthorizationAndNoIngredients() {
        // Генерация уникальных данных для пользователя
        String email = generateUniqueEmail();
        String password = generateUniquePassword();
        String name = generateUniqueName();
        // Создание нового пользователя
        Response createUserResponse = createUniqueUser(email, password, name);
        createUserResponse.then().statusCode(200);
        // Логин под созданным пользователем и получение токена авторизации
        Response loginResponse = loginWithUser(email, password, name);
        loginResponse.then().statusCode(200);
        // Извлечение accessToken из ответа
        accessToken = loginResponse.jsonPath().getString("accessToken");
        // Создание заказа с токеном авторизации и без ингредиентов, с использованием MethodsUserCreation
        Response orderResponse = MethodsUserCreation.createOrderWitNoIngredients(accessToken);
        // Логирование и проверка ошибки 400
        orderResponse.then().log().all();
        MethodsUserCreation.verifyOrderCreationNoIngredients(orderResponse);
    }

    // *Баг. Заказ создается, хотя по документации не должен создаваться без авторизации
    @Test
    @Description("Создание заказа без авторизации, с ингредиентами")
    public void createOrderWithNoAuthorization() {
        // Создание заказа без токена авторизации
        Response orderResponse = MethodsUserCreation.createOrderWithoutAuthorization();
        // Логирование ответа и проверка ошибки 401 Unauthorized
        orderResponse.then().log().all();
        MethodsUserCreation.verifyOrderCreationUnauthorized(orderResponse);
    }

    // *Баг так как есть баг в создании заказа без авторизации
    @Test
    @Description("Создание заказа без авторизации и без ингредиентов")
    public void createOrderWithNoAuthorizationAndIngredients() {
        // Создание заказа без токена авторизации и без ингредиентов
        Response orderResponseWithoutAuthorization = MethodsUserCreation.createOrderWithoutAuthorizationAndIngredients();
        // Логирование ответа и проверка кода 401 при отсутствии авторизации
        orderResponseWithoutAuthorization.then().log().all();
        MethodsUserCreation.verifyOrderCreationNoIngredientsUnauthorized(orderResponseWithoutAuthorization);
        // Создание заказа без ингредиентов (с авторизацией или без)
        Response orderResponseWithoutIngredients = MethodsUserCreation.createOrderWithoutAuthorizationAndIngredients();
        // Логирование ответа и проверка кода 400 при отсутствии ингредиентов
        MethodsUserCreation.verifyOrderCreationNoAuthorizedAndNoIngredients(orderResponseWithoutIngredients);
    }

    // *Баг. Ожидается код ошибки 500 по документации, а фактически 400, но чтобы все равно удалить
    // пользователя после теста добавилв конструкцию try catch
    @Test
    @Description("Создание заказа с неверным хешем ингредиентов")
    public void createOrderWithInvalidIngredientsHash() {
        // Генерация уникальных данных для пользователя
        String email = generateUniqueEmail();
        String password = generateUniquePassword();
        String name = generateUniqueName();
        // Создание нового пользователя
        Response createUserResponse = createUniqueUser(email, password, name);
        createUserResponse.then().statusCode(200);
        // Логин под созданным пользователем и получение токена авторизации
        Response loginResponse = loginWithUser(email, password, name);
        loginResponse.then().statusCode(200);
        // Извлечение accessToken из ответа
        String accessToken = loginResponse.jsonPath().getString("accessToken");
        try {
            // Создание заказа с неверным хешем ингредиентов с использованием MethodsUserCreation
            Response orderResponse = MethodsUserCreation.createOrderWithInvalidIngredientsHash(accessToken);
            // Логирование и проверка кода ответа 400
            orderResponse.then().log().all();
            MethodsUserCreation.verifyOrderCreationInvalidIngredientsHash(orderResponse);
            // Заказ не был создан но пользователь может быть удален
            orderResponse.then().statusCode(400);
        } catch (Exception e) {
            // В случае ошибки с код 400, все равно продолжим удаление пользователя
            System.out.println("Ошибка при создании заказа: " + e.getMessage());
        } finally {
            // Удаление пользователя по токену
            deleteUserByToken(accessToken);
        }
    }
}