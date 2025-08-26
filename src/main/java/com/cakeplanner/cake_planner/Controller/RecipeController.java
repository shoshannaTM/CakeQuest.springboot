package com.cakeplanner.cake_planner.Controller;

import com.cakeplanner.cake_planner.Model.DTO.EditRecipeDTO;
import com.cakeplanner.cake_planner.Model.DTO.RecipeDTO;
import com.cakeplanner.cake_planner.Model.DTO.IngredientDTO;
import com.cakeplanner.cake_planner.Model.Entities.Enums.RecipeType;
import com.cakeplanner.cake_planner.Model.Entities.User;
import com.cakeplanner.cake_planner.Model.Services.RecipeService;
import com.cakeplanner.cake_planner.Model.Services.RecipeTransactionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
public class RecipeController {
    private final RecipeService recipeService;
    private final RecipeTransactionService recipeTransactionService;

    public RecipeController(RecipeService recipeService,
                            RecipeTransactionService recipeTransactionService) {
        this.recipeService = recipeService;
        this.recipeTransactionService = recipeTransactionService;
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
    public String showNewForm(Model model){
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

        boolean manual = (form.getRecipeName() == null || form.getRecipeName().isBlank())
                && (form.getIngredients() == null || form.getIngredients().isEmpty())
                && (form.getInstructions() == null || form.getInstructions().isEmpty());

        redirectAttributes.addFlashAttribute("form", form);

        if (manual) {
            redirectAttributes.addFlashAttribute("message",
                    "We couldn’t load that URL. Please enter the recipe manually.");
            return "redirect:/recipes/edit/" + form.getUserRecipeId() + "?mode=manual";
        }

        redirectAttributes.addFlashAttribute("message",
                "Recipe scraping isn’t always perfect. Review and fix anything before saving.");
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
            form = recipeService.manualInputForm(user);
        }

        if (form.getIngredients() == null) form.setIngredients(new java.util.ArrayList<>());
        if (form.getInstructions() == null) form.setInstructions(new java.util.ArrayList<>());

        model.addAttribute("form", form);
        model.addAttribute("mode", mode);
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

        String message = (String) model.asMap().get("message");
        if (message == null) {
            message = "manual".equalsIgnoreCase(mode)
                    ? "We couldn’t load that URL. Please enter the recipe manually."
                    : "Recipe scraping isn’t always perfect. Review and update anything before saving.";
        }

        model.addAttribute("form", form);
        model.addAttribute("recipeTypes", RecipeType.values());
        model.addAttribute("mode", mode);
        model.addAttribute("message", message);
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
                             @ModelAttribute("user") User user,
                             RedirectAttributes redirectAttributes) {

        if (form.getIngredients() == null || form.getIngredients().isEmpty() ||
                form.getInstructions() == null || form.getInstructions().isEmpty()) {
            redirectAttributes.addFlashAttribute("form", form);
            redirectAttributes.addFlashAttribute("message",
                    "Please add ingredients and instructions before saving.");
            return "redirect:/recipes/edit/" + form.getUserRecipeId() + "?mode=manual";
        }

        recipeService.updateUserRecipeFromEditForm(form);
        redirectAttributes.addFlashAttribute("message", "Recipe updated and saved!");
        return "redirect:/recipes/" + form.getUserRecipeId();
    }


    @PostMapping(value = "/recipe/edit", params = "discard")
    public String discardRecipe(@ModelAttribute("form") EditRecipeDTO form,
                                @ModelAttribute("user") User user,
                                RedirectAttributes redirectAttributes) {
        recipeTransactionService.removeFromUserRecipes(user, form.getUserRecipeId());
        redirectAttributes.addFlashAttribute("message", "Recipe deleted successfully");
        return "redirect:/recipes";
    }

    @PostMapping("/recipe/delete/{id}")
    public String deleteRecipe(@PathVariable("id") Long recipeId,
                               RedirectAttributes redirectAttributes,
                               @ModelAttribute("user") User user) {
        RecipeTransactionService.DeleteResult res =
                recipeTransactionService.deleteIfNoActiveTasks(recipeId);

        if (res.deleted()) {
            redirectAttributes.addFlashAttribute("message", "Recipe deleted successfully");
            return "redirect:/recipes";
        } else {
            String alert = "This recipe is used by " + res.openTasks()
                    + " active task(s) and can’t be deleted until they’re complete.";
            redirectAttributes.addFlashAttribute("alert", alert);
            return "redirect:/recipes/" + recipeId;
        }
    }
}
