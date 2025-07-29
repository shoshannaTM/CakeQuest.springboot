package com.cakeplanner.cake_planner.Controller;

import com.cakeplanner.cake_planner.Model.DTO.CakeOrderDTO;
import com.cakeplanner.cake_planner.Model.DTO.CakeTaskDTO;
import com.cakeplanner.cake_planner.Model.DTO.IngredientDTO;
import com.cakeplanner.cake_planner.Model.DTO.RecipeDTO;
import com.cakeplanner.cake_planner.Model.Entities.*;
import com.cakeplanner.cake_planner.Model.Entities.Enums.RecipeType;
import com.cakeplanner.cake_planner.Model.Entities.Enums.TaskType;
import com.cakeplanner.cake_planner.Model.Repositories.*;
import com.cakeplanner.cake_planner.Model.Services.CakeOrderService;
import com.cakeplanner.cake_planner.Model.Services.CakeTaskService;
import com.cakeplanner.cake_planner.Model.Services.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Controller
public class CakeController {
    public DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy @ HH:mm");
    @Autowired
    UserRecipeRepository userRecipeRepository;

    @Autowired
    RecipeRepository recipeRepository;

    @Autowired
    RecipeIngredientRepository recipeIngredientRepository;

    @Autowired
    RecipeService recipeService;

    @Autowired
    CakeOrderRepository cakeOrderRepository;

    @Autowired
    CakeTaskRepository cakeTaskRepository;

    @Autowired
    CakeOrderService cakeOrderService;

    @Autowired
    CakeTaskService cakeTaskService;

    @GetMapping("/cakeForm")
    public String showCakeForm(@ModelAttribute("user") User user, Model model) {
        List<UserRecipe> userCakeRecipes = userRecipeRepository.findByUserAndRecipe_RecipeType(user, RecipeType.CAKE);
        List<UserRecipe> userFillingRecipes = userRecipeRepository.findByUserAndRecipe_RecipeType(user, RecipeType.FILLING);
        List<UserRecipe> userFrostingRecipes = userRecipeRepository.findByUserAndRecipe_RecipeType(user, RecipeType.FROSTING);

        List<Recipe> cakeRecipes = new ArrayList<>();
        for (UserRecipe ur : userCakeRecipes) {
            cakeRecipes.add(ur.getRecipe());
        }

        List<Recipe> fillingRecipes = new ArrayList<>();
        for (UserRecipe ur : userFillingRecipes) {
            fillingRecipes.add(ur.getRecipe());
        }

        List<Recipe> frostingRecipes = new ArrayList<>();
        for (UserRecipe ur : userFrostingRecipes) {
            frostingRecipes.add(ur.getRecipe());
        }

        model.addAttribute("cakeRecipes", cakeRecipes);
        model.addAttribute("fillingRecipes", fillingRecipes);
        model.addAttribute("frostingRecipes", frostingRecipes);

        return "cakeForm";
    }


