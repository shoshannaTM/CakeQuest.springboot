package com.cakeplanner.cake_planner.Model.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class RecipeIngredientId implements Serializable {
        @Column(name = "recipe_id")
        private int recipeId;

        @Column(name = "ingredient_id")
        private int ingredientId;

        public RecipeIngredientId() {}

        public RecipeIngredientId(int recipeId, int ingredientId) {
            this.recipeId = recipeId;
            this.ingredientId = ingredientId;
        }

        public int getRecipeId() {
            return recipeId;
        }

        public void setRecipeId(int recipeId) {
            this.recipeId = recipeId;
        }

        public int getIngredientId() {
            return ingredientId;
        }

        public void setIngredientId(int ingredientId) {
            this.ingredientId = ingredientId;
        }

        //allows an object to be compared to the composite primary key recipeIngredientId
        //checks if object o is a recipeIngredientId object, if so compares to see if they are equal
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RecipeIngredientId that = (RecipeIngredientId) o;
            return recipeId == that.recipeId && ingredientId == that.ingredientId;
        }

        @Override
        public int hashCode() {
            return Objects.hash(recipeId, ingredientId);
        }
    }
