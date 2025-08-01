package com.cakeplanner.cake_planner.Model.Services;

import com.cakeplanner.cake_planner.Model.DTO.CakeOrderDTO;
import com.cakeplanner.cake_planner.Model.DTO.CakeTaskDTO;
import com.cakeplanner.cake_planner.Model.Entities.CakeOrder;
import com.cakeplanner.cake_planner.Model.Entities.CakeTask;
import com.cakeplanner.cake_planner.Model.Entities.Enums.TaskType;
import com.cakeplanner.cake_planner.Model.Entities.ShoppingListItem;
import com.cakeplanner.cake_planner.Model.Entities.User;
import com.cakeplanner.cake_planner.Model.Repositories.CakeOrderRepository;
import com.cakeplanner.cake_planner.Model.Repositories.CakeTaskRepository;
import com.cakeplanner.cake_planner.Model.Repositories.RecipeIngredientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
public class CakeTaskService {

    @Autowired
    CakeTaskRepository cakeTaskRepository;

    @Autowired
    CakeOrderRepository cakeOrderRepository;

    @Autowired
    CakeOrderService cakeOrderService;

    @Autowired
    RecipeIngredientRepository recipeIngredientRepository;

    public CakeTaskDTO shoppingTaskToDTO(CakeTask task){
            CakeTaskDTO dto = new CakeTaskDTO(
                    task.getTaskId(),
                    task.getName(),
                    task.getTaskType(),
                    task.getShoppingList(),
                    task.getDietaryRestriction(),
                    task.getDueDate(),
                    task.getCompleted()
            );
            return dto;
    }

    public CakeTaskDTO recipeTaskToDTO(CakeTask task){
        Integer recipeId = (task.getRecipe() != null) ? task.getRecipe().getRecipeId() : null;
        CakeTaskDTO dto = new CakeTaskDTO(
                task.getTaskId(),
                task.getName(),
                task.getTaskType(),
                recipeId,
                task.getDietaryRestriction(),
                task.getDecorationNotes(),
                task.getDueDate(),
                task.getCompleted()
        );
        return dto;
    }

    public CakeTaskDTO decorateTaskToDTO(CakeTask task){
        CakeTaskDTO dto = new CakeTaskDTO(
                task.getTaskId(),
                task.getName(),
                task.getTaskType(),
                task.getDietaryRestriction(),
                task.getDecorationNotes(),
                task.getDueDate(),
                task.getCompleted()
        );
        return dto;
    }

    public CakeTaskDTO getCakeTaskDTObyId(int taskId){
        Optional<CakeTask> taskOptional = cakeTaskRepository.findById(taskId);
        if (taskOptional.isEmpty()) {
            return new CakeTaskDTO();
        }
        CakeTask ct = taskOptional.get();

        if(ct.getTaskType().equals(TaskType.SHOP_PANTRY) || ct.getTaskType().equals(TaskType.SHOP_STORE)){
            CakeTaskDTO dto = shoppingTaskToDTO(ct);
            return dto;
        } else if(ct.getTaskType().equals(TaskType.DECORATE)){
            CakeTaskDTO dto = decorateTaskToDTO(ct);
            return dto;
        } else {
            CakeTaskDTO dto = recipeTaskToDTO(ct);
            return dto;
        }
    }

    public List<CakeTaskDTO> getCakeTaskDTOsForUser(User user) {
        List<CakeTask> cakeTasks = cakeTaskRepository.findAllByUser(user);
        List<CakeTaskDTO> cakeTaskDTOs = new ArrayList<>();
        for(CakeTask ct: cakeTasks) {
            if (ct.getTaskType().equals(TaskType.SHOP_PANTRY) || ct.getTaskType().equals(TaskType.SHOP_STORE)) {
                CakeTaskDTO dto = shoppingTaskToDTO(ct);
                cakeTaskDTOs.add(dto);
            } else if(ct.getTaskType().equals(TaskType.DECORATE)){
                CakeTaskDTO dto = decorateTaskToDTO(ct);
                cakeTaskDTOs.add(dto);
            } else {
                CakeTaskDTO dto = recipeTaskToDTO(ct);
                cakeTaskDTOs.add(dto);
            }
        }
        return cakeTaskDTOs;
    }

