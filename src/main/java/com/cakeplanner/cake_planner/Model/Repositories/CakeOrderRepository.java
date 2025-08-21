package com.cakeplanner.cake_planner.Model.Repositories;

import com.cakeplanner.cake_planner.Model.Entities.CakeOrder;
import com.cakeplanner.cake_planner.Model.Entities.Recipe;
import com.cakeplanner.cake_planner.Model.Entities.User;
import com.cakeplanner.cake_planner.Model.Entities.UserRecipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CakeOrderRepository extends JpaRepository<CakeOrder, Long> {
    List<CakeOrder> findByUser(User user);

    @Query("""
        SELECT co FROM CakeOrder co
        WHERE co.cakeRecipe = :recipe
        OR co.fillingRecipe = :recipe
        OR co.frostingRecipe = :recipe
        """)
    List<CakeOrder> findAllUsingRecipe(Recipe recipe);

    List<CakeOrder> findAllByCakeRecipeOrFillingRecipeOrFrostingRecipe(
            UserRecipe cake, UserRecipe filling, UserRecipe frosting
    );
}
