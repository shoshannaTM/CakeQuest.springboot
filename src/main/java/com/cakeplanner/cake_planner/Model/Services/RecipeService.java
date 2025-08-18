package com.cakeplanner.cake_planner.Model.Services;

import com.cakeplanner.cake_planner.Model.DTO.EditRecipeDTO;
import com.cakeplanner.cake_planner.Model.DTO.IngredientDTO;
import com.cakeplanner.cake_planner.Model.DTO.RecipeDTO;
import com.cakeplanner.cake_planner.Model.Entities.*;
import com.cakeplanner.cake_planner.Model.Entities.Enums.TaskType;
import com.cakeplanner.cake_planner.Model.Entities.Enums.RecipeType;
import com.cakeplanner.cake_planner.Model.Repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//check if recipe already exists by url, adds info to recipe table, ingredients table, and recipeIngredient table
//Return a recipeDTO object (with ingredientDTO included) to controller to display recipe in UI

@Service
public class RecipeService {
    @Autowired
    RecipeScraperService recipeScraperService;
    @Autowired
    CakeOrderService cakeOrderService;
    @Autowired
    RecipeRepository recipeRepository;
    @Autowired
    UserRecipeRepository userRecipeRepository;
    @Autowired
    CakeTaskRepository cakeTaskRepository;
    @Autowired
    RecipeIngredientRepository recipeIngredientRepository;
    @Autowired
    IngredientRepository ingredientRepository;
    @Autowired
    CakeOrderRepository cakeOrderRepository;

    @Transactional
    public EditRecipeDTO processRecipeForEdit(String recipeUrl, RecipeType recipeType, User user) throws IOException {
        Optional<Recipe> optionalRecipe = recipeRepository.findRecipeByRecipeUrl(recipeUrl);

        Recipe recipe;
        if (optionalRecipe.isPresent()) {
            recipe = optionalRecipe.get();
            // If user hasn't saved this recipe yet, create UserRecipe link
            if (!userRecipeRepository.existsByUserAndRecipe(user, recipe)) {
                userRecipeRepository.save(new UserRecipe(new UserRecipeId(recipe.getRecipeId(), user.getUserId()), recipe, user));
            }
        } else {
            RecipeDTO recipeDTO = recipeScraperService.scrapeRecipe(recipeUrl, recipeType);
            recipe = new Recipe(recipeDTO.getRecipeType(), recipeDTO.getRecipeName(), recipeDTO.getInstructions(), recipeDTO.getRecipeUrl());
            recipeRepository.save(recipe);
            saveIngredients(recipe, recipeDTO);
            userRecipeRepository.save(new UserRecipe(new UserRecipeId(recipe.getRecipeId(), user.getUserId()), recipe, user));
        }

        List<RecipeIngredient> recipeIngredients = recipeIngredientRepository.findRecipeIngredientsByRecipeId(recipe.getRecipeId());
        List<IngredientDTO> ingredientDTOList = recipeIngredientsToDTO(recipeIngredients);
        List<String> instructionsList = instructionsFromString(recipe.getInstructions());

        return new EditRecipeDTO(recipe.getRecipeId(), recipe.getRecipeName(), ingredientDTOList, instructionsList);
    }

    public List<String> instructionsFromString(String instructionsString) {
        List<String> instructions = new ArrayList<>();
        for (String s : instructionsString.split("\\|")) {
            if (!s.isBlank()) {
                instructions.add(s.trim());
            }
        }
        return instructions;
    }

    public RecipeDTO recipeToDTO(int recipeId){
        Recipe r = recipeRepository.findRecipeByRecipeId(recipeId);
        List<RecipeIngredient> recipeIngredients = recipeIngredientRepository.findRecipeIngredientsByRecipeId(recipeId);
        List<IngredientDTO> ingredientDTOList = recipeIngredientsToDTO(recipeIngredients);

        RecipeDTO recipeDTO = new RecipeDTO(r.getRecipeName(), r.getRecipeUrl(), r.getInstructions(),
                                            r.getRecipeType(), ingredientDTOList, recipeId);
        return recipeDTO;
    }

    public List<IngredientDTO> recipeIngredientsToDTO(List<RecipeIngredient> recipeIngredients) {
        List<IngredientDTO> dtoList = new ArrayList<>();
        for (RecipeIngredient recipeIngredient : recipeIngredients) {
            IngredientDTO dto = new IngredientDTO(
                    recipeIngredient.getIngredient().getIngredientName(),
                    recipeIngredient.getQuantity(),
                    recipeIngredient.getUnit()
            );
            dtoList.add(dto);
        }
        return dtoList;
    }

    public boolean saveRecipe (RecipeDTO recipeDTO, User user){
            Recipe recipe = new Recipe(recipeDTO.getRecipeType(), recipeDTO.getRecipeName(),
                    recipeDTO.getInstructions(), recipeDTO.getRecipeUrl());
            recipeRepository.save(recipe);
            return (recipe.getRecipeId() > 0);
        }

