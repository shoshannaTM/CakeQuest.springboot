package com.cakeplanner.cake_planner.Model.Entities;

import com.cakeplanner.cake_planner.Model.Entities.Enums.RecipeType;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "user_recipe",
       uniqueConstraints = @UniqueConstraint(name="unique_r_unique_u", columnNames = {"user_id","base_recipe_id"}))
public class UserRecipe {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_recipe_id")
    private Long userRecipeId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "base_recipe_id")
    private Recipe baseRecipe;

    @Enumerated(EnumType.STRING)
    @Column(name = "recipe_type")
    private RecipeType recipeType;

    @Column(name = "user_recipe_name")
    private String userRecipeName;

    @Column(name = "user_recipe_instructions", columnDefinition = "TEXT")
    private String userRecipeInstructions;

    @OneToMany(mappedBy = "userRecipe", cascade = CascadeType.ALL, orphanRemoval = true, fetch = LAZY)
    private List<UserRecipeIngredient> userRecipeIngredients = new ArrayList<>();

    public UserRecipe() {}

    public UserRecipe(User user, Recipe baseRecipe, RecipeType recipeType,
                      String userRecipeName, String userRecipeInstructions,
                      List<UserRecipeIngredient> userRecipeIngredients) {
        this.user = user;
        this.baseRecipe = baseRecipe;
        this.recipeType = recipeType;
        this.userRecipeName = userRecipeName;
        this.userRecipeInstructions = userRecipeInstructions;
        this.userRecipeIngredients = userRecipeIngredients;
    }

    public UserRecipe(Long userRecipeId, User user, Recipe baseRecipe,
                      RecipeType recipeType, String userRecipeName,
                      String userRecipeInstructions,
                      List<UserRecipeIngredient> userRecipeIngredients) {
        this.userRecipeId = userRecipeId;
        this.user = user;
        this.baseRecipe = baseRecipe;
        this.recipeType = recipeType;
        this.userRecipeName = userRecipeName;
        this.userRecipeInstructions = userRecipeInstructions;
        this.userRecipeIngredients = userRecipeIngredients;
    }

    public Long getUserRecipeId() {
        return userRecipeId;
    }

    public void setUserRecipeId(Long userRecipeId) {
        this.userRecipeId = userRecipeId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Recipe getBaseRecipe() {
        return baseRecipe;
    }

    public void setBaseRecipe(Recipe baseRecipe) {
        this.baseRecipe = baseRecipe;
    }

    public RecipeType getRecipeType() {
        return recipeType;
    }

    public void setRecipeType(RecipeType recipeType) {
        this.recipeType = recipeType;
    }

    public String getUserRecipeName() {
        return userRecipeName;
    }

    public void setUserRecipeName(String userRecipeName) {
        this.userRecipeName = userRecipeName;
    }

    public String getUserRecipeInstructions() {
        return userRecipeInstructions;
    }

    public void setUserRecipeInstructions(String userRecipeInstructions) {
        this.userRecipeInstructions = userRecipeInstructions;
    }

    public List<UserRecipeIngredient> getUserRecipeIngredients() {
        return userRecipeIngredients;
    }

    public void setUserRecipeIngredients(List<UserRecipeIngredient> userRecipeIngredients) {
        this.userRecipeIngredients = userRecipeIngredients;
    }
}
