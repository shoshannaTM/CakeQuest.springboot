package com.cakeplanner.cake_planner.Model.Services;

import com.cakeplanner.cake_planner.Model.DTO.IngredientDTO;
import com.cakeplanner.cake_planner.Model.DTO.RecipeDTO;
import com.cakeplanner.cake_planner.Model.Entities.Enums.RecipeType;
import com.cakeplanner.cake_planner.Model.Entities.Ingredient;
import com.cakeplanner.cake_planner.Model.Entities.Recipe;
import com.cakeplanner.cake_planner.Model.Entities.RecipeIngredient;
import com.cakeplanner.cake_planner.Model.Repositories.IngredientRepository;
import jakarta.transaction.Transactional;
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
import java.util.Optional;

// Uses jsoup to scrape recipe from URL once recipeService certifies that recipe doesn't already exist
// Calls Spoonacular to process ingredients. Returns Recipe.

@Service
public class RecipeScraperService {
        private final IngredientRepository ingredientRepository;
        private final SpoonacularService spoonacularService;

        public RecipeScraperService(IngredientRepository ingredientRepository,
                                    SpoonacularService spoonacularService) {
            this.ingredientRepository = ingredientRepository;
            this.spoonacularService = spoonacularService;
        }

        @Transactional
        public Recipe scrapeRecipe(String url) throws IOException {
        //get document from url
        Document document = Jsoup.connect(url).ignoreHttpErrors(true).get();

        //get json scripts from document and store them in a jsoup Elements object, by querying the css using .select method from jsoup
        //(Elements is a jsoup object that represents a list of jsoup Element objects, an Element = an HTML tag)
        Elements scripts = document.select("script[type=application/ld+json]");

        JSONObject jsonObject = filterRecipeData(scripts);

        Recipe recipe = new Recipe();
        if (jsonObject != null) {
            List<RecipeIngredient> ingredients = extractIngredients(jsonObject, recipe);
            String instructions = extractInstructions(jsonObject);
            String recipeName = jsonObject.optString("name", "No Name Found");

            recipe.setRecipeUrl(url);
            recipe.setBaseRecipeName(recipeName);
            recipe.setBaseRecipeInstructions(instructions);
            recipe.setBaseRecipeIngredients(ingredients);
        } else {
            System.out.println("Json object null");
        }
        return recipe;
    }

    public List<RecipeIngredient> extractIngredients(JSONObject jsonObject, Recipe recipe){
        JSONArray ingredients = jsonObject.optJSONArray("recipeIngredient");
        List<String> ingredientList = new ArrayList<>();

        if (ingredients != null) {
            //System.out.println("Ingredients:");
            for (int i = 0; i < ingredients.length(); i++) {
                String ingredient = ingredients.getString(i);
                ingredientList.add(ingredient);
                //System.out.println(" • " + ingredients.getString(i));
            }
        } else{
            System.out.println("Ingredients not found");
        }
        String parsedIngredients = spoonacularService.parseIngredients(ingredientList);
        //System.out.println(parsedIngredients);
        // System.out.println("--------------------");
        List<RecipeIngredient> recipeIngredients = stringToRecipeIngredientList(parsedIngredients, recipe);
        return recipeIngredients;
    }

    public List<RecipeIngredient> stringToRecipeIngredientList(String parsedIngredients, Recipe recipe){
        List<RecipeIngredient> simplifiedList = new ArrayList<>();
        JSONArray ingredientsArray = new JSONArray(parsedIngredients);

        for(int i = 0; i < ingredientsArray.length(); i++) {
            JSONObject ingredientObj = ingredientsArray.getJSONObject(i);
            // Extract fields
            String name = ingredientObj.optString("name");
            double amount = ingredientObj.optDouble("amount");
            String unit = ingredientObj.optString("unit");

            if (name.isEmpty() || Double.isNaN(amount) || amount <= 0) {
                  continue;
            }

            Optional<Ingredient> optionalIngredient = ingredientRepository.findByIngredientNameIgnoreCase(name);

            Ingredient ingredient;
            if (optionalIngredient.isPresent()) {
                ingredient = optionalIngredient.get();
            } else {
                ingredient = ingredientRepository.save(new Ingredient(name));
            }

            simplifiedList.add(new RecipeIngredient(recipe, ingredient, amount, unit));

            // Print or process as needed
            //System.out.println("Ingredient: " + name);
            //System.out.println("Amount: " + amount);
            //System.out.println("Unit: " + unit);
            //System.out.println("-----");
            // Optional: Save to DB or add to list for later
        }
        //System.out.println(simplifiedList);
        return simplifiedList;
    }

    public String extractInstructions(JSONObject jsonObject) {
        Object instructionsRaw = jsonObject.opt("recipeInstructions");
        String allSteps = "";
        //if the instructions are held in an array, cast the object to json array to traverse it
        if (instructionsRaw instanceof JSONArray) {
            JSONArray instructions = (JSONArray) instructionsRaw;
            //for every item in the array, add it to a string, steps separated by "|"
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
                            allSteps = allSteps + "|" + subStep.optString("text");
                        }
                        //if just a normal step, not array, print as a string
                    } else {
                        // Normal step
                        allSteps = allSteps + "|"  + stepObj.optString("text");
                    }
                    //this is to catch unexpected format
                } else {
                    allSteps = allSteps + "|" + step.toString();
                }

            }
        } else {
            System.out.println("instructions not found");
        }
        return allSteps;
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
}
