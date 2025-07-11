package com.cakeplanner.cake_planner.Model.Services;

import com.cakeplanner.cake_planner.Model.Entities.Recipe;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.json.JSONObject;
import org.json.JSONArray;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class RecipeScraperService {
        //FIXME finish json scraping using jsoup

        public void scrapeRecipe() throws IOException {
                //FIXME eventually pass url to method as string from newRecipe form
                String url = "https://www.allrecipes.com/recipe/174347/quick-and-almost-professional-buttercream-icing/";
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
                        System.out.println("Json object not found, possibly in array of Json objects");
                }

        }

                public JSONObject filterRecipeData(Elements scripts) {
                        //for every element in the scripts, check to see if it is of type "Recipe"
                        for (Element script : scripts) {
                                //extract html from inside the script
                                String json = script.html();
                                //skip if it isn't recipe data
                                if (!json.contains("\"@type\":\"Recipe\"")) {
                                        continue;
                                }

        Object rawJson = new org.json.JSONTokener(json).nextValue();

        // If it's already a recipe object
        if (rawJson instanceof JSONObject jsonObject) {
            // Handle @graph array
            if (jsonObject.has("@graph")) {
                JSONArray graph = jsonObject.getJSONArray("@graph");
                for (int i = 0; i < graph.length(); i++) {
                    JSONObject node = graph.getJSONObject(i);
                    if ("Recipe".equals(node.optString("@type"))) {
                        return node;
                    }
                        }
            }

            // Fallback if this is directly a recipe
            if ("Recipe".equals(jsonObject.optString("@type"))) {
                        return jsonObject;
                }
        }
    }

    return null; // Nothing found
}


                public void showIngredients(JSONObject jsonObject){
                        JSONArray ingredients = jsonObject.optJSONArray("recipeIngredient");
                        if (ingredients != null) {
                                System.out.println("Ingredients:");
                                for (int i = 0; i < ingredients.length(); i++) {
                                        System.out.println(" • " + ingredients.getString(i));
                                }
                        } else{
                                System.out.println("Ingredients not found");
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
