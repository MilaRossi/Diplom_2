package ru.praktikum;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertTrue;

public class OrderTest {
    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }
    @Test
    @DisplayName("создать заказ с авторизацией и ингредиентами")
    public void createOrderWithAuthAndIngredients() {
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
        // создать заказ
        Order order = new Order();
        JSONObject ingredients = order.ingredientsForBurger();
        Response responseOrderCreate = given().header("Content-type", "application/json").auth().oauth2(accessToken)
                .body(ingredients.toString()).when().post("/api/orders");
        Integer number = responseOrderCreate.then().extract().path("order.number");
        assertTrue(number > 0);
        System.out.println(number);
    }

    @Test
    @DisplayName("создать заказ с авторизацией и без ингредиентов")
    public void createOrderWithAuthAndWithoutIngredients() {
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
        // создать заказ
        Order order = new Order();
        JSONObject ingredients = order.emptyBurger();
        Response responseOrderCreate = given().header("Content-type", "application/json").auth().oauth2(accessToken)
                .body(ingredients.toString()).when().post("/api/orders");
        responseOrderCreate.then().assertThat().statusCode(400)
                .and().assertThat().body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("создать заказ с авторизацией и с неверным хешем ингредиентов")
    public void createOrderWithAuthAndWithIncorrectIngredients() {
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
        // создать заказ
        Order order = new Order();
        JSONObject ingredients = order.incorrectBurger();
        Response responseOrderCreate = given().header("Content-type", "application/json").auth().oauth2(accessToken)
                .body(ingredients.toString()).when().post("/api/orders");
        responseOrderCreate.then().assertThat().statusCode(500);
    }

    @Test
    @DisplayName("создать заказ без авторизации")
    public void createOrderWithoutAuth() {
        Order order = new Order();
        JSONObject ingredients = order.incorrectBurger();
        Response responseOrderCreate = given().header("Content-type", "application/json").auth().oauth2("accessToken")
                .body(ingredients.toString()).when().post("/api/orders");
        responseOrderCreate.then().assertThat().statusCode(403);
    }


}
