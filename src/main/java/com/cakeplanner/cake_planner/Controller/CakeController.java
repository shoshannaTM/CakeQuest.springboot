package com.cakeplanner.cake_planner.Controller;

import com.cakeplanner.cake_planner.Model.DTO.CakeOrderDTO;
import com.cakeplanner.cake_planner.Model.DTO.CakeTaskDTO;
import com.cakeplanner.cake_planner.Model.DTO.RecipeDTO;
import com.cakeplanner.cake_planner.Model.Entities.*;
import com.cakeplanner.cake_planner.Model.Entities.Enums.RecipeType;
import com.cakeplanner.cake_planner.Model.Services.CakeOrderService;
import com.cakeplanner.cake_planner.Model.Services.CakeTaskService;
import com.cakeplanner.cake_planner.Model.Services.RecipeService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
public class CakeController {
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy @ HH:mm");
    private final RecipeService recipeService;
    private final CakeOrderService cakeOrderService;
    private final CakeTaskService cakeTaskService;

    public CakeController(RecipeService recipeService, CakeOrderService cakeOrderService, CakeTaskService cakeTaskService) {
        this.recipeService = recipeService;
        this.cakeOrderService = cakeOrderService;
        this.cakeTaskService = cakeTaskService;
    }


    @GetMapping("/cakeForm")
    public String showCakeForm(@ModelAttribute("user") User user, Model model) {
        List<UserRecipe> userCakeRecipes = recipeService.usersRecipesByType(user, RecipeType.CAKE);
        List<UserRecipe> userFillingRecipes = recipeService.usersRecipesByType(user, RecipeType.FILLING);
        List<UserRecipe> userFrostingRecipes = recipeService.usersRecipesByType(user, RecipeType.FROSTING);

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
       cakeOrderService.createCakeOrderFromForm(cakeName, dueDate, cakeRecipeId, cakeMultiplier,
                                                fillingRecipeId, fillingMultiplier, frostingRecipeId,
                                                frostingMultiplier, dietaryRestriction, decorationNotes, user);
        return "redirect:/";
    }

    //FIXME
    @GetMapping("/cakes/{id}")
    public String viewCakeDetails(@PathVariable int id, Model model) {
        CakeOrderDTO cakeOrderDTO = cakeOrderService.cakeOrderIdToDTO(id);
        List<CakeTaskDTO> tasks = cakeTaskService.getCakeTaskDTOsForCake(id);
        tasks.sort(Comparator.comparing(CakeTaskDTO::getDueDate));

        model.addAttribute("cakeOrder", cakeOrderDTO);
        model.addAttribute("tasks", tasks);

        return "cakeDetails";
    }

    @GetMapping("/task/SHOP_PANTRY/{id}")
    public String getPantryTask(@PathVariable int id, Model model) {
        CakeTaskDTO ctDTO = cakeTaskService.getCakeTaskDTObyId(id);
        model.addAttribute("mode", "task");
        model.addAttribute("task", ctDTO);
        return "pantryShoppingList";
    }

    @PostMapping("/task/SHOP_PANTRY/{id}")
    public String postPantryTask(@PathVariable int id,
                                 @RequestParam( required = false) Map<String, String> pantry,
                                 Model model){
        Boolean completed = cakeTaskService.toggleTaskComplete(id);
        if(completed){
            cakeTaskService.processPantryList(id, pantry);
        } else {
            cakeTaskService.resetPantryList(id);
        }
        CakeTaskDTO ctDTO = cakeTaskService.getCakeTaskDTObyId(id);
        model.addAttribute("mode", "task");
        model.addAttribute("task", ctDTO);
        return "pantryShoppingList";
    }

    @GetMapping("/task/SHOP_STORE/{id}")
    public String getStoreTask(@PathVariable int id, Model model) {
        CakeTaskDTO ctDTO = cakeTaskService.getCakeTaskDTObyId(id);
        model.addAttribute("mode", "task");
        model.addAttribute("task", ctDTO);
        return "shoppingList";
    }

    @PostMapping("/task/SHOP_STORE/{id}")
    public String postStoreShopping(@PathVariable int id, Model model){
        Boolean completed = cakeTaskService.toggleTaskComplete(id);
        CakeTaskDTO ctDTO = cakeTaskService.getCakeTaskDTObyId(id);
        model.addAttribute("mode", "task");
        model.addAttribute("task", ctDTO);
        return "shoppingList";
    }

