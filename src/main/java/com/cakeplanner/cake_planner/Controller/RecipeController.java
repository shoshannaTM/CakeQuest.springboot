package com.cakeplanner.cake_planner.Controller;

import com.cakeplanner.cake_planner.Model.DTO.IngredientDTO;
import com.cakeplanner.cake_planner.Model.DTO.RecipeDTO;
import com.cakeplanner.cake_planner.Model.Entities.Enums.RecipeType;
import com.cakeplanner.cake_planner.Model.Entities.Recipe;
import com.cakeplanner.cake_planner.Model.Entities.RecipeIngredient;
import com.cakeplanner.cake_planner.Model.Entities.User;
import com.cakeplanner.cake_planner.Model.Repositories.RecipeIngredientRepository;
import com.cakeplanner.cake_planner.Model.Repositories.RecipeRepository;
import com.cakeplanner.cake_planner.Model.Services.RecipeScraperService;
import com.cakeplanner.cake_planner.Model.Services.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
public class RecipeController {
    @Autowired
    private RecipeScraperService recipeScraperService;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private RecipeIngredientRepository recipeIngredientRepository;
    @Autowired
    private RecipeService recipeService;
    //FIXME
    @GetMapping("/recipes/{recipeId}")
    public String showRecipeDetails(@PathVariable Integer recipeId, Model model) {
        // Fetch the recipe entity from the database
        Recipe recipe = recipeRepository.findRecipeByRecipeId(recipeId);

        if (recipe == null) {
            // Optional: return a custom error page or redirect
            return "error/404";
        }
        //fIXME should have a method in RecipeService
        List<RecipeIngredient> recipeIngredients = recipeIngredientRepository.findRecipeIngredientsByRecipeId(recipeId);
        List<IngredientDTO> ingredientDTOList = recipeService.recipeIngredientsToDTO(recipeIngredients);

        RecipeDTO recipeDTO = new RecipeDTO(recipe.getRecipeName(), recipe.getRecipeUrl(), recipe.getInstructions(),
                recipe.getRecipeType(), ingredientDTOList, recipe.getRecipeId());


        model.addAttribute("recipe", recipeDTO);
        model.addAttribute("mode", "read");
        return "recipeDetails";
    }


    @GetMapping("/recipes/new")
    public String showNewForm(Model model) throws IOException {
        model.addAttribute("recipeTypes", RecipeType.values());
        return "newRecipe";
    }

    @PostMapping("/recipes/new")
    public String handleNewRecipe(@RequestParam("recipeUrl") String recipeUrl,
                                    @RequestParam("recipeType") RecipeType recipeType,
                                    @ModelAttribute("user") User user,
                                    Model model) throws IOException {
        RecipeDTO displayRecipe = recipeService.processRecipeForDisplay(recipeUrl, recipeType, user);
        model.addAttribute("recipe", displayRecipe);
        return "recipeDetails";
    }
}
