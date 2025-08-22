package com.cakeplanner.cake_planner.Model.DTO;

import com.cakeplanner.cake_planner.Model.Entities.Enums.RecipeType;

import java.util.ArrayList;
import java.util.List;

public class EditRecipeDTO {
    private Long userRecipeId;
    private String recipeName;
    private RecipeType RecipeType;
    private List<IngredientDTO> ingredients = new ArrayList<>();
    private List<String> instructions = new ArrayList<>();


    public EditRecipeDTO() {
    }

    public EditRecipeDTO(Long userRecipeId, String recipeName,
                         RecipeType recipeType, List<IngredientDTO> ingredients,
                         List<String> instructions) {
        this.userRecipeId = userRecipeId;
        this.recipeName = recipeName;
        RecipeType = recipeType;
        this.ingredients = ingredients;
        this.instructions = instructions;
    }

    public EditRecipeDTO(String recipeName, RecipeType recipeType,
                         List<IngredientDTO> ingredients,
                         List<String> instructions) {
        this.recipeName = recipeName;
        RecipeType = recipeType;
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

    public com.cakeplanner.cake_planner.Model.Entities.Enums.RecipeType getRecipeType() {
        return RecipeType;
    }

    public void setRecipeType(com.cakeplanner.cake_planner.Model.Entities.Enums.RecipeType recipeType) {
        RecipeType = recipeType;
    }

    public List<IngredientDTO> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<IngredientDTO> ingredients) {
        this.ingredients = ingredients;
    }

    public List<String> getInstructions() {
        return instructions;
    }

    public void setInstructions(List<String> instructions) {
        this.instructions = instructions;
    }

    public String instructionsToString() {
        StringBuilder sb = new StringBuilder();

        for (String s : this.instructions) {
            if (s != null) {
                s = s.trim();
                if (!s.isEmpty()) {
                    if (sb.length() > 0) {
                        sb.append("|");
                    }
                    sb.append(s);
                }
            }
        }
        return sb.toString();
    }

}
