package com.cakeplanner.cake_planner.Model.Services;

import com.cakeplanner.cake_planner.Model.DTO.IngredientDTO;
import com.cakeplanner.cake_planner.Model.DTO.RecipeDTO;
import com.cakeplanner.cake_planner.Model.Entities.Enums.RecipeType;
import com.cakeplanner.cake_planner.Model.Entities.Ingredient;
import com.cakeplanner.cake_planner.Model.Entities.Recipe;
import com.cakeplanner.cake_planner.Model.Entities.RecipeIngredient;
import com.cakeplanner.cake_planner.Model.Entities.User;
import com.cakeplanner.cake_planner.Model.Entities.UserRecipe;
import com.cakeplanner.cake_planner.Model.Entities.UserRecipeId;
import com.cakeplanner.cake_planner.Model.Repositories.IngredientRepository;
import com.cakeplanner.cake_planner.Model.Repositories.RecipeIngredientRepository;
import com.cakeplanner.cake_planner.Model.Repositories.RecipeRepository;
import com.cakeplanner.cake_planner.Model.Repositories.UserRecipeRepository;
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
    RecipeRepository recipeRepository;

    @Autowired
    private UserRecipeRepository userRecipeRepository;

    @Autowired
    RecipeIngredientRepository recipeIngredientRepository;

    @Autowired
    IngredientRepository ingredientRepository;

    @Transactional
    public RecipeDTO processRecipeForDisplay(String recipeUrl, RecipeType recipeType, User user) throws IOException {
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
            saveIngredients(recipe, recipeDTO); // pass the recipe object directly
            userRecipeRepository.save(new UserRecipe(new UserRecipeId(recipe.getRecipeId(), user.getUserId()), recipe, user));
        }

        List<RecipeIngredient> recipeIngredients = recipeIngredientRepository.findRecipeIngredientsByRecipeId(recipe.getRecipeId());
        List<IngredientDTO> ingredientDTOList = recipeIngredientsToDTO(recipeIngredients);

        return new RecipeDTO(recipe.getRecipeName(), recipe.getRecipeUrl(), recipe.getInstructions(), recipe.getRecipeType(), ingredientDTOList);
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


    public void saveIngredients(Recipe recipe, RecipeDTO recipeDTO) {
        for (IngredientDTO dto : recipeDTO.getIngredients()) {
            Ingredient ingredient = ingredientRepository
                    .findByIngredientNameIgnoreCase(dto.getName())
                    .orElseGet(() -> ingredientRepository.save(new Ingredient(dto.getName())));
            RecipeIngredient recipeIngredient = new RecipeIngredient(recipe, ingredient, dto.getAmount(), dto.getUnit());
            recipeIngredientRepository.save(recipeIngredient);
        }
    }
}
