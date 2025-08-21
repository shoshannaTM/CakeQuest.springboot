package com.cakeplanner.cake_planner.Model.DTO;

import com.cakeplanner.cake_planner.Model.Entities.Enums.RecipeType;

import java.util.List;

public class RecipeDTO {
    private Long userRecipeId;
    private String recipeName;
    private RecipeType recipeType;
    private List<IngredientDTO> ingredients;
    private String instructions;

    public RecipeDTO(Long userRecipeId, String recipeName, RecipeType recipeType,
                     List<IngredientDTO> ingredients, String instructions) {
        this.userRecipeId = userRecipeId;
        this.recipeName = recipeName;
        this.recipeType = recipeType;
        this.ingredients = ingredients;
        this.instructions = instructions;
    }

    public Long getUserRecipeId() {
        return userRecipeId;
    }

    public void setUserRecipeId(Long userRecipeId) {
        this.userRecipeId = userRecipeId;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public RecipeType getRecipeType() {
        return recipeType;
    }

    public void setRecipeType(RecipeType recipeType) {
        this.recipeType = recipeType;
    }

    public List<IngredientDTO> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<IngredientDTO> ingredients) {
        this.ingredients = ingredients;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
}
