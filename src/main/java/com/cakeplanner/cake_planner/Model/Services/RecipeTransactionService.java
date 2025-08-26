package com.cakeplanner.cake_planner.Model.Services;

import com.cakeplanner.cake_planner.Model.DTO.EditRecipeDTO;
import com.cakeplanner.cake_planner.Model.DTO.IngredientDTO;
import com.cakeplanner.cake_planner.Model.Entities.*;
import com.cakeplanner.cake_planner.Model.Entities.Enums.RecipeType;
import com.cakeplanner.cake_planner.Model.Repositories.*;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class RecipeTransactionService {
    private final RecipeRepository recipeRepository;
    private final UserRecipeRepository userRecipeRepository;

    public RecipeTransactionService(RecipeRepository recipeRepository,
                                    UserRecipeRepository userRecipeRepository) {
        this.recipeRepository = recipeRepository;
        this.userRecipeRepository = userRecipeRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Recipe saveBaseRecipe(Recipe scraped) {
        return recipeRepository.save(scraped);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public EditRecipeDTO emptyUserRecipeForManual(User user, @Nullable RecipeType recipeType) {
        UserRecipe ur = new UserRecipe();
        ur.setUser(user);
        ur.setRecipeType(recipeType);
        ur.setUserRecipeName("");
        ur.setUserRecipeInstructions("");
        ur.setUserRecipeIngredients(new ArrayList<>());
        ur = userRecipeRepository.saveAndFlush(ur);

        return new EditRecipeDTO(
                ur.getUserRecipeId(),
                ur.getUserRecipeName(),
                ur.getRecipeType(),
                new ArrayList<>(),
                new ArrayList<>()
        );
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UserRecipe getOrCreateUserRecipe(User user, Recipe recipe, RecipeType recipeType) {
        return userRecipeRepository.findByUserAndBaseRecipe(user, recipe)
                .orElseGet(() -> {
                    UserRecipe ur = new UserRecipe();

                    List<RecipeIngredient> recipeIngredients =
                            recipe.getBaseRecipeIngredients() != null
                                    ? recipe.getBaseRecipeIngredients() : List.of();

                    List<UserRecipeIngredient> urIngredients =
                            recipeIngToUserRecipeIng(recipeIngredients, ur);

                    ur.setUser(user);
                    ur.setBaseRecipe(recipe);
                    ur.setRecipeType(recipeType);
                    ur.setUserRecipeName(recipe.getBaseRecipeName());
                    ur.setUserRecipeInstructions(recipe.getBaseRecipeInstructions());
                    ur.setUserRecipeIngredients(urIngredients);

                    return userRecipeRepository.save(ur);
                });
    }

    private List<UserRecipeIngredient> recipeIngToUserRecipeIng(List<RecipeIngredient> recipeIngredients,
                                                                UserRecipe userRecipe) {
        List<UserRecipeIngredient> userIng = new ArrayList<>();
        for (RecipeIngredient ri : recipeIngredients) {
            UserRecipeIngredient uri = new UserRecipeIngredient(
                    userRecipe,
                    ri.getIngredient(),
                    ri.getQuantity(),
                    ri.getUnit()
            );
            userIng.add(uri);
        }
        return userIng;
    }

    public List<IngredientDTO> userRecipeIngredientsToDTO(List<UserRecipeIngredient> userRecipeIngredients) {
        if (userRecipeIngredients == null) return List.of();

        List<IngredientDTO> dtoList = new ArrayList<>();
        for (UserRecipeIngredient uri: userRecipeIngredients) {
            IngredientDTO dto = new IngredientDTO(
                    uri.getIngredient().getIngredientName(),
                    uri.getQuantity(),
                    uri.getUnit()
            );
            dtoList.add(dto);
        }
        return dtoList;
    }
}
