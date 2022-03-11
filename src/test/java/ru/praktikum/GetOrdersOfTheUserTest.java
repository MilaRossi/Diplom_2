package ru.praktikum;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertTrue;

public class GetOrdersOfTheUserTest {
    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }
    @Test
    @DisplayName("Получить заказы конкретного пользователя")
    public void getOrders() {
        // генерация данных пользователя
        User user = new User();
        JSONObject credentials = user.generateCredentials();
        // создание пользователя
        user.createUser(credentials);
        // логин пользователя
        Response responseLoginUser = user.loginUser(credentials);
        // извлечение токена
        String accessTokenWithBearer = responseLoginUser.then().extract().path("accessToken");
        String accessToken = accessTokenWithBearer.split(" ")[1];
        System.out.println(accessToken);

        // создать 2 заказa
        Order firstOrder = new Order();
        Order secondOrder = new Order();
        JSONObject firstIngredients = firstOrder.ingredientsForBurger();
        JSONObject secondIngredients = secondOrder.ingredientsForBurger();
        given().header("Content-type", "application/json").auth().oauth2(accessToken)
                .body(firstIngredients.toString()).when().post("/api/orders");
        given().header("Content-type", "application/json").auth().oauth2(accessToken)
                .body(secondIngredients.toString()).when().post("/api/orders");
        // получить заказы
        Response responseGetOrders = given().auth().oauth2(accessToken).get("/api/orders");
        System.out.println(responseGetOrders.asPrettyString());
        boolean success = responseGetOrders.then().extract().path("success");
        assertTrue(String.valueOf(success), true);

    }

}
