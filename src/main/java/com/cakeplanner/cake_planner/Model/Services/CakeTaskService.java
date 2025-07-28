package com.cakeplanner.cake_planner.Model.Services;

import com.cakeplanner.cake_planner.Model.DTO.CakeTaskDTO;
import com.cakeplanner.cake_planner.Model.Entities.CakeTask;
import com.cakeplanner.cake_planner.Model.Entities.Enums.TaskType;
import com.cakeplanner.cake_planner.Model.Entities.User;
import com.cakeplanner.cake_planner.Model.Repositories.CakeTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class CakeTaskService {

    @Autowired
    CakeTaskRepository cakeTaskRepository;
    public List<CakeTaskDTO> getCakeTaskDTOs(User user) {
        List<CakeTask> cakeTasks = cakeTaskRepository.findAllByUser(user);
        List<CakeTaskDTO> cakeTaskDTOs = new ArrayList<>();
        for(CakeTask ct: cakeTasks) {
            if (ct.getTaskType().equals(TaskType.SHOP)) {
                CakeTaskDTO dto = new CakeTaskDTO(
                        ct.getTaskId(),
                        ct.getName(),
                        ct.getTaskType(),
                        ct.getShoppingList().getId(),
                        ct.getDietaryRestriction(),
                        ct.getDueDate()
                );
                cakeTaskDTOs.add(dto);
            } else {
                Integer recipeId = (ct.getRecipe() != null) ? ct.getRecipe().getRecipeId() : null;
                CakeTaskDTO dto = new CakeTaskDTO(
                        ct.getTaskId(),
                        ct.getName(),
                        ct.getTaskType(),
                        recipeId,
                        ct.getDietaryRestriction(),
                        ct.getDecorationNotes(),
                        ct.getDueDate()
                );
                cakeTaskDTOs.add(dto);
            }

        }
        return cakeTaskDTOs;
    }
}
