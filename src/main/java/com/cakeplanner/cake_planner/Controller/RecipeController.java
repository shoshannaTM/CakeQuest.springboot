package com.cakeplanner.cake_planner.Controller;

import com.cakeplanner.cake_planner.Model.Services.RecipeScraperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@Controller
public class RecipeController {
    @Autowired
    private RecipeScraperService recipeScraperService;

    //FIXME
    @GetMapping("/recipes/1")
    public String showUpdateForm(Model model) {
        return "recipeDetails";
    }

    @GetMapping("/recipes/new")
    public String showNewForm(Model model) throws IOException {
        return "newRecipe";
    }
}
