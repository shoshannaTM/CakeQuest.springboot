package com.cakeplanner.cake_planner.Model.Services;

import com.cakeplanner.cake_planner.Model.DTO.CakeOrderDTO;
import com.cakeplanner.cake_planner.Model.Entities.CakeOrder;
import com.cakeplanner.cake_planner.Model.Entities.User;
import com.cakeplanner.cake_planner.Model.Repositories.CakeOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CakeOrderService {

    @Autowired
    CakeOrderRepository cakeOrderRepository;

    public void save(CakeOrder cakeOrder) {
        cakeOrderRepository.save(cakeOrder);
    }

    public List<CakeOrderDTO> getCakeDTOs(User user) {
        List<CakeOrderDTO> cakeDTOs = new ArrayList<>();
        List<CakeOrder> cakes = cakeOrderRepository.findByUser(user);
        for(CakeOrder cakeOrder: cakes){
            CakeOrderDTO cakeDTO = new CakeOrderDTO(cakeOrder.getCakeId(), cakeOrder.getCakeName(), cakeOrder.getDueDate(),
                                                    cakeOrder.getCakeRecipe(), cakeOrder.getCakeMultiplier(),
                                                    cakeOrder.getFillingRecipe(), cakeOrder.getFillingMultiplier(),
                                                    cakeOrder.getFrostingRecipe(), cakeOrder.getFrostingMultiplier(),
                                                    cakeOrder.getDietaryRestriction(), cakeOrder.getDecorationNotes());

            cakeDTOs.add(cakeDTO);
        }

        return cakeDTOs;
    }
}
