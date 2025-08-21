package com.cakeplanner.cake_planner.Model.Repositories;

import com.cakeplanner.cake_planner.Model.Entities.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    Optional<Recipe> findByRecipeUrl(String recipeUrl);

   /* @Query("select r.baseRecipeId from Recipe r where r.recipeUrl = :recipeUrl")
    Long findRecipeIdByRecipeUrl(@Param("recipeUrl") String recipeUrl);

    //Recipe findBaseRecipeByBaseRecipeId(@Param("baseRecipeId") Long baseRecipeId);

    @Query("""
        select r
        from Recipe r
        where lower(r.baseRecipeName) like lower(concat('%', :query, '%'))
    """)
    List<Recipe> findByRecipeNameContainingIgnoreCase(@Param("query") String query);*/
}