    @GetMapping("/task/BAKE/{id}")
    public String getBakeTask(@PathVariable int id, Model model) {
        CakeTaskDTO ctDTO = cakeTaskService.getCakeTaskDTObyId(id);
        int recipeId = ctDTO.getRecipeId();
        RecipeDTO recipeDTO = recipeService.recipeToDTO(recipeId);
        model.addAttribute("recipe", recipeDTO);
        model.addAttribute("mode", "task");
        model.addAttribute("task", ctDTO);
        return "recipeDetails";
    }

    @PostMapping("/task/BAKE/{id}")
    public String postBakeTask(@PathVariable int id, Model model){
        Boolean completed = cakeTaskService.toggleTaskComplete(id);
        CakeTaskDTO ctDTO = cakeTaskService.getCakeTaskDTObyId(id);
        int recipeId = ctDTO.getRecipeId();
        RecipeDTO recipeDTO = recipeService.recipeToDTO(recipeId);
        model.addAttribute("recipe", recipeDTO);
        model.addAttribute("mode", "task");
        model.addAttribute("task", ctDTO);
        return "recipeDetails";
    }

    @GetMapping("/task/MAKE_FILLING/{id}")
    public String getMakeFillingTask(@PathVariable int id, Model model) {
        CakeTaskDTO ctDTO = cakeTaskService.getCakeTaskDTObyId(id);
        int recipeId = ctDTO.getRecipeId();
        RecipeDTO recipeDTO = recipeService.recipeToDTO(recipeId);
        model.addAttribute("recipe", recipeDTO);
        model.addAttribute("mode", "task");
        model.addAttribute("task", ctDTO);
        return "recipeDetails";
    }

    @PostMapping("/task/MAKE_FILLING/{id}")
    public String postMakeFillingTask(@PathVariable int id, Model model){
        Boolean completed = cakeTaskService.toggleTaskComplete(id);
        CakeTaskDTO ctDTO = cakeTaskService.getCakeTaskDTObyId(id);
        int recipeId = ctDTO.getRecipeId();
        RecipeDTO recipeDTO = recipeService.recipeToDTO(recipeId);
        model.addAttribute("recipe", recipeDTO);
        model.addAttribute("mode", "task");
        model.addAttribute("task", ctDTO);
        return "recipeDetails";
    }

    @GetMapping("/task/MAKE_FROSTING/{id}")
    public String getMakeFrostingTask(@PathVariable int id, Model model){
        CakeTaskDTO ctDTO = cakeTaskService.getCakeTaskDTObyId(id);
        int recipeId = ctDTO.getRecipeId();
        RecipeDTO recipeDTO = recipeService.recipeToDTO(recipeId);
        double frostingMultiplier = cakeTaskService.getFrostingMultiplier(id, ctDTO.getTaskType());
        model.addAttribute("recipe", recipeDTO);
        model.addAttribute("multiplier", frostingMultiplier);
        model.addAttribute("mode", "task");
        model.addAttribute("task", ctDTO);
        return "recipeDetails";
    }

    @PostMapping("/task/MAKE_FROSTING/{id}")
    public String postMakeFrostingTask(@PathVariable int id, Model model){
        Boolean completed = cakeTaskService.toggleTaskComplete(id);
        CakeTaskDTO ctDTO = cakeTaskService.getCakeTaskDTObyId(id);
        int recipeId = ctDTO.getRecipeId();
        double frostingMultiplier = cakeTaskService.getFrostingMultiplier(id,ctDTO.getTaskType());
        RecipeDTO recipeDTO = recipeService.recipeToDTO(recipeId);
        model.addAttribute("recipe", recipeDTO);
        model.addAttribute("multiplier", frostingMultiplier);
        model.addAttribute("mode", "task");
        model.addAttribute("task", ctDTO);
        return "recipeDetails";
    }

    @GetMapping("/task/DECORATE/{id}")
    public String getDecorate(@PathVariable int id, Model model) {
        CakeTaskDTO ctDTO = cakeTaskService.getCakeTaskDTObyId(id);
        model.addAttribute("mode", "task");
        model.addAttribute("task", ctDTO);
        return "decorateDetails";
    }

    @PostMapping("/task/DECORATE/{id}")
    public String postDecorate(@PathVariable int id, Model model){
        Boolean completed = cakeTaskService.toggleTaskComplete(id);
        CakeTaskDTO ctDTO = cakeTaskService.getCakeTaskDTObyId(id);
        model.addAttribute("mode", "task");
        model.addAttribute("task", ctDTO);
        return "decorateDetails";
    }
}
