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
/*
    public Recipe scrapeRecipe(String url) throws IOException {
        Document document = Jsoup.connect(url).get();

        Elements scripts = document.select("script[type=application/ld+json]");

        for (Element script : scripts) {
            //extract html from inside the script
            String json = script.html();
            //skip if it isn't recipe data
            if (!json.contains("\"@type\":\"Recipe\"")) {
                continue;
            }
            //create a new json object out of the raw text string extracted from the script
            JSONObject jsonObject = new JSONObject(json);


            //if the website is using a graph array, search it for recipe data too
            if (jsonObject.has("@graph")) {
                JSONArray graph = jsonObject.getJSONArray("@graph");
                for (int i = 0; i < graph.length(); i++) {
                    JSONObject node = graph.getJSONObject(i);
                    if ("Recipe".equals(node.optString("@type"))) {
                        jsonObject = node;
                        break;
                    }
                }
            }
            //print out the recipe title
            System.out.println("Title: " + jsonObject.optString("name"));

            //print out a bulleted list of recipe ingredients
            JSONArray ingredients = jsonObject.optJSONArray("recipeIngredient");
            if (ingredients != null) {
                System.out.println("Ingredients:");
                for (int i = 0; i < ingredients.length(); i++) {
                    System.out.println(" • " + ingredients.getString(i));
                }
            }

            //print out a numbered list of instructions
            Object instructionsRaw = jsonObject.opt("recipeInstructions");
            if (instructionsRaw instanceof JSONArray) {
                System.out.println("Instructions:");
                JSONArray instructions = (JSONArray) instructionsRaw;
                for (int i = 0; i < instructions.length(); i++) {
                    Object step = instructions.get(i);
                    if (step instanceof JSONObject) {
                        System.out.println((i + 1) + ". " + ((JSONObject) step).optString("text"));
                    } else {
                        System.out.println((i + 1) + ". " + step.toString());
                    }
                break;
        }

        Recipe recipe = new Recipe();
        return recipe;
        */
        }

