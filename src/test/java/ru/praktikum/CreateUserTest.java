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

public class CreateUserTest {
    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }

    @Test
    @DisplayName("регистрация уникального пользователя")
    public void createUniqueUser() {
        // генерация данных пользователя
        User user = new User();
        JSONObject credentials = user.generateCredentials();
        // создание пользователя
        Response responseCreateUser = user.createUser(credentials);
        // извлечение токена
        String accessToken = responseCreateUser.then().extract().path("accessToken");
        // проверка токена
        assertThat(accessToken, startsWith("Bearer"));
        // печать токена
        System.out.println(accessToken);
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
