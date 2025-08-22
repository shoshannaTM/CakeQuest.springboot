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
                                    RedirectAttributes redirectAttributes,
                                    Model model) {
        RecipeDTO recipeDTO = recipeService.userRecipeToDTO(recipeId);

        if (recipeDTO == null) {
            redirectAttributes.addFlashAttribute("message", "Recipe not found.");
            return "redirect:/recipes";
        }

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
                                  @ModelAttribute("user") User user,
                                  RedirectAttributes redirectAttributes) throws IOException {
        EditRecipeDTO form = recipeService.processRecipeForEdit(recipeUrl, recipeType, user);

        if (form.getUserRecipeId() == null) {
            if (form.getIngredients() == null) form.setIngredients(new java.util.ArrayList<>());
            if (form.getInstructions() == null) form.setInstructions(new java.util.ArrayList<>());
            redirectAttributes.addFlashAttribute("form", form);
            redirectAttributes.addFlashAttribute("message",
                    "Recipe scraping isn’t always perfect. Review and fix anything before saving.");
            return "redirect:/recipes/manual";
        }

        return "redirect:/recipes/edit/" + form.getUserRecipeId() + "?mode=scrape";
    }

    @GetMapping("/recipes/manual")
    public String editRecipeManual(@RequestParam(value = "mode", required = false) String mode,
                                   @RequestParam(value = "message", required = false) String message,
                                   @ModelAttribute("user") User user,
                                   @ModelAttribute("form") EditRecipeDTO flashedForm,
                                   Model model) {
        EditRecipeDTO form = flashedForm;

        if (form == null || form.getUserRecipeId() == null) {
            // Only create a new draft if no flashed form came in
            form = recipeService.emptyUserRecipeForManual(user);
        }

        if (form.getIngredients() == null) form.setIngredients(new java.util.ArrayList<>());
        if (form.getInstructions() == null) form.setInstructions(new java.util.ArrayList<>());

        model.addAttribute("form", form);
        model.addAttribute("recipeTypes", RecipeType.values());
        model.addAttribute("message", "We couldn’t load that URL. Please enter the recipe manually.");
        model.addAttribute("backUrl", "/recipes");
        return "editRecipe";
    }


    @GetMapping("/recipes/edit/{id}")
    public String editRecipe(@PathVariable Long id,
                             @RequestParam(value = "mode", required = false) String mode,
                             @ModelAttribute("form") EditRecipeDTO form,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        if (form == null || form.getUserRecipeId() == null) {
            RecipeDTO recipeDTO = recipeService.userRecipeToDTO(id);
                if (recipeDTO == null) {
                    redirectAttributes.addFlashAttribute("message", "Recipe not found.");
                    return "redirect:/recipes";
        }
            form = new EditRecipeDTO(
                    recipeDTO.getUserRecipeId(),
                    recipeDTO.getRecipeName(),
                    recipeDTO.getRecipeType(),
                    recipeDTO.getIngredients(),
                    recipeService.instructionsFromString(recipeDTO.getInstructions())
            );
        }
    if (form.getIngredients() == null) form.setIngredients(new java.util.ArrayList<>());
    if (form.getInstructions() == null) form.setInstructions(new java.util.ArrayList<>());

        model.addAttribute("form", form);
        model.addAttribute("recipeTypes", RecipeType.values());
        model.addAttribute("message",
                "Recipe scraping isn’t always perfect. Review and update anything before saving.");
        model.addAttribute("backUrl", "/recipes");
        return "editRecipe";
    }

    @PostMapping(value = "/recipe/edit", params = "addIngredient")
    public String addIngredient(@ModelAttribute("form") EditRecipeDTO form,
                                RedirectAttributes redirectAttributes) {
    if (form.getIngredients() == null) form.setIngredients(new java.util.ArrayList<>());
        form.getIngredients().add(new IngredientDTO("", 0.0, ""));
        redirectAttributes.addFlashAttribute("form", form);
        return "redirect:/recipes/edit/" + form.getUserRecipeId();
    }


    @PostMapping(value = "/recipe/edit", params = "addStep")
    public String addStep(@ModelAttribute("form") EditRecipeDTO form, RedirectAttributes redirectAttributes) {
    if (form.getInstructions() == null) form.setInstructions(new java.util.ArrayList<>());
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
