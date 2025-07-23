package com.cakeplanner.cake_planner.Model.Repositories;

import com.cakeplanner.cake_planner.Model.Entities.CakeOrder;
import com.cakeplanner.cake_planner.Model.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CakeOrderRepository extends JpaRepository<CakeOrder, Integer> {
    List<CakeOrder> findByUser(User user);

}