    public List<CakeTaskDTO> getCakeTaskDTOsForCake(int id) {
        Optional<CakeOrder> cakeOrderOptional = cakeOrderRepository.findById(id);

        if (cakeOrderOptional.isEmpty()) {
            List<CakeTaskDTO> empty = new ArrayList<>();
            return empty;
        }
        CakeOrder cakeOrder = cakeOrderOptional.get();
        List<CakeTask> cakeTasks = cakeTaskRepository.findAllByCakeOrder(cakeOrder);
        List<CakeTaskDTO> cakeTaskDTOs = new ArrayList<>();
        for(CakeTask ct: cakeTasks) {
            if (ct.getTaskType().equals(TaskType.SHOP_PANTRY) || ct.getTaskType().equals(TaskType.SHOP_STORE)) {
                CakeTaskDTO dto = shoppingTaskToDTO(ct);
                cakeTaskDTOs.add(dto);
            } else if(ct.getTaskType().equals(TaskType.DECORATE)){
                CakeTaskDTO dto = decorateTaskToDTO(ct);
                cakeTaskDTOs.add(dto);
            } else {
                CakeTaskDTO dto = recipeTaskToDTO(ct);
                cakeTaskDTOs.add(dto);
            }
        }
        return cakeTaskDTOs;
    }

    public Boolean toggleTaskComplete(int taskId) {
        Optional<CakeTask> cakeTaskOptional = cakeTaskRepository.findById(taskId);

        if (cakeTaskOptional.isPresent()) {
            CakeTask cakeTask = cakeTaskOptional.get();
            cakeTask.setCompleted(!cakeTask.getCompleted());
            cakeTaskRepository.save(cakeTask);
            return cakeTask.getCompleted();
        }
        return null;
    }

    public boolean markTaskIncomplete(int taskId) {
        Optional<CakeTask> cakeTaskOptional = cakeTaskRepository.findById(taskId);

        if (cakeTaskOptional.isPresent()) {
            CakeTask cakeTask = cakeTaskOptional.get();
            cakeTask.setCompleted(false);
            cakeTaskRepository.save(cakeTask);
            return false;
        }
        return true;
    }

    public TaskType getTaskType(int taskId){
        Optional<CakeTask> cakeTaskOptional = cakeTaskRepository.findById(taskId);

        if (cakeTaskOptional.isPresent()) {
            CakeTask cakeTask = cakeTaskOptional.get();
            return cakeTask.getTaskType();
        }
        return null;
    }

    public void resetPantryList(int taskId){
        Optional<CakeTask> optionalTask = cakeTaskRepository.findById(taskId);
        if (optionalTask.isEmpty()){
            return;
        }
        CakeTask task = optionalTask.get();
        CakeOrder cakeOrder = task.getCakeOrder();

        cakeOrderService.buildShoppingList(
                recipeIngredientRepository.findRecipeIngredientsByRecipeId(cakeOrder.getCakeRecipe().getRecipeId()),
                recipeIngredientRepository.findRecipeIngredientsByRecipeId(cakeOrder.getFillingRecipe().getRecipeId()),
                recipeIngredientRepository.findRecipeIngredientsByRecipeId(cakeOrder.getFrostingRecipe().getRecipeId()),
                cakeOrder
        );
        task.setShoppingList(cakeOrder.getShoppingList());
        cakeTaskRepository.save(task);
    }

    public void processPantryList(int taskId, Map<String, String> pantryData) {
        Optional<CakeTask> optionalTask = cakeTaskRepository.findById(taskId);
        if (optionalTask.isEmpty()){
            return;
        }
        CakeTask task = optionalTask.get();

            List<ShoppingListItem> items = task.getShoppingList().getItems();
            for (ShoppingListItem item : items) {
                String key = item.getName();
                if (pantryData.containsKey(key)) {
                    try {
                        double pantryAmount = Double.parseDouble(pantryData.get(key));
                        item.setAmount(Math.max(0, item.getAmount() - pantryAmount));
                    } catch (NumberFormatException e) {
                        // FIXME
                    }
                }
            }
        cakeTaskRepository.save(task);
    }
}
