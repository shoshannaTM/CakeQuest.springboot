package com.cakeplanner.cake_planner.Controller;

import com.cakeplanner.cake_planner.Model.Entities.DummyCakes;
import com.cakeplanner.cake_planner.Model.Entities.User;
import com.cakeplanner.cake_planner.Model.Entities.UserRecipe;
import com.cakeplanner.cake_planner.Model.Entities.Enums.RecipeType;
import com.cakeplanner.cake_planner.Model.Entities.Recipe;
import com.cakeplanner.cake_planner.Model.Repositories.UserRecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {
    public DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy @ HH:mm");
    @Autowired
    UserRecipeRepository userRecipeRepository;

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
    public String createCake(Model model) {
        return "home";
    }

    //FIXME
    @GetMapping("cakes/{id}")
    public String viewCakeDetails(@PathVariable int id, Model model) {
        // Recreate the dummy cake with the matching ID
        DummyCakes cake = null;

        if (id == 1) {
            cake = new DummyCakes("Sophie's Unicorn Cake", "Vanilla", "Strawberry Jam", "Buttercream", LocalDateTime.now().plusDays(2).format(formatter), 25, 1);
        } else if (id == 2) {
            cake = new DummyCakes("Dad's Retirement Cake", "Chocolate", "Salted Caramel", "Ganache", LocalDateTime.now().plusDays(5).format(formatter), 60, 2);
        } else if (id == 3) {
            cake = new DummyCakes("Wedding Cake", "Red Velvet", "Cream Cheese", "Fondant", LocalDateTime.now().plusWeeks(1).format(formatter), 90, 3);
        }


        model.addAttribute("cake", cake);
        return "cakeDetails";
    }



}
