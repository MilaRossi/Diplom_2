package ru.praktikum;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static io.restassured.RestAssured.given;

public class Order {
    @Step("составить бургер")
    // получить ингредиенты
    public Response getIngredients() {
        return given().get("/api/ingredients");
    }
    // вытащить массив данных
    ArrayList<HashMap> ingredients = getIngredients().then().extract().path("data");
    // первый ингредиент
    String firstIngredientId = ingredients.get(0).get("_id").toString();
    // второй ингредиент
    String secondIngredientId = ingredients.get(1).get("_id").toString();
    // создать массив ингредиентов для бургера
    String[] burgerIngredients = {firstIngredientId, secondIngredientId};
    // создать тело запроса
    public JSONObject ingredientsForBurger() {
        JSONObject json = new JSONObject();
        json.put("ingredients", burgerIngredients);
        return json;
    }

    @Step("составить пустой бургер")
    public JSONObject emptyBurger() {
        String[] noIngredients = {};
        JSONObject json = new JSONObject();
        json.put("ingredients", noIngredients);
        return json;
    }

    @Step("составить бургер с неверным хешем ингредиентов")
    public JSONObject incorrectBurger() {
        String[] incorrectIngredients = {"firstIncorrectIngredient", "secondIncorrectIngredient"};
        JSONObject json = new JSONObject();
        json.put("ingredients", incorrectIngredients);
        return json;
    }


}
