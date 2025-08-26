package com.cakeplanner.cake_planner.Model.Repositories;

import com.cakeplanner.cake_planner.Model.Entities.Enums.RecipeType;
import com.cakeplanner.cake_planner.Model.Entities.Recipe;
import com.cakeplanner.cake_planner.Model.Entities.User;
import com.cakeplanner.cake_planner.Model.Entities.UserRecipe;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRecipeRepository extends JpaRepository<UserRecipe, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select ur from UserRecipe ur where ur.userRecipeId = :id")
    Optional<UserRecipe> findForUpdate(@Param("id") Long id);

    List<UserRecipe> findByUser(User user);

    List<UserRecipe> findByUserAndUserRecipeNameContainingIgnoreCase(User user, String name);

    List<UserRecipe> findByUserAndRecipeType(User user, RecipeType type);

    Optional<UserRecipe> findByUserAndBaseRecipe(User user, Recipe baseRecipe);

    Optional<UserRecipe> findByUserRecipeId(Long userRecipeId);

    void deleteByUserAndUserRecipeId(User user, Long userRecipeId);

    boolean existsByUserAndUserRecipeId(User user, Long userRecipeId);
}

