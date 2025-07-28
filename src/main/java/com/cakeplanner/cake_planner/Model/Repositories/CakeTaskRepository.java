package com.cakeplanner.cake_planner.Model.Repositories;

import com.cakeplanner.cake_planner.Model.Entities.CakeTask;
import com.cakeplanner.cake_planner.Model.Entities.Ingredient;
import com.cakeplanner.cake_planner.Model.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CakeTaskRepository extends JpaRepository<CakeTask, Integer> {
    List<CakeTask> findAllByUser(User user);

}
