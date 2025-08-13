package com.cakeplanner.cake_planner.Model.Entities;

import com.cakeplanner.cake_planner.Model.Entities.Enums.RecipeType;
import jakarta.persistence.*;

import java.util.Set;

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

    @Column(name = "recipe_url", nullable = false)
    private String recipeUrl;

    @OneToMany(
            mappedBy = "recipe",
            cascade = CascadeType.REMOVE,
            orphanRemoval = true
    )
    private java.util.Set<RecipeIngredient> recipeIngredients = new java.util.HashSet<>();

    @OneToMany(
            mappedBy = "recipe",
            cascade = CascadeType.REMOVE,
            orphanRemoval = true
    )
    private java.util.Set<UserRecipe> userRecipes = new java.util.HashSet<>();

    public Recipe() {
    }

    public Recipe(int recipeId, RecipeType recipeType, String recipeName, String instructions, String recipeUrl) {
        this.recipeId = recipeId;
        this.recipeType = recipeType;
        this.recipeName = recipeName;
        this.instructions = instructions;
        this.recipeUrl = recipeUrl;
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

    public String getRecipeUrl() {
        return recipeUrl;
    }

    public void setRecipeUrl(String recipeUrl) {
        this.recipeUrl = recipeUrl;
    }

    public Set<RecipeIngredient> getRecipeIngredients() {
        return recipeIngredients;
    }

    public void setRecipeIngredients(Set<RecipeIngredient> recipeIngredients) {
        this.recipeIngredients = recipeIngredients;
    }

    public Set<UserRecipe> getUserRecipes() {
        return userRecipes;
    }

    public void setUserRecipes(Set<UserRecipe> userRecipes) {
        this.userRecipes = userRecipes;
    }
}