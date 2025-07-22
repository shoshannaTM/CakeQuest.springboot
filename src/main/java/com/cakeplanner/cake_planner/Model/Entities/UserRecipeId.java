package com.cakeplanner.cake_planner.Model.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
@Embeddable
public class UserRecipeId implements Serializable {
    @Column(name = "recipe_id")
    private int recipeId;

    @Column(name = "user_id")
    private int userId;

    public UserRecipeId() {}

    public UserRecipeId(int recipeId, int userId) {
        this.recipeId = recipeId;
        this.userId = userId;
    }

    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
