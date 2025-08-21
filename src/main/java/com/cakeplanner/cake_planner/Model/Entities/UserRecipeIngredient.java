package com.cakeplanner.cake_planner.Model.Entities;

import jakarta.persistence.*;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "user_recipe_ingredient")
public class UserRecipeIngredient {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userRecipeIngredientId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_recipe_id", nullable = false)
    private UserRecipe userRecipe;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;
    @Column(nullable = false) private Double quantity;

    @Column(nullable = false) private String unit;

    public UserRecipeIngredient() {
    }

    public UserRecipeIngredient(UserRecipe userRecipe, Ingredient ingredient, Double quantity,
                                String unit) {
        this.userRecipe = userRecipe;
        this.ingredient = ingredient;
        this.unit = unit;
        this.quantity = quantity;
    }

    public UserRecipeIngredient(Long userRecipeIngredientId,
                                UserRecipe userRecipe, Ingredient ingredient,
                                Double quantity, String unit) {
        this.userRecipeIngredientId = userRecipeIngredientId;
        this.userRecipe = userRecipe;
        this.ingredient = ingredient;
        this.unit = unit;
        this.quantity = quantity;
    }

    public Long getUserRecipeIngredientId() {
        return userRecipeIngredientId;
    }

    public void setUserRecipeIngredientId(Long userRecipeIngredientId) {
        this.userRecipeIngredientId = userRecipeIngredientId;
    }

    public UserRecipe getUserRecipe() {
        return userRecipe;
    }

    public void setUserRecipe(UserRecipe userRecipe) {
        this.userRecipe = userRecipe;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }
}

