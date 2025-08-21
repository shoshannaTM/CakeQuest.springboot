package com.cakeplanner.cake_planner.Model.Entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "recipe", indexes = {
        @Index(name="unique_recipe_url", columnList="recipe_url", unique = true)
})
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "base_recipe_id")
    private Long baseRecipeId;

    @Column(name = "recipe_url", nullable = false)
    private String recipeUrl;

    @Column(name = "base_recipe_name", nullable = false)
    private String baseRecipeName;

    @Column(name = "base_recipe_instructions", columnDefinition = "TEXT")
    private String baseRecipeInstructions;


    @OneToMany(mappedBy = "recipe",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = LAZY)
    private List<RecipeIngredient> baseRecipeIngredients;

    public Recipe() {
    }

    public Recipe(String recipeUrl, String baseRecipeName,
                  String baseRecipeInstructions,
                  List<RecipeIngredient> baseRecipeIngredients) {
        this.recipeUrl = recipeUrl;
        this.baseRecipeName = baseRecipeName;
        this.baseRecipeInstructions = baseRecipeInstructions;
        this.baseRecipeIngredients = baseRecipeIngredients;
    }

    public Recipe(Long baseRecipeId, String recipeUrl,
                  String baseRecipeName, String baseRecipeInstructions,
                  List<RecipeIngredient> baseRecipeIngredients) {
        this.baseRecipeId = baseRecipeId;
        this.recipeUrl = recipeUrl;
        this.baseRecipeName = baseRecipeName;
        this.baseRecipeInstructions = baseRecipeInstructions;
        this.baseRecipeIngredients = baseRecipeIngredients;
    }

    public Long getBaseRecipeId() {
        return baseRecipeId;
    }

    public void setBaseRecipeId(Long baseRecipeId) {
        this.baseRecipeId = baseRecipeId;
    }

    public String getRecipeUrl() {
        return recipeUrl;
    }

    public void setRecipeUrl(String recipeUrl) {
        this.recipeUrl = recipeUrl;
    }

    public String getBaseRecipeName() {
        return baseRecipeName;
    }

    public void setBaseRecipeName(String baseRecipeName) {
        this.baseRecipeName = baseRecipeName;
    }

    public String getBaseRecipeInstructions() {
        return baseRecipeInstructions;
    }

    public void setBaseRecipeInstructions(String baseRecipeInstructions) {
        this.baseRecipeInstructions = baseRecipeInstructions;
    }

    public List<RecipeIngredient> getBaseRecipeIngredients() {
        return baseRecipeIngredients;
    }

    public void setBaseRecipeIngredients(List<RecipeIngredient> baseRecipeIngredients) {
        this.baseRecipeIngredients = baseRecipeIngredients;
    }
}