package com.cakeplanner.cake_planner.Model.Services;

import com.cakeplanner.cake_planner.Model.Entities.Ingredient;
import com.cakeplanner.cake_planner.Model.Entities.Recipe;
import com.cakeplanner.cake_planner.Model.Repositories.IngredientRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.json.JSONObject;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class RecipeScraperService {
    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private SpoonacularService spoonacularService;
        public void scrapeRecipe(String url) throws IOException {
                //get document from url
                Document document = Jsoup.connect(url).get();

                //get json scripts from document and store them in a jsoup Elements object, by querying the css using .select method from jsoup
                //(Elements is a jsoup object that represents a list of jsoup Element objects, an Element = an HTML tag)
                Elements scripts = document.select("script[type=application/ld+json]");

                JSONObject jsonObject = filterRecipeData(scripts);
                if(jsonObject != null){
                        showIngredients(jsonObject);
                        showInstructions(jsonObject);
                }else{
                        System.out.println("Json object null");
                }

        }
    public JSONObject filterRecipeData(Elements scripts) {
        for (Element script : scripts) {
            String json = script.data();

            // Skip if no "Recipe" anywhere in the string
            if (!json.contains("Recipe")) continue;

            try {
                Object rawJson = new org.json.JSONTokener(json).nextValue();
                // 1. If it's an array (like on AllRecipes)
                if (rawJson instanceof JSONArray array) {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject node = array.getJSONObject(i);
                        if (isRecipeType(node)) return node;
                    }
                }

                // 2. If it's a single object (or a @graph container)
                if (rawJson instanceof JSONObject jsonObject) {
                    // Look inside @graph if present
                    if (jsonObject.has("@graph")) {
                        JSONArray graph = jsonObject.getJSONArray("@graph");
                        for (int i = 0; i < graph.length(); i++) {
                            JSONObject node = graph.getJSONObject(i);
                            if (isRecipeType(node)) return node;
                        }
                    }

                    // Fallback: check top-level object
                    if (isRecipeType(jsonObject)) return jsonObject;
                }
            } catch (Exception e) {
                System.out.println("Error parsing script JSON: " + e.getMessage());
            }
        }

        return null; // no match found
    }

    private boolean isRecipeType(JSONObject node) {
        Object type = node.opt("@type");

        if (type instanceof String) {
            return "Recipe".equals(type);
        }

        if (type instanceof JSONArray typeArray) {
            for (int j = 0; j < typeArray.length(); j++) {
                if ("Recipe".equals(typeArray.optString(j))) {
                    return true;
                }
            }
        }

        return false;
    }



    public void showIngredients(JSONObject jsonObject){
                        JSONArray ingredients = jsonObject.optJSONArray("recipeIngredient");
                        List<String> ingredientList = new ArrayList<>();

                        if (ingredients != null) {
                                System.out.println("Ingredients:");
                                for (int i = 0; i < ingredients.length(); i++) {
                                    String ingredient = ingredients.getString(i);
                                    ingredientList.add(ingredient);
                                    System.out.println(" • " + ingredients.getString(i));
                                }
                        } else{
                                System.out.println("Ingredients not found");
                        }
                        String parsedIngredients = spoonacularService.parseIngredients(ingredientList);
                        System.out.println(parsedIngredients);
    }

    //FIXME this checks the ingredients table to see if this ingredient already exists, needs to be implemented still
    public void addIngredient(String ingredientName){
            if(ingredientRepository.findByIngredientNameIgnoreCase(ingredientName).isEmpty()){
                Ingredient ingredient = new Ingredient();
                ingredient.setIngredientName(ingredientName);
                ingredientRepository.save(ingredient);
        } else {
                System.out.println("ingredient found, not added");
            }

    }

    public void showInstructions(JSONObject jsonObject) {
            Object instructionsRaw = jsonObject.opt("recipeInstructions");
            //if the instructions are held in an array, cast the object to json array to traverse it
            if (instructionsRaw instanceof JSONArray) {
                System.out.println("Instructions:");
                JSONArray instructions = (JSONArray) instructionsRaw;

                //for every item in the array, format it and print it out
                for (int i = 0; i < instructions.length(); i++) {
                    Object step = instructions.get(i);

                    // If it's a one dimensional array, parse out the steps in the array and print
                    if (step instanceof JSONObject) {
                        JSONObject stepObj = (JSONObject) step;

                        // check for nested array using item list element, then print those if they exist
                        if (stepObj.has("itemListElement")) {
                            JSONArray subSteps = stepObj.getJSONArray("itemListElement");

                            for (int j = 0; j < subSteps.length(); j++) {
                                JSONObject subStep = subSteps.getJSONObject(j);
                                System.out.println((i + 1) + "." + (j + 1) + " " + subStep.optString("text"));
                            }
                            //if just a normal step, not array, print as a string
                        } else {
                            // Normal step
                            System.out.println((i + 1) + ". " + stepObj.optString("text"));
                        }
                        //this is to catch unexpected format
                    } else {
                        System.out.println((i + 1) + ". " + step.toString());
                    }

                }
            } else {
                System.out.println("instructions not found");
            }
        }

}