    public List<UserRecipe> usersRecipesByType(User user, RecipeType recipeType){
        List<UserRecipe> userRecipesOfType = userRecipeRepository.findByUserAndRecipe_RecipeType(user, recipeType);
        return userRecipesOfType;
    }

    public void saveIngredients(Recipe recipe, RecipeDTO recipeDTO) {
        for (IngredientDTO dto : recipeDTO.getIngredients()) {
            Ingredient ingredient = ingredientRepository
                    .findByIngredientNameIgnoreCase(dto.getName())
                    .orElseGet(() -> ingredientRepository.save(new Ingredient(dto.getName())));
            RecipeIngredient recipeIngredient = new RecipeIngredient(recipe, ingredient, dto.getAmount(), dto.getUnit());
            recipeIngredientRepository.save(recipeIngredient);
        }
    }

    public List<RecipeDTO> filterRecipes(User user, String type){
        List<RecipeDTO> displayRecipes = new ArrayList<>();
        if(type.equalsIgnoreCase("all")){
            List<UserRecipe> recipes = userRecipeRepository.findByUser(user);
            for (UserRecipe ur : recipes) {
                int recipeId = ur.getRecipe().getRecipeId();
                RecipeDTO dto = recipeToDTO(recipeId);
                displayRecipes.add(dto);
            }
        } else {
            RecipeType recipeType = RecipeType.valueOf(type.toUpperCase());
            List<UserRecipe> recipes = userRecipeRepository.findByUserAndRecipe_RecipeType(user, recipeType);
            for (UserRecipe ur : recipes) {
                int recipeId = ur.getRecipe().getRecipeId();
                RecipeDTO dto = recipeToDTO(recipeId);
                displayRecipes.add(dto);
            }
        }
        return displayRecipes;
    }

    public List<RecipeDTO> searchRecipes(User user, String search){
        List<RecipeDTO> displayRecipes = new ArrayList<>();
        List<UserRecipe> recipes = userRecipeRepository.findByUserAndRecipe_RecipeNameContainingIgnoreCase(user, search);
        for (UserRecipe ur : recipes) {
            int recipeId = ur.getRecipe().getRecipeId();
            RecipeDTO dto = recipeToDTO(recipeId);
            displayRecipes.add(dto);
        }
        return displayRecipes;
    }

    @Transactional
    public void updateRecipeFromEditForm(EditRecipeDTO form) {
        Recipe recipe = recipeRepository.findRecipeByRecipeId(form.getRecipeId());
        recipe.setRecipeName(form.getRecipeName());
        recipe.setInstructions(form.instructionsToString());

        // Replace ingredients
        List<RecipeIngredient> existing =
                recipeIngredientRepository.findRecipeIngredientsByRecipeId(form.getRecipeId());
        recipeIngredientRepository.deleteAllInBatch(existing);

        for (IngredientDTO dto : form.getIngredients()) {
            if (dto.getAmount() == 0 || dto.getName() == null || dto.getName().isBlank()) continue;

            Ingredient ing = ingredientRepository
                    .findByIngredientNameIgnoreCase(dto.getName())
                    .orElseGet(() -> ingredientRepository.save(new Ingredient(dto.getName())));

            recipeIngredientRepository.save(new RecipeIngredient(recipe, ing, dto.getAmount(), dto.getUnit()));
        }
        recipeRepository.saveAndFlush(recipe); // ensure new ingredients are visible in this TX

        // Find all orders that use this recipe
        List<CakeOrder> affected = cakeOrderRepository.findAllUsingRecipe(recipe);

        for (CakeOrder order : affected) {
            // Rebuild the shopping list from current recipe data (no "other" yet)
            List<RecipeIngredient> cakeIng =
                    recipeIngredientRepository.findRecipeIngredientsByRecipeId(order.getCakeRecipe().getRecipeId());
            List<RecipeIngredient> fillingIng =
                    recipeIngredientRepository.findRecipeIngredientsByRecipeId(order.getFillingRecipe().getRecipeId());
            List<RecipeIngredient> frostingIng =
                    recipeIngredientRepository.findRecipeIngredientsByRecipeId(order.getFrostingRecipe().getRecipeId());

            cakeOrderService.buildShoppingList(cakeIng, fillingIng, frostingIng, order);
            cakeOrderRepository.save(order);

            // Repoint incomplete shopping tasks at the refreshed list
            List<CakeTask> shopTasks = cakeTaskRepository.findAllByCakeOrderAndTaskTypeInAndCompletedFalse(
                    order, List.of(TaskType.SHOP_PANTRY, TaskType.SHOP_STORE)
            );
            for (CakeTask t : shopTasks) {
                t.setShoppingList(order.getShoppingList());
                cakeTaskRepository.save(t);
            }
        }
    }


    public void deleteRecipe(Integer recipeId) {
        //userRecipeRepository.deleteAllByRecipeId(recipeId);
        //recipeIngredientRepository.deleteAllByRecipeId(recipeId);
        recipeRepository.deleteById(recipeId);
    }
}
