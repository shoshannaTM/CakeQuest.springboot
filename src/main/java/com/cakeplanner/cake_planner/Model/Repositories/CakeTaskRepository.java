package com.cakeplanner.cake_planner.Model.Repositories;

import com.cakeplanner.cake_planner.Model.Entities.CakeOrder;
import com.cakeplanner.cake_planner.Model.Entities.CakeTask;
import com.cakeplanner.cake_planner.Model.Entities.Enums.TaskType;
import com.cakeplanner.cake_planner.Model.Entities.Ingredient;
import com.cakeplanner.cake_planner.Model.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CakeTaskRepository extends JpaRepository<CakeTask, Long> {
    List<CakeTask> findAllByUser(User user);

    List<CakeTask> findAllByCakeOrder(CakeOrder cakeOrder);

    CakeOrder findCakeOrderByTaskId(Long taskId);

    CakeTask findByCakeOrderAndTaskType(CakeOrder cakeOrder, TaskType taskType);


    List<CakeTask> findAllByCakeOrderAndTaskTypeInAndCompletedFalse(CakeOrder order, List<Object> objects);
}
