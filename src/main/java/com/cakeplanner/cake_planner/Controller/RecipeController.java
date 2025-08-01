package com.cakeplanner.cake_planner.Controller;

import com.cakeplanner.cake_planner.Model.DTO.RecipeDTO;
import com.cakeplanner.cake_planner.Model.Entities.Enums.RecipeType;
import com.cakeplanner.cake_planner.Model.Entities.User;
import com.cakeplanner.cake_planner.Model.Services.RecipeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
public class RecipeController {
    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping("/recipes/{recipeId}")
    public String showRecipeDetails(@PathVariable Integer recipeId, Model model) {
        RecipeDTO recipeDTO = recipeService.recipeToDTO(recipeId);
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
