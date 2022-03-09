package ru.praktikum;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;

public class LoginUserTest {
    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }
    @Test
    @DisplayName("успешный логин пользователя")
    public void successfulLoginUser() {
        // генерация данных пользователя
        User user = new User();
        JSONObject credentials = user.generateCredentials();
        // создание пользователя
        user.createUser(credentials);
        // логин пользователя
        Response responseLoginUser = user.loginUser(credentials);
        // извлечение токена
        String accessToken = responseLoginUser.then().extract().path("accessToken");
        // проверка токена
        assertThat(accessToken, startsWith("Bearer"));
        // печать токена
        System.out.println(accessToken);

    }

    @Test
    @DisplayName("Получить ошибку логина пользователя при указании неверного пароля")
    public void loginUserWithIncorrectPassword() {
        // генерация данных пользователя
        User user = new User();
        JSONObject credentials = user.generateCredentials();
        // создание пользователя
        user.createUser(credentials);
        // логин пользователя
        Response responseLoginUser = user.loginUser(credentials);
        responseLoginUser.then().assertThat().statusCode(401)
                .and().assertThat().body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Получить ошибку логина пользователя без указания пароля")
    public void loginUserWithEmptyPassword() {
        // генерация данных пользователя
        User user = new User();
        JSONObject credentials = user.generateCredentialsWithoutPassword();
        // создание пользователя
        user.createUser(credentials);
        // логин пользователя
        Response responseLoginUser = user.loginUser(credentials);
        responseLoginUser.then().assertThat().statusCode(401)
                .and().assertThat().body("message", equalTo("email or password are incorrect"));
    }


}
