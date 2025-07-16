package com.cakeplanner.cake_planner.Model.Repositories;

import com.cakeplanner.cake_planner.Model.Entities.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IngredientRepository extends JpaRepository<Ingredient, Integer> {
    //use optional, because this is to check whether ingredient exists, so high likelihood of retuning null
    Optional<Ingredient> findByIngredientNameIgnoreCase(String ingredientName);
}
