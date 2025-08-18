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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
public class CakeController {
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    private final RecipeService recipeService;
    private final CakeOrderService cakeOrderService;
    private final CakeTaskService cakeTaskService;

    public CakeController(RecipeService recipeService, CakeOrderService cakeOrderService, CakeTaskService cakeTaskService) {
        this.recipeService = recipeService;
        this.cakeOrderService = cakeOrderService;
        this.cakeTaskService = cakeTaskService;
    }


    @GetMapping("/cakeForm")
    public String showCakeForm(@ModelAttribute("user") User user,
                               @RequestHeader(value = "Referer") String referer,
                               Model model) {
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

        String min = LocalDateTime.now().format(formatter);
        model.addAttribute("min", min);
        model.addAttribute("cakeRecipes", cakeRecipes);
        model.addAttribute("fillingRecipes", fillingRecipes);
        model.addAttribute("frostingRecipes", frostingRecipes);
        model.addAttribute("backUrl", "/");

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

    @GetMapping("/cakes/{id}")
    public String viewCakeDetails(@PathVariable int id,
                                  Model model) {
        CakeOrderDTO cakeOrderDTO = cakeOrderService.cakeOrderIdToDTO(id);
        List<CakeTaskDTO> tasks = cakeTaskService.getCakeTaskDTOsForCake(id);

        List<CakeTaskDTO> toDoTasks = cakeTaskService.getIncompleteTasks(tasks);
        toDoTasks.sort(Comparator.comparing(CakeTaskDTO::getDueDate));

        List<CakeTaskDTO> completedTasks = cakeTaskService.getCompletedTasks(tasks);
        completedTasks.sort(Comparator.comparing(CakeTaskDTO::getDueDate));

        String backUrl = "?backUrl=/cakes/" + id;

        model.addAttribute("toDoTasks", toDoTasks);
        model.addAttribute("completedTasks", completedTasks);
        model.addAttribute("cakeOrder", cakeOrderDTO);
        model.addAttribute("backUrl", backUrl);

        return "cakeDetails";
    }

    @GetMapping("/task/SHOP_PANTRY/{id}")
    public String getPantryTask(@PathVariable int id,
                                @RequestParam(value = "backUrl", required = false) String backUrl,
                                Model model) {
        CakeTaskDTO ctDTO = cakeTaskService.getCakeTaskDTObyId(id);
        model.addAttribute("mode", "task");
        model.addAttribute("task", ctDTO);
        model.addAttribute("backUrl", backUrl);
        return "pantryShoppingList";
    }

    @PostMapping("/task/SHOP_PANTRY/{id}")
    public String postPantryTask(@PathVariable int id,
                                 @RequestParam( required = false) Map<String, String> pantry,
                                 @RequestParam(value = "backUrl", required = false) String backUrl,
                                 RedirectAttributes redirectAttributes) {
        Boolean completed = cakeTaskService.toggleTaskComplete(id);
        if(completed){
            cakeTaskService.processPantryList(id, pantry);
        } else {
            cakeTaskService.resetPantryList(id);
        }
        // Pass the backUrl forward through redirect
        redirectAttributes.addAttribute("backUrl", backUrl);
        return "redirect:/task/SHOP_PANTRY/" + id;
    }

    @GetMapping("/task/SHOP_STORE/{id}")
    public String getStoreTask(@PathVariable int id,
                               @RequestParam(value = "backUrl", required = false) String backUrl,
                               Model model) {
        CakeTaskDTO ctDTO = cakeTaskService.getCakeTaskDTObyId(id);
        model.addAttribute("mode", "task");
        model.addAttribute("task", ctDTO);
        model.addAttribute("backUrl", backUrl);
        return "shoppingList";
    }

    @PostMapping("/task/SHOP_STORE/{id}")
    public String postStoreShopping(@PathVariable int id,
                                    @RequestParam(value = "backUrl", required = false) String backUrl,
                                    RedirectAttributes redirectAttributes,
                                    Model model){
        Boolean completed = cakeTaskService.toggleTaskComplete(id);
        CakeTaskDTO ctDTO = cakeTaskService.getCakeTaskDTObyId(id);
        model.addAttribute("mode", "task");
        model.addAttribute("task", ctDTO);
        redirectAttributes.addAttribute("backUrl", backUrl);
        return "redirect:/task/SHOP_STORE/" + id;
    }

    @GetMapping("/task/BAKE/{id}")
    public String getBakeTask(@PathVariable int id,
                              @RequestParam(value = "backUrl", required = false) String backUrl,
                              Model model) {
        CakeTaskDTO ctDTO = cakeTaskService.getCakeTaskDTObyId(id);
        int recipeId = ctDTO.getRecipeId();
        RecipeDTO recipeDTO = recipeService.recipeToDTO(recipeId);
        double cakeMultiplier = cakeTaskService.getMultiplier(id, ctDTO.getTaskType());

        model.addAttribute("recipe", recipeDTO);
        model.addAttribute("multiplier", cakeMultiplier);
        model.addAttribute("mode", "task");
        model.addAttribute("task", ctDTO);
        model.addAttribute("backUrl", backUrl);
        return "recipeDetails";
    }

    @PostMapping("/task/BAKE/{id}")
    public String postBakeTask(@PathVariable int id,
                               @RequestParam(value = "backUrl", required = false) String backUrl,
                               RedirectAttributes redirectAttributes,
                               Model model){
        Boolean completed = cakeTaskService.toggleTaskComplete(id);
        CakeTaskDTO ctDTO = cakeTaskService.getCakeTaskDTObyId(id);

        int recipeId = ctDTO.getRecipeId();
        RecipeDTO recipeDTO = recipeService.recipeToDTO(recipeId);
        double cakeMultiplier = cakeTaskService.getMultiplier(id, ctDTO.getTaskType());

        model.addAttribute("recipe", recipeDTO);
        model.addAttribute("multiplier", cakeMultiplier);
        model.addAttribute("mode", "task");
        model.addAttribute("task", ctDTO);

        redirectAttributes.addAttribute("backUrl", backUrl);
        return "redirect:/task/BAKE/" + id;
    }

    @GetMapping("/task/MAKE_FILLING/{id}")
    public String getMakeFillingTask(@PathVariable int id,
                                        @RequestParam(value = "backUrl", required = false) String backUrl,
                                        Model model) {
        CakeTaskDTO ctDTO = cakeTaskService.getCakeTaskDTObyId(id);
        int recipeId = ctDTO.getRecipeId();
        RecipeDTO recipeDTO = recipeService.recipeToDTO(recipeId);
        double fillingMultiplier = cakeTaskService.getMultiplier(id, ctDTO.getTaskType());
        model.addAttribute("recipe", recipeDTO);
        model.addAttribute("multiplier", fillingMultiplier);
        model.addAttribute("mode", "task");
        model.addAttribute("task", ctDTO);
        model.addAttribute("backUrl", backUrl);
        return "recipeDetails";
    }

    @PostMapping("/task/MAKE_FILLING/{id}")
    public String postMakeFillingTask(@PathVariable int id,
                                        @RequestParam(value = "backUrl", required = false) String backUrl,
                                        RedirectAttributes redirectAttributes,
                                        Model model){
        Boolean completed = cakeTaskService.toggleTaskComplete(id);
        CakeTaskDTO ctDTO = cakeTaskService.getCakeTaskDTObyId(id);
        int recipeId = ctDTO.getRecipeId();
        RecipeDTO recipeDTO = recipeService.recipeToDTO(recipeId);
        double fillingMultiplier = cakeTaskService.getMultiplier(id, ctDTO.getTaskType());
        model.addAttribute("recipe", recipeDTO);
        model.addAttribute("multiplier", fillingMultiplier);
        model.addAttribute("mode", "task");
        model.addAttribute("task", ctDTO);

        redirectAttributes.addAttribute("backUrl", backUrl);
        return "redirect:/task/MAKE_FILLING/" + id;
    }

    @GetMapping("/task/MAKE_FROSTING/{id}")
    public String getMakeFrostingTask(@PathVariable int id,
                                       @RequestParam(value = "backUrl", required = false) String backUrl,
                                      Model model){
        CakeTaskDTO ctDTO = cakeTaskService.getCakeTaskDTObyId(id);
        int recipeId = ctDTO.getRecipeId();
        RecipeDTO recipeDTO = recipeService.recipeToDTO(recipeId);
        double frostingMultiplier = cakeTaskService.getMultiplier(id, ctDTO.getTaskType());
        model.addAttribute("recipe", recipeDTO);
        model.addAttribute("multiplier", frostingMultiplier);
        model.addAttribute("mode", "task");
        model.addAttribute("task", ctDTO);
        model.addAttribute("backUrl", backUrl);
        return "recipeDetails";
    }

     @PostMapping("/task/MAKE_FROSTING/{id}")
    public String postMakeFrostingTask(@PathVariable int id,
                                        @RequestParam(value = "backUrl", required = false) String backUrl,
                                        RedirectAttributes redirectAttributes,
                                        Model model){
        Boolean completed = cakeTaskService.toggleTaskComplete(id);
        CakeTaskDTO ctDTO = cakeTaskService.getCakeTaskDTObyId(id);
        int recipeId = ctDTO.getRecipeId();
        double frostingMultiplier = cakeTaskService.getMultiplier(id,ctDTO.getTaskType());
        RecipeDTO recipeDTO = recipeService.recipeToDTO(recipeId);
        model.addAttribute("recipe", recipeDTO);
        model.addAttribute("multiplier", frostingMultiplier);
        model.addAttribute("mode", "task");
        model.addAttribute("task", ctDTO);

        redirectAttributes.addAttribute("backUrl", backUrl);
        return "redirect:/task/MAKE_FROSTING/" + id;
    }

    @GetMapping("/task/DECORATE/{id}")
    public String getDecorate(@PathVariable int id,
                              @RequestParam(value = "backUrl", required = false) String backUrl,
                              Model model) {
        CakeTaskDTO ctDTO = cakeTaskService.getCakeTaskDTObyId(id);
        model.addAttribute("mode", "task");
        model.addAttribute("task", ctDTO);
        model.addAttribute("backUrl", backUrl);
        return "decorateDetails";
    }

    @PostMapping("/task/DECORATE/{id}")
    public String postDecorate(@PathVariable int id,
                               @RequestParam(value = "backUrl", required = false) String backUrl,
                               RedirectAttributes redirectAttributes,
                               Model model){
        Boolean completed = cakeTaskService.toggleTaskComplete(id);
        CakeTaskDTO ctDTO = cakeTaskService.getCakeTaskDTObyId(id);
        model.addAttribute("mode", "task");
        model.addAttribute("task", ctDTO);
        redirectAttributes.addAttribute("backUrl", backUrl);
        return "redirect:/task/DECORATE/" + id;
    }

    @PostMapping("cake/delete/{id}")
    public String postDelete(@PathVariable int id,
                             @RequestParam(value = "backUrl", required = false) String backUrl,
                             RedirectAttributes redirectAttributes,
                             Model model){
        cakeOrderService.deleteById(id);
        return "redirect:/";
    }
}
