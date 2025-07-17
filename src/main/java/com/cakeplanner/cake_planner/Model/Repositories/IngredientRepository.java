package com.cakeplanner.cake_planner.Model.Repositories;

import com.cakeplanner.cake_planner.Model.Entities.Ingredient;
import com.cakeplanner.cake_planner.Model.Entities.RecipeIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IngredientRepository extends JpaRepository<Ingredient, Integer> {
    //use optional, because this is to check whether ingredient exists, so high likelihood of retuning null
    Optional<Ingredient> findByIngredientNameIgnoreCase(String ingredientName);

    @Query("SELECT ri.ingredient FROM RecipeIngredient ri WHERE ri.recipe.recipeId = :recipeId")
    List<Ingredient> findIngredientsByRecipeId(@Param("recipeId") Integer recipeId);

}
