package com.cakeplanner.cake_planner.Entities;

import com.cakeplanner.cake_planner.Entities.Enums.RecipeType;
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

    @Column(name = "flavor_name", nullable = false)
    private String flavorName;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String instructions;

    public Recipe() {
    }

    public Recipe(RecipeType recipeType, String flavorName, String instructions) {
        this.recipeType = recipeType;
        this.flavorName = flavorName;
        this.instructions = instructions;
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

    public String getFlavorName() {
        return flavorName;
    }

    public void setFlavorName(String flavorName) {
        this.flavorName = flavorName;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
}
