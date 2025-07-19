package com.cakeplanner.cake_planner.Controller;
import com.cakeplanner.cake_planner.Model.DTO.IngredientDTO;
import com.cakeplanner.cake_planner.Model.DTO.RecipeDTO;
import com.cakeplanner.cake_planner.Model.Entities.DummyCakes;
import com.cakeplanner.cake_planner.Model.Entities.Enums.RecipeType;
import com.cakeplanner.cake_planner.Model.Entities.Recipe;
import com.cakeplanner.cake_planner.Model.Entities.RecipeIngredient;
import com.cakeplanner.cake_planner.Model.Entities.User;
import com.cakeplanner.cake_planner.Model.Repositories.RecipeIngredientRepository;
import com.cakeplanner.cake_planner.Model.Repositories.RecipeRepository;
import com.cakeplanner.cake_planner.Model.Services.RecipeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
public class NavController {
    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private RecipeIngredientRepository recipeIngredientRepository;

    @Autowired
    private RecipeService recipeService;
    @GetMapping("/")
    public String showCakes(Model model) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy @ HH:mm");

        List<DummyCakes> cakes = new ArrayList<>();

            cakes.add(new DummyCakes(
                    "Sophie's Unicorn Cake",
                    "Vanilla",
                    "Strawberry Jam",
                    "Buttercream",
                    LocalDateTime.now().plusDays(2).format(formatter),
                    25,
                    1
            ));

            cakes.add(new DummyCakes(
                    "Dad's Retirement Cake",
                    "Chocolate",
                    "Salted Caramel",
                    "Ganache",
                    LocalDateTime.now().plusDays(5).format(formatter),
                    60,
                    2
            ));

            cakes.add(new DummyCakes(
                    "Wedding Cake",
                    "Red Velvet",
                    "Cream Cheese",
                    "Fondant",
                    LocalDateTime.now().plusWeeks(1).format(formatter),
                    90,
                    3
            ));

            model.addAttribute("cakes", cakes);
            return "home";
    }

    @GetMapping("/recipes")
    public String recipes(@RequestParam(name = "type", required = false, defaultValue = "all") String type,
                          @RequestParam(name = "query", required = false) String query,
                          @ModelAttribute("user") User user,
                          Model model) {
        List<Recipe> recipes;
        // if search bar has input
        if (query != null && !query.isBlank()) {
                type = "all";
                recipes = recipeRepository.findByRecipeNameContainingIgnoreCaseAndUser(query, user);
        } else {
            // No search, just filter by type
            if (type.equalsIgnoreCase("all")) {
                recipes = recipeRepository.findAllByUser(user);
            } else {
                try {
                    RecipeType recipeType = RecipeType.valueOf(type.toUpperCase());
                    recipes = recipeRepository.findByRecipeTypeAndUser(recipeType, user);
                } catch (IllegalArgumentException e) {
                    recipes = new ArrayList<>();
                }
            }
        }

        List<RecipeDTO> displayRecipes = new ArrayList<>();
    for (Recipe recipe : recipes) {
            List<RecipeIngredient> recipeIngredientsList = recipeIngredientRepository.findRecipeIngredientsByRecipeId(recipe.getRecipeId());
            List <IngredientDTO> ingredientDTOList = recipeService.recipeIngredientsToDTO(recipeIngredientsList);
            RecipeDTO recipeDTO = new RecipeDTO(recipe.getRecipeName(), recipe.getRecipeUrl(), recipe.getInstructions(),
                                                recipe.getRecipeType(), ingredientDTOList, recipe.getRecipeId());
            displayRecipes.add(recipeDTO);
        }

        model.addAttribute("recipes", displayRecipes);
        model.addAttribute("selectedType", type);
        model.addAttribute("query", query);
        return "recipes";
    }

    @GetMapping("/shopping")
    public String shopping(){return "shopping";}

    @GetMapping("/profile")
    public String profile(){return "profile";}
}
