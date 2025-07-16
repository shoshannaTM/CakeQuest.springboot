package com.cakeplanner.cake_planner.Model.Services;

//check if recipe already exists by url, adds info to recipe table, ingredients table, and recipeIngredient table
//Return a recipeDTO object (with ingredientDTO included) to controller to display recipe in UI

import com.cakeplanner.cake_planner.Model.DTO.RecipeDTO;
import com.cakeplanner.cake_planner.Model.Repositories.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class RecipeService {
    @Autowired
    RecipeScraperService recipeScraperService;
    @Autowired
    RecipeRepository recipeRepository;

    public void processRecipeForDisplay(String recipeUrl, String recipeType) throws IOException {
        // Use url to check if recipe exists in table before scraping
        if(recipeRepository.findRecipeByRecipeUrl(recipeUrl).isEmpty()) {
            RecipeDTO recipeDTO = recipeScraperService.scrapeRecipe(recipeUrl, recipeType);
        } else {
            System.out.println("Recipe already in table");
            System.out.println(recipeRepository.findRecipeByRecipeUrl(recipeUrl));
        }

    }

}
