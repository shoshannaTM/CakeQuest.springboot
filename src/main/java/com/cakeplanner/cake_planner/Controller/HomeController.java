package com.cakeplanner.cake_planner.Controller;

import com.cakeplanner.cake_planner.Model.DTO.CakeOrderDTO;
import com.cakeplanner.cake_planner.Model.Entities.*;
import com.cakeplanner.cake_planner.Model.Entities.Enums.RecipeType;
import com.cakeplanner.cake_planner.Model.Repositories.RecipeRepository;
import com.cakeplanner.cake_planner.Model.Repositories.UserRecipeRepository;
import com.cakeplanner.cake_planner.Model.Services.CakeOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {
    public DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy @ HH:mm");
    @Autowired
    UserRecipeRepository userRecipeRepository;

    @Autowired
    RecipeRepository recipeRepository;

    @Autowired
    CakeOrderService cakeOrderService;

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

        return "cakeDetails";
    }
}
