package com.example.android.mybakingapp.util;

import com.example.android.mybakingapp.model.Ingredient;
import com.example.android.mybakingapp.model.Receipe;
import com.example.android.mybakingapp.model.Step;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonReceipeUtil {

    /**
     * Method that given a json string parses it into a receipe list
     *
     * @param receipeJsonStr - json string received from movie server
     * @return - receipe list
     */
    public static List<Receipe> getReceipeFromJson(String receipeJsonStr) {
        List<Receipe> parsedReceipeData = new ArrayList<>();
        try {
            JSONArray items = new JSONArray(receipeJsonStr);
            JSONObject movieObj;
            for (int i = 0; i < items.length(); i++) {
                movieObj = items.getJSONObject(i);
                Receipe receipe = new Receipe();
                receipe.setId(Long.parseLong(movieObj.getString("id")));
                receipe.setName(movieObj.getString("name"));
                receipe.setServings(movieObj.getInt("servings"));
                receipe.setImage(movieObj.getString("image"));

                JSONArray ingredients = movieObj.getJSONArray("ingredients");
                Ingredient[] ingredientsArray = new Ingredient[ingredients.length()];
                JSONObject ingredientObj;
                Ingredient ingredient;
                for (int j = 0; j < ingredients.length(); j++) {
                    ingredient = new Ingredient();
                    ingredientObj = ingredients.getJSONObject(j);
                    ingredient.setQuantity(Float.parseFloat(ingredientObj.getString("quantity")));
                    ingredient.setMeasure(ingredientObj.getString("measure"));
                    ingredient.setIngredient(ingredientObj.getString("ingredient"));
                    ingredientsArray[j] = ingredient;
                }

                receipe.setIngredients(ingredientsArray);

                JSONArray steps = movieObj.getJSONArray("steps");
                Step[] stepsArray = new Step[steps.length()];
                JSONObject stepObj;
                Step step;
                for (int j = 0; j < steps.length(); j++) {
                    step = new Step();
                    stepObj = steps.getJSONObject(j);
                    step.setId(Long.parseLong(stepObj.getString("id")));
                    step.setShortDescription(stepObj.getString("shortDescription"));
                    step.setDescription(stepObj.getString("description"));
                    step.setVideoURL(stepObj.getString("videoURL"));
                    step.setThumbnailURL(stepObj.getString("thumbnailURL"));
                    stepsArray[j] = step;
                }

                receipe.setSteps(stepsArray);
                parsedReceipeData.add(receipe);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return parsedReceipeData;
    }
}
