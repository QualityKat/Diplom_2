package apiTests;

import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UserOrdersTest extends MethodsUserCreation {

    private String accessToken;

    @Before
    public void setUp() {
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
        this.accessToken = loginResponse.jsonPath().getString("accessToken");
    }

    @After
    public void tearDown() {
        // Удаление пользователя по токену
        if (accessToken != null) {
            deleteUserByToken(accessToken);
        }
    }

    @Test
    @Description("Получение заказов авторизованного пользователя")
    public void getUserOrdersWithAuthorization() {
        // Создание заказа с токеном авторизации с использованием MethodsUserCreation
        Response orderResponse = MethodsUserCreation.createOrderWithIngredients(accessToken);

        // Логирование и проверка успешного создания заказа
        orderResponse.then().log().all();
        MethodsUserCreation.verifyOrderCreation(orderResponse);

        // Получение заказов авторизованного пользователя
        Response ordersResponse = MethodsUserCreation.getUserOrders(accessToken);

        // Проверка успешного получения заказов
        MethodsUserCreation.verifyUserOrdersRetrieval(ordersResponse);
    }

    @Test
    @Description("Проверка создания заказа и получения списка заказов без авторизации")
    public void createOrderAndGetUserOrdersWithoutAuthorization() {
        // Попытка получения списка заказов без авторизации
        Response ordersResponse = MethodsUserCreation.getUserOrdersWithoutAuthorization();

        // Проверка что запрос без авторизации возвращает ошибку 401 Unauthorized
        MethodsUserCreation.verifyUnauthorizedResponse(ordersResponse);
    }
}
