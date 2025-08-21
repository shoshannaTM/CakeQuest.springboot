package com.cakeplanner.cake_planner.Model.Entities;

import jakarta.persistence.*;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name="recipe_ingredient")
public class RecipeIngredient {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="recipe_ingredient_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="base_recipe_id", nullable=false)
    private Recipe recipe;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="ingredient_id", nullable=false)
    private Ingredient ingredient;

    @Column(name="quantity", nullable=false)
    private Double quantity;

    @Column(name="unit", nullable=false, length=32)
    private String unit;

    public RecipeIngredient() {
    }

    public RecipeIngredient(Recipe recipe, Ingredient ingredient,
                            Double quantity, String unit) {
        this.recipe = recipe;
        this.ingredient = ingredient;
        this.quantity = quantity;
        this.unit = unit;
    }

    public RecipeIngredient(Long id, Recipe recipe,
                            Ingredient ingredient, Double quantity,
                            String unit) {
        this.id = id;
        this.recipe = recipe;
        this.ingredient = ingredient;
        this.quantity = quantity;
        this.unit = unit;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
