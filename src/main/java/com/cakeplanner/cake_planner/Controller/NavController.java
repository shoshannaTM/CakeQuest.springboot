package com.cakeplanner.cake_planner.Controller;
import com.cakeplanner.cake_planner.Model.DTO.CakeOrderDTO;
import com.cakeplanner.cake_planner.Model.DTO.CakeTaskDTO;
import com.cakeplanner.cake_planner.Model.DTO.IngredientDTO;
import com.cakeplanner.cake_planner.Model.DTO.RecipeDTO;
import com.cakeplanner.cake_planner.Model.Entities.*;
import com.cakeplanner.cake_planner.Model.Entities.Enums.RecipeType;
import com.cakeplanner.cake_planner.Model.Repositories.CakeOrderRepository;
import com.cakeplanner.cake_planner.Model.Repositories.RecipeIngredientRepository;
import com.cakeplanner.cake_planner.Model.Repositories.RecipeRepository;
import com.cakeplanner.cake_planner.Model.Repositories.UserRecipeRepository;
import com.cakeplanner.cake_planner.Model.Services.CakeOrderService;
import com.cakeplanner.cake_planner.Model.Services.RecipeService;
import com.cakeplanner.cake_planner.Model.Services.CakeTaskService;
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
import java.util.Comparator;
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
    private CakeOrderService cakeOrderService;

    @Autowired
    private CakeTaskService cakeTaskService;

    @Autowired
    private RecipeService recipeService;

    @GetMapping("/")
    public String cakes( @ModelAttribute("user") User user,
                             Model model) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy @ HH:mm");

        List<CakeOrderDTO> cakes = cakeOrderService.getCakeDTOs(user);
        model.addAttribute("cakes", cakes);

        //Do I want to only get tasks for the current day??
        List<CakeTaskDTO> tasks = cakeTaskService.getCakeTaskDTOs(user);
        tasks.sort(Comparator.comparing(CakeTaskDTO::getDueDate));
        model.addAttribute("tasks", tasks);

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
