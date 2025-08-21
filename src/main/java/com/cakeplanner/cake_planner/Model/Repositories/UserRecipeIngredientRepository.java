package com.cakeplanner.cake_planner.Model.Repositories;

import com.cakeplanner.cake_planner.Model.Entities.UserRecipeIngredient;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRecipeIngredientRepository extends JpaRepository<UserRecipeIngredient, Long> {

    //return a list of recipe ingredients from the recipe whose id is passed
    @EntityGraph(attributePaths = "ingredient")
    List<UserRecipeIngredient> findAllByUserRecipe_UserRecipeId(Long userRecipeId);

}
