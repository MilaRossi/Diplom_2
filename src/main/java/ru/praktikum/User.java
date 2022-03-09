package ru.praktikum;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import com.github.javafaker.Faker;

import static io.restassured.RestAssured.given;

public class User {
    @Step("создание кредов mail/password/name")
    public JSONObject generateCredentials() {
        Faker faker = new Faker();
        String userEmail = faker.internet().emailAddress();
        String userPassword = RandomStringUtils.randomAlphabetic(10);
        String userName = RandomStringUtils.randomAlphabetic(10);
        JSONObject json = new JSONObject();
        json.put("email", userEmail);
        json.put("password",userPassword );
        json.put("name",userName );
        return json;
    }

    // создание кредов mail/password
    public JSONObject generateCredentialsWithoutName() {
        Faker faker = new Faker();
        String userEmail = faker.internet().emailAddress();
        String userPassword = RandomStringUtils.randomAlphabetic(10);
        JSONObject json = new JSONObject();
        json.put("email", userEmail);
        json.put("password",userPassword );
        json.put("name","" );
        return json;
    }

    // создание кредов mail/password
    public JSONObject generateCredentialsWithoutPassword() {
        Faker faker = new Faker();
        String userEmail = faker.internet().emailAddress();
        JSONObject json = new JSONObject();
        json.put("email", userEmail);
        json.put("password","" );
        return json;
    }



    @Step("создание пользователя")
    public Response createUser(JSONObject credentials ) {
        return given().header("Content-type", "application/json")
                .body(credentials.toString()).when().post("/api/auth/register");
    }


    @Step("логин пользователя")
    public Response loginUser(JSONObject credentials ) {
        return given().header("Content-type", "application/json")
                .body(credentials.toString()).when().post("/api/auth/login");
    }




}
