package com.cakeplanner.cake_planner.Model.Repositories;

import com.cakeplanner.cake_planner.Model.Entities.Enums.RecipeType;
import com.cakeplanner.cake_planner.Model.Entities.Recipe;
import com.cakeplanner.cake_planner.Model.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RecipeRepository extends JpaRepository<Recipe, Integer> {
    Optional<Recipe> findRecipeByRecipeUrl(String recipeUrl);
    @Query("SELECT r.recipeId FROM Recipe r WHERE r.recipeUrl = :recipeUrl")
    Integer findRecipeIdByRecipeUrl(@Param("recipeUrl") String recipeUrl);

    Recipe findRecipeByRecipeId(Integer recipeId);

    List<Recipe> findAll();

    List<Recipe> findByRecipeType(RecipeType type);

    List<Recipe> findByRecipeNameContainingIgnoreCase(String query);
}
