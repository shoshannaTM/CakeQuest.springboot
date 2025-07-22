package com.cakeplanner.cake_planner.Model.Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "user_recipe")
public class UserRecipe {

    @EmbeddedId
    private UserRecipeId userRecipeId;

    @ManyToOne
    @MapsId("recipeId")
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    //Links ingredient_id in composite key to Ingredient entity
    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public UserRecipe() {
    }

    public UserRecipe(UserRecipeId userRecipeId, Recipe recipe, User user) {
        this.userRecipeId = userRecipeId;
        this.recipe = recipe;
        this.user = user;
    }

    public UserRecipeId getUserRecipeId() {
        return userRecipeId;
    }

    public void setUserRecipeId(UserRecipeId userRecipeId) {
        this.userRecipeId = userRecipeId;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
