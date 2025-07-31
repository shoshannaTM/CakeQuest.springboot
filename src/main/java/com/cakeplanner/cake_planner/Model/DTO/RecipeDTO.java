package com.cakeplanner.cake_planner.Model.DTO;

import com.cakeplanner.cake_planner.Model.Entities.Enums.RecipeType;

import java.util.List;

public class RecipeDTO {
    private String recipeName;
    private String recipeUrl;
    private String instructions;
    private RecipeType recipeType;
    private List<IngredientDTO> ingredients;
    private int recipeId;

    public RecipeDTO(String recipeName, String recipeUrl, String instructions, RecipeType recipeType, List<IngredientDTO> ingredients, int recipeId) {
        this.recipeName = recipeName;
        this.recipeUrl = recipeUrl;
        this.instructions = instructions;
        this.recipeType = recipeType;
        this.ingredients = ingredients;
        this.recipeId = recipeId;
    }

    public RecipeDTO(String recipeName, String recipeUrl, String instructions, RecipeType recipeType, List<IngredientDTO> ingredients) {
        this.recipeName = recipeName;
        this.recipeUrl = recipeUrl;
        this.instructions = instructions;
        this.recipeType = recipeType;
        this.ingredients = ingredients;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public String getRecipeUrl() {
        return recipeUrl;
    }

    public void setRecipeUrl(String recipeUrl) {
        this.recipeUrl = recipeUrl;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
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

    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }
}
