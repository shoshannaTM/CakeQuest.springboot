package com.cakeplanner.cake_planner.Model.DTO;

import com.cakeplanner.cake_planner.Model.Entities.Enums.RecipeType;

import java.util.ArrayList;
import java.util.List;

public class EditRecipeDTO {
    private int recipeId;
    private String recipeName;
    private List<IngredientDTO> ingredients = new ArrayList<>();
    private List<String> instructions = new ArrayList<>();


    public EditRecipeDTO() {
    }

    public EditRecipeDTO(int recipeId, String recipeName, List<IngredientDTO> ingredients, List<String> instructions) {
        this.recipeId = recipeId;
        this.recipeName = recipeName;
        this.ingredients = ingredients;
        this.instructions = instructions;
    }

    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
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
