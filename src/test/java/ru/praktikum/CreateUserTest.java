package ru.praktikum;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CreateUserTest {
    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }

    @Test
    @DisplayName("регистрация уникального пользователя")
    public void registerUniqueUser() {
        // генерация данных пользователя
        User user = new User();
        JSONObject credentials = user.generateCredentials();
        // создание пользователя
        user.createUser(credentials);
        // логин пользователя
        Response responseLoginUser = user.loginUser(credentials);
        Boolean actualAnswer = responseLoginUser.then().extract().path("success");
        assertTrue("В ответе true", actualAnswer);

    }

    @Test
    @DisplayName("Получить ошибку создания пользователя, который уже зарегистрирован")
    public void createUserWithReservedCredentials() {
        // генерация данных пользователя
        User user = new User();
        JSONObject credentials = user.generateCredentials();
        // создание пользователя
        user.createUser(credentials);
        // повторное создание этого же пользователя
        Response responseCreateUserWithReservedCredentials = user.createUser(credentials);
        responseCreateUserWithReservedCredentials.then().assertThat().statusCode(403)
                .and().assertThat().body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Получить ошибку создания пользователя, который не указал имя")
    public void createUserWithEmptyName() {
        // генерация данных пользователя
        User user = new User();
        JSONObject credentials = user.generateCredentialsWithoutName();
        // создание пользователя
        Response responseCreateUserWithEmptyName = user.createUser(credentials);
        responseCreateUserWithEmptyName.then().assertThat().statusCode(403)
                .and().assertThat().body("message", equalTo("Email, password and name are required fields"));
    }


}