    @PostMapping("/cakeForm")
    public String createCake(@RequestParam("cakeName") String cakeName,
                             @RequestParam("dueDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dueDate,
                             @RequestParam("cakeRecipeId") int cakeRecipeId,
                             @RequestParam("cakeMultiplier") double cakeMultiplier,
                             @RequestParam("fillingRecipeId") int fillingRecipeId,
                             @RequestParam("fillingMultiplier") double fillingMultiplier,
                             @RequestParam("frostingRecipeId") int frostingRecipeId,
                             @RequestParam("frostingMultiplier") double frostingMultiplier,
                             @RequestParam(value = "dietaryRestriction", required = false) String dietaryRestriction,
                             @RequestParam(value = "decorationNotes", required = false) String decorationNotes,
                             @ModelAttribute("user") User user,
                             Model model) {
        Recipe cakeRecipe = recipeRepository.findRecipeByRecipeId(cakeRecipeId);
        Recipe fillingRecipe = recipeRepository.findRecipeByRecipeId(fillingRecipeId);
        Recipe frostingRecipe = recipeRepository.findRecipeByRecipeId(frostingRecipeId);
        CakeOrder cakeOrder = new CakeOrder(user, cakeName, dueDate, cakeRecipe, cakeMultiplier, fillingRecipe, fillingMultiplier,
                frostingRecipe, frostingMultiplier, dietaryRestriction, decorationNotes);

        CakeOrderDTO cakeDTO = cakeOrderService.save(cakeOrder);

        return "redirect:/";
    }

    //FIXME
    @GetMapping("/cakes/{id}")
    public String viewCakeDetails(@PathVariable int id, Model model) {
        Optional<CakeOrder> cakeOrderOptional = cakeOrderRepository.findById(id);

        if (cakeOrderOptional.isEmpty()) {
            return "error/404";
        }

        CakeOrder cakeOrder = cakeOrderOptional.get();
        List<CakeTaskDTO> tasks = cakeTaskService.getCakeTaskDTOsForCake(cakeOrder);
        tasks.sort(Comparator.comparing(CakeTaskDTO::getDueDate));

        model.addAttribute("cakeOrder", cakeOrder);
        model.addAttribute("tasks", tasks);

        return "cakeDetails";
    }


    @GetMapping("/tasks/{id}")
    public String viewTaskDetails(@PathVariable int id, Model model) {
        Optional<CakeTask> taskOptional = cakeTaskRepository.findById(id);
        if (taskOptional.isEmpty()) {
            return "error/404";
        }

        CakeTask cakeTask = taskOptional.get();

        if (cakeTask.getTaskType() == TaskType.SHOP) {
            model.addAttribute("mode", "task");
            model.addAttribute("task", cakeTask);
            return "shoppingList";
        } else if (cakeTask.getTaskType() == TaskType.DECORATE) {
            model.addAttribute("task", cakeTask);
            model.addAttribute("mode", "task");
            return "decorateDetails";
        } else {
            //fIXME should have a method in RecipeService
            Recipe recipe = cakeTask.getRecipe();
            int recipeId = recipe.getRecipeId();
            List<RecipeIngredient> recipeIngredients = recipeIngredientRepository.findRecipeIngredientsByRecipeId(recipeId);
            List<IngredientDTO> ingredientDTOList = recipeService.recipeIngredientsToDTO(recipeIngredients);

            RecipeDTO recipeDTO = new RecipeDTO(recipe.getRecipeName(), recipe.getRecipeUrl(), recipe.getInstructions(),
                    recipe.getRecipeType(), ingredientDTOList, recipe.getRecipeId());
            model.addAttribute("mode", "task");
            model.addAttribute("recipe", recipeDTO);
            return "recipeDetails";
        }
    }

    @PostMapping("/tasks/{id}")
    public String markTaskComplete(@PathVariable int id, Model model) {
        boolean success = cakeTaskService.markTaskComplete(id);
        TaskType type = cakeTaskService.getTaskType(id);

        Optional<CakeTask> taskOptional = cakeTaskRepository.findById(id);
        if (taskOptional.isEmpty()) {
            return "error/404";
        }

        CakeTask cakeTask = taskOptional.get();

        if (success) {
            model.addAttribute("completed", "true");
            if (cakeTask.getTaskType() == TaskType.SHOP) {
                model.addAttribute("mode", "task");
                model.addAttribute("task", cakeTask);
                return "shoppingList";
            } else if (cakeTask.getTaskType() == TaskType.DECORATE) {
                model.addAttribute("task", cakeTask);
                model.addAttribute("mode", "task");
                return "decorateDetails";
            } else {
                //fIXME should have a method in RecipeService
                Recipe recipe = cakeTask.getRecipe();
                int recipeId = recipe.getRecipeId();
                List<RecipeIngredient> recipeIngredients = recipeIngredientRepository.findRecipeIngredientsByRecipeId(recipeId);
                List<IngredientDTO> ingredientDTOList = recipeService.recipeIngredientsToDTO(recipeIngredients);

                RecipeDTO recipeDTO = new RecipeDTO(recipe.getRecipeName(), recipe.getRecipeUrl(), recipe.getInstructions(),
                        recipe.getRecipeType(), ingredientDTOList, recipe.getRecipeId());
                model.addAttribute("mode", "task");
                model.addAttribute("recipe", recipeDTO);
                return "recipeDetails";
            }
        } else {
            return "error/404";
        }
    }

}
