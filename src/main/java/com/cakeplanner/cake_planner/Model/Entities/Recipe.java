package com.cakeplanner.cake_planner.Model.Entities;

import com.cakeplanner.cake_planner.Model.Entities.Enums.RecipeType;
import jakarta.persistence.*;

@Entity
@Table(name = "recipe")

public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_id")
    private int recipeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "recipe_type", nullable = false)
    private RecipeType recipeType;

    @Column(name = "recipe_name", nullable = false)
    private String recipeName;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String instructions;

    @Column(name = "recipe_url", nullable = false, unique = true)
    private String recipeUrl;

    public Recipe() {
    }

    public Recipe(RecipeType recipeType, String recipeName, String instructions, String recipeUrl) {
        this.recipeType = recipeType;
        this.recipeName = recipeName;
        this.instructions = instructions;
        this.recipeUrl = recipeUrl;
    }

    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    public RecipeType getRecipeType() {
        return recipeType;
    }

    public void setRecipeType(RecipeType recipeType) {
        this.recipeType = recipeType;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getRecipeUrl() { return recipeUrl; }

    public void setRecipeUrl(String recipeUrl) { this.recipeUrl = recipeUrl; }
}
