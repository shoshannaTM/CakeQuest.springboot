package com.cakeplanner.cake_planner.Controller;

import com.cakeplanner.cake_planner.Model.DTO.RecipeDTO;
import com.cakeplanner.cake_planner.Model.Entities.Enums.RecipeType;
import com.cakeplanner.cake_planner.Model.Repositories.RecipeRepository;
import com.cakeplanner.cake_planner.Model.Services.RecipeScraperService;
import com.cakeplanner.cake_planner.Model.Services.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@Controller
public class RecipeController {
    @Autowired
    private RecipeScraperService recipeScraperService;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private RecipeService recipeService;
    //FIXME
    @GetMapping("/recipes/1")
    public String showUpdateForm(Model model) {
        return "recipeDetails";
    }

    @GetMapping("/recipes/new")
    public String showNewForm(Model model) throws IOException {
        model.addAttribute("recipeTypes", RecipeType.values());
        return "newRecipe";
    }

    @PostMapping("/recipes/new")
    public String handleNewRecipe(@RequestParam("recipeUrl") String recipeUrl,
                                @RequestParam("recipeType") RecipeType recipeType, Model model) throws IOException {
        RecipeDTO displayRecipe = recipeService.processRecipeForDisplay(recipeUrl, recipeType);
        model.addAttribute("recipe", displayRecipe);
        return "recipeDetails";
    }
}
