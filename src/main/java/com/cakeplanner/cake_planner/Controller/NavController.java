package com.cakeplanner.cake_planner.Controller;
import com.cakeplanner.cake_planner.Model.DTO.IngredientDTO;
import com.cakeplanner.cake_planner.Model.DTO.RecipeDTO;
import com.cakeplanner.cake_planner.Model.Entities.*;
import com.cakeplanner.cake_planner.Model.Entities.Enums.RecipeType;
import com.cakeplanner.cake_planner.Model.Repositories.RecipeIngredientRepository;
import com.cakeplanner.cake_planner.Model.Repositories.RecipeRepository;
import com.cakeplanner.cake_planner.Model.Repositories.UserRecipeRepository;
import com.cakeplanner.cake_planner.Model.Services.RecipeService;
import jakarta.servlet.http.HttpSession;
import jdk.jfr.StackTrace;
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
    private UserRecipeRepository userRecipeRepository;

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

        List<UserRecipe> userRecipes;

        if (query != null && !query.isBlank()) {
            // Search by name for this user
            userRecipes = userRecipeRepository.findByUserAndRecipe_RecipeNameContainingIgnoreCase(user, query);
        } else if (type.equalsIgnoreCase("all")) {
            // All recipes for this user
            userRecipes = userRecipeRepository.findByUser(user);
        } else {
            try {
                RecipeType recipeType = RecipeType.valueOf(type.toUpperCase());
                userRecipes = userRecipeRepository.findByUserAndRecipe_RecipeType(user, recipeType);
            } catch (IllegalArgumentException e) {
                userRecipes = new ArrayList<>();
            }
        }

        // Convert UserRecipe to RecipeDTO for display
        List<RecipeDTO> displayRecipes = new ArrayList<>();
        for (UserRecipe ur : userRecipes) {
            Recipe recipe = ur.getRecipe();
            List<RecipeIngredient> ingredients = recipeIngredientRepository.findRecipeIngredientsByRecipeId(recipe.getRecipeId());
            List<IngredientDTO> ingredientDTOs = recipeService.recipeIngredientsToDTO(ingredients);

            RecipeDTO dto = new RecipeDTO(recipe.getRecipeName(), recipe.getRecipeUrl(), recipe.getInstructions(),
                    recipe.getRecipeType(), ingredientDTOs, recipe.getRecipeId());
            displayRecipes.add(dto);
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
