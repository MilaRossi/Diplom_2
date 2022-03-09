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

public class ChangeUserTest {
    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }
    @Test
    @DisplayName("изменить имя")
    public void successfullyChangeTheName() {
        // генерация данных пользователя
        User user = new User();
        JSONObject credentials = user.generateCredentials();
        // создание пользователя
        user.createUser(credentials);
        // логин пользователя
        Response responseLoginUser = user.loginUser(credentials);
        // извлечение имени
        String oldName = responseLoginUser.then().extract().path("user.name");
        // извлечение токена
        String accessTokenWithBearer = responseLoginUser.then().extract().path("accessToken");
        String accessToken = accessTokenWithBearer.split(" ")[1];
        // изменить имя
        JSONObject credentialsWithNewName = user.changeTheName();
        Response responseChangeTheName = given().header("Content-type", "application/json").auth().oauth2(accessToken)
                .and().body(credentialsWithNewName.toString()).when().patch("/api/auth/user");
        // извлечение нового имени
        String newName = responseChangeTheName.then().extract().path("user.name");
        assertTrue(oldName != newName);
        System.out.println(oldName);
        System.out.println(newName);

    }

    @Test
    @DisplayName("изменить email")
    public void successfullyChangeTheEmail() {
        // генерация данных пользователя
        User user = new User();
        JSONObject credentials = user.generateCredentials();
        // создание пользователя
        user.createUser(credentials);
        // логин пользователя
        Response responseLoginUser = user.loginUser(credentials);
        // извлечение email
        String oldEmail = responseLoginUser.then().extract().path("user.email");
        // извлечение токена
        String accessTokenWithBearer = responseLoginUser.then().extract().path("accessToken");
        String accessToken = accessTokenWithBearer.split(" ")[1];
        // изменить email
        JSONObject credentialsWithNewEmail = user.changeTheEmail();
        Response responseChangeTheEmail = given().header("Content-type", "application/json").auth().oauth2(accessToken)
                .and().body(credentialsWithNewEmail.toString()).when().patch("/api/auth/user");
        // извлечение нового имени
        String newEmail = responseChangeTheEmail.then().extract().path("user.email");
        assertTrue(oldEmail != newEmail);
        System.out.println(oldEmail);
        System.out.println(newEmail);

    }

    @Test
    @DisplayName("получить ошибку при изменении имени без авторизации")
    public void getTheError401Unauthorized() {
        // генерация данных пользователя
        User user = new User();
        JSONObject credentials = user.generateCredentials();
        // создание пользователя
        user.createUser(credentials);
        // логин пользователя
        user.loginUser(credentials);
        // изменить имя
        JSONObject credentialsWithNewName = user.changeTheName();
        Response responseChangeTheName = given().header("Content-type", "application/json").auth().oauth2("")
                .and().body(credentialsWithNewName.toString()).when().patch("/api/auth/user");
        responseChangeTheName.then().assertThat().statusCode(401)
                .and().assertThat().body("message", equalTo("You should be authorised"));
        System.out.println(responseChangeTheName.asPrettyString());

    }

    @Test
    @DisplayName("передать почту, которая уже используется, и получить ошибку")
    public void getTheError403Forbidden() {
        // генерация данных пользователя
        User user1 = new User();
        User user2 = new User();
        JSONObject credentialsFirstUser = user1.generateCredentials();
        JSONObject credentialsSecondUser = user2.generateCredentials();
        // создание пользователя
        user1.createUser(credentialsFirstUser);
        user2.createUser(credentialsSecondUser);
        // логин второго пользователя
        Response responseLoginUser = user2.loginUser(credentialsSecondUser);
        // извлечение токена второго пользователя
        String accessTokenWithBearer = responseLoginUser.then().extract().path("accessToken");
        String accessToken = accessTokenWithBearer.split(" ")[1];
        // изменить email второго пользователя на email первого
        Response responseChangeTheEmail = given().header("Content-type", "application/json").auth().oauth2(accessToken)
                .and().body(credentialsFirstUser.toString()).when().patch("/api/auth/user");
        System.out.println(responseChangeTheEmail.asPrettyString());
        responseChangeTheEmail.then().assertThat().statusCode(403)
                .and().assertThat().body("message", equalTo("User with such email already exists"));

    }

}
