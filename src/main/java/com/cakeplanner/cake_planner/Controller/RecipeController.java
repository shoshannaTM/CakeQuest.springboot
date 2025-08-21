package com.cakeplanner.cake_planner.Controller;

import com.cakeplanner.cake_planner.Model.DTO.EditRecipeDTO;
import com.cakeplanner.cake_planner.Model.DTO.RecipeDTO;
import com.cakeplanner.cake_planner.Model.DTO.IngredientDTO;
import com.cakeplanner.cake_planner.Model.Entities.Enums.RecipeType;
import com.cakeplanner.cake_planner.Model.Entities.User;
import com.cakeplanner.cake_planner.Model.Services.RecipeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
public class RecipeController {
    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {

        this.recipeService = recipeService;
    }

    @GetMapping("/recipes/{recipeId}")
    public String showRecipeDetails(@PathVariable Long recipeId,
                                    Model model) {
        RecipeDTO recipeDTO = recipeService.userRecipeToDTO(recipeId);
        model.addAttribute("recipe", recipeDTO);
        model.addAttribute("mode", "read");
        model.addAttribute("backUrl", "/recipes");
        return "recipeDetails";
    }

    @GetMapping("/recipes/new")
    public String showNewForm(Model model) throws IOException {
        model.addAttribute("recipeTypes", RecipeType.values());
        model.addAttribute("backUrl", "/recipes");
        return "newRecipe";
    }

    @PostMapping("/recipes/new")
    public String handleNewRecipe(@RequestParam("recipeUrl") String recipeUrl,
                                  @RequestParam("recipeType") RecipeType recipeType,
                                  @ModelAttribute("user") User user) throws IOException {
        EditRecipeDTO form = recipeService.processRecipeForEdit(recipeUrl, recipeType, user);

        return "redirect:/recipes/edit/" + form.getUserRecipeId() + "?mode=scrape";
    }

    @GetMapping("/recipes/edit/{id}")
    public String editRecipe(@PathVariable Long id,
                             @RequestParam(value = "mode", required = false) String mode,
                             @ModelAttribute("form") EditRecipeDTO form,
                             Model model) {
        if (form == null || form.getUserRecipeId() == null) {
            RecipeDTO recipeDTO = recipeService.userRecipeToDTO(id);
            form = new EditRecipeDTO(
                    recipeDTO.getUserRecipeId(),
                    recipeDTO.getRecipeName(),
                    recipeDTO.getIngredients(),
                    recipeService.instructionsFromString(recipeDTO.getInstructions())
            );
        }

        model.addAttribute("form", form);
        model.addAttribute("mode", mode);
        model.addAttribute("backUrl", "/recipes");
        return "editRecipe";
    }

    @PostMapping(value = "/recipe/edit", params = "addIngredient")
    public String addIngredient(@ModelAttribute("form") EditRecipeDTO form,
                                RedirectAttributes redirectAttributes) {
        form.getIngredients().add(new IngredientDTO("", 0.0, ""));
        redirectAttributes.addFlashAttribute("form", form);
        return "redirect:/recipes/edit/" + form.getUserRecipeId();
    }


    @PostMapping(value = "/recipe/edit", params = "addStep")
    public String addStep(@ModelAttribute("form") EditRecipeDTO form, RedirectAttributes redirectAttributes) {
        form.getInstructions().add("");
        redirectAttributes.addFlashAttribute("form", form);
        return "redirect:/recipes/edit/" + form.getUserRecipeId();
    }

    @PostMapping(value = "/recipe/edit", params = "save")
    public String saveRecipe(@ModelAttribute("form") EditRecipeDTO form,
                             RedirectAttributes redirectAttributes) {
        recipeService.updateUserRecipeFromEditForm(form);
        redirectAttributes.addFlashAttribute("message", "Recipe updated and saved!");
        return "redirect:/recipes/" + form.getUserRecipeId();
    }


    @PostMapping("/recipe/delete/{id}")
    public String deleteRecipe(@PathVariable("id") Long recipeId,
                                RedirectAttributes redirectAttributes,
                                @ModelAttribute("user") User user) {
        recipeService.removeFromUserRecipes(recipeId, user);
        redirectAttributes.addFlashAttribute("message", "Recipe deleted successfully");
        return "redirect:/recipes";
    }
}
