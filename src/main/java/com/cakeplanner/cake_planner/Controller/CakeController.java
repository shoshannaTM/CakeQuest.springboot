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
import java.util.*;

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

        CakeTaskDTO ctDTO = cakeTaskService.getCakeTaskDTObyId(id);

        if (ctDTO.getTaskType() == null) {
            return "error/404";
        } else if (ctDTO.getTaskType().equals(TaskType.SHOP_PANTRY)){
            model.addAttribute("mode", "task");
            model.addAttribute("task", ctDTO);
            return "pantryShoppingList";
        } else if (ctDTO.getTaskType().equals(TaskType.SHOP_STORE)){
            model.addAttribute("mode", "task");
            model.addAttribute("task", ctDTO);
            return "shoppingList";
        } else if (ctDTO.getTaskType().equals(TaskType.DECORATE)){
            model.addAttribute("mode", "task");
            model.addAttribute("task", ctDTO);
            return "decorateDetails";
        } else {
            int recipeId = ctDTO.getRecipeId();
            RecipeDTO recipeDTO = recipeService.recipeToDto(recipeId);
            model.addAttribute("mode", "task");
            model.addAttribute("recipe", recipeDTO);
            model.addAttribute("task", ctDTO);

            return "recipeDetails";
        }
    }

    @PostMapping("/tasks/shop_pantry/{id}")
    public String postPantryShopping(@PathVariable int id,
                                     @RequestParam Map<String, String> pantry, Model model) {
        cakeTaskService.processPantryList(id, pantry);
        CakeTaskDTO ctDTO = cakeTaskService.getCakeTaskDTObyId(id);
        model.addAttribute("mode", "task");
        model.addAttribute("task", ctDTO);
        return "shoppingList";
    }

    @PostMapping("/tasks/{id}")
    public String markTaskComplete(@PathVariable int id, Model model) {
        boolean success = cakeTaskService.markTaskComplete(id);
        if(!success){
            return "error/404";
        }
        CakeTaskDTO ctDTO = cakeTaskService.getCakeTaskDTObyId(id);

        if (ctDTO.getTaskType() == null) {
            return "error/404";
        } else if (ctDTO.getTaskType().equals(TaskType.SHOP_PANTRY)){
            model.addAttribute("mode", "task");
            model.addAttribute("task", ctDTO);
            return "pantryShoppingList";
        } else if (ctDTO.getTaskType().equals(TaskType.SHOP_STORE)){
            model.addAttribute("mode", "task");
            model.addAttribute("task", ctDTO);
            return "shoppingList";
        } else if (ctDTO.getTaskType().equals(TaskType.DECORATE)){
            model.addAttribute("mode", "task");
            model.addAttribute("task", ctDTO);
            return "decorateDetails";
        } else {
            int recipeId = ctDTO.getRecipeId();
            RecipeDTO recipeDTO = recipeService.recipeToDto(recipeId);
            model.addAttribute("mode", "task");
            model.addAttribute("recipe", recipeDTO);
            model.addAttribute("task", ctDTO);

            return "recipeDetails";
        }
    }
}
