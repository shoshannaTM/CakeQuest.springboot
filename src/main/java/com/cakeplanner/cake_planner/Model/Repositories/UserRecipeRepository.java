package com.cakeplanner.cake_planner.Model.Repositories;

import com.cakeplanner.cake_planner.Model.Entities.Enums.RecipeType;
import com.cakeplanner.cake_planner.Model.Entities.Recipe;
import com.cakeplanner.cake_planner.Model.Entities.User;
import com.cakeplanner.cake_planner.Model.Entities.UserRecipe;
import com.cakeplanner.cake_planner.Model.Entities.UserRecipeId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRecipeRepository extends JpaRepository<UserRecipe, UserRecipeId> {

    boolean existsByUserAndRecipe(User user, Recipe recipe);
    List<UserRecipe> findByUser(User user);
    List<UserRecipe> findByUserAndRecipe_RecipeNameContainingIgnoreCase(User user, String name);
    List<UserRecipe> findByUserAndRecipe_RecipeType(User user, RecipeType type);
}
