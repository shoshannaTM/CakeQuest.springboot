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
    public String showRecipeDetails(@PathVariable Integer recipeId,
                                    Model model) {
        RecipeDTO recipeDTO = recipeService.recipeToDTO(recipeId);
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

        return "redirect:/recipes/edit/" + form.getRecipeId() + "?mode=scrape";
    }

    @GetMapping("/recipes/edit/{id}")
    public String editRecipe(@PathVariable Integer id,
                             @RequestParam(value = "mode", required = false) String mode,
                             Model model) {

        RecipeDTO recipe = recipeService.recipeToDTO(id);
        EditRecipeDTO form = new EditRecipeDTO(
                recipe.getRecipeId(),
                recipe.getRecipeName(),
                recipe.getIngredients(),
                recipeService.instructionsFromString(recipe.getInstructions())
        );
        model.addAttribute("form", form);
        model.addAttribute("mode", mode);
        model.addAttribute("backUrl", "/recipes");
        return "editRecipe";
    }

    @PostMapping(value = "/recipe/edit", params = "addIngredient")
    public String addIngredient(@ModelAttribute("form") EditRecipeDTO form, RedirectAttributes ra) {
        form.getIngredients().add(new IngredientDTO("", 0.0, ""));
        ra.addFlashAttribute("form", form);
        return "redirect:/recipes/edit/" + form.getRecipeId();
    }

    @PostMapping(value = "/recipe/edit", params = "removeIngredient")
    public String removeIngredient(@ModelAttribute("form") EditRecipeDTO form,
                                   @RequestParam("removeIngredient") int index,
                                   RedirectAttributes ra) {
        if (index >= 0 && index < form.getIngredients().size()) {
            form.getIngredients().remove(index);
        }
        ra.addFlashAttribute("form", form);
        return "redirect:/recipes/edit/" + form.getRecipeId();
    }

    @PostMapping(value = "/recipe/edit", params = "addStep")
    public String addStep(@ModelAttribute("form") EditRecipeDTO form, RedirectAttributes ra) {
        form.getInstructions().add("");
        ra.addFlashAttribute("form", form);
        return "redirect:/recipes/edit/" + form.getRecipeId();
    }

    @PostMapping(value = "/recipe/edit", params = "removeStep")
    public String removeStep(@ModelAttribute("form") EditRecipeDTO form,
                             @RequestParam("removeStep") int index,
                             RedirectAttributes ra) {
        if (index >= 0 && index < form.getInstructions().size()) {
            form.getInstructions().remove(index);
        }
        ra.addFlashAttribute("form", form);
        return "redirect:/recipes/edit/" + form.getRecipeId();
    }

    @PostMapping(value = "/recipe/edit", params = "save")
    public String saveRecipe(@ModelAttribute("form") EditRecipeDTO form,
                             RedirectAttributes ra) {
        // Persist: name + instructions + ingredients
        recipeService.updateRecipeFromEditForm(form);
        ra.addFlashAttribute("message", "Recipe updated!");
        return "redirect:/recipes/" + form.getRecipeId();
    }

}
