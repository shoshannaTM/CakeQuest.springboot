package com.cakeplanner.cake_planner.Model.Services;

import com.cakeplanner.cake_planner.Model.DTO.CakeOrderDTO;
import com.cakeplanner.cake_planner.Model.DTO.CakeTaskDTO;
import com.cakeplanner.cake_planner.Model.Entities.CakeOrder;
import com.cakeplanner.cake_planner.Model.Entities.CakeTask;
import com.cakeplanner.cake_planner.Model.Entities.Enums.TaskType;
import com.cakeplanner.cake_planner.Model.Entities.ShoppingListItem;
import com.cakeplanner.cake_planner.Model.Entities.User;
import com.cakeplanner.cake_planner.Model.Repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class CakeTaskService {
    private final CakeOrderService cakeOrderService;
    private final CakeTaskRepository cakeTaskRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final UserRecipeIngredientRepository userRecipeIngredientRepository;
    private final CakeOrderRepository cakeOrderRepository;

    public CakeTaskService(CakeOrderService cakeOrderService,
                            CakeTaskRepository cakeTaskRepository,
                            RecipeIngredientRepository recipeIngredientRepository,
                            UserRecipeIngredientRepository userRecipeIngredientRepository,
                            CakeOrderRepository cakeOrderRepository) {
        this.cakeOrderService = cakeOrderService;
        this.cakeTaskRepository = cakeTaskRepository;
        this.recipeIngredientRepository = recipeIngredientRepository;
        this.userRecipeIngredientRepository = userRecipeIngredientRepository;
        this.cakeOrderRepository = cakeOrderRepository;
    }


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
        Long recipeId = (task.getUserRecipe() != null) ? task.getUserRecipe().getUserRecipeId() : null;
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

    public CakeTaskDTO getCakeTaskDTObyId(Long taskId){
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

    public List<CakeTaskDTO> getIncompleteTasks(List<CakeTaskDTO> usersTasks){
        List<CakeTaskDTO> incompleteTasks = new ArrayList<>();
        for(CakeTaskDTO ct : usersTasks){
            if(!ct.getCompleted()){
                incompleteTasks.add(ct);
            }
        }
        return incompleteTasks;
    }

    public List<CakeTaskDTO> getCompletedTasks(List<CakeTaskDTO> usersTasks){
        List<CakeTaskDTO> completedTasks = new ArrayList<>();
        for(CakeTaskDTO ct : usersTasks){
            if(ct.getCompleted()){
                completedTasks.add(ct);
            }
        }
        return completedTasks;
    }

    public Map<CakeOrderDTO, Integer> getUpcomingCakes(List<CakeOrderDTO> cakes){
        Map<CakeOrderDTO, Integer> upcomingCakes = new HashMap<>();
        for(CakeOrderDTO cake: cakes){
            Optional<CakeOrder> cakeOrderOptional = cakeOrderRepository.findById(cake.getCakeId());

            if (cakeOrderOptional.isEmpty()) {
                continue;
            }

            CakeOrder cakeOrder = cakeOrderOptional.get();
            Integer progress = progressPercent(cakeOrder);

            if(progress < 100) {
                upcomingCakes.put(cake, progress);
            }
        }
        return upcomingCakes;
    }

    public Map<CakeOrderDTO, Integer> getPastCakes(List<CakeOrderDTO> cakes){
        Map<CakeOrderDTO, Integer> pastCakes = new HashMap<>();
        for(CakeOrderDTO cake: cakes){
            Optional<CakeOrder> cakeOrderOptional = cakeOrderRepository.findById(cake.getCakeId());

            if (cakeOrderOptional.isEmpty()) {
                continue;
            }

            CakeOrder cakeOrder = cakeOrderOptional.get();
            Integer progress = progressPercent(cakeOrder);

            if(progress == 100) {
                pastCakes.put(cake, progress);
            }
        }
        return pastCakes;
    }

    public Integer progressPercent(CakeOrder cakeOrder) {
        List<CakeTask> cakeTasks = cakeTaskRepository.findAllByCakeOrder(cakeOrder);

        if (cakeTasks.isEmpty()) {
            return 0;
        }
        int completedCount = 0;
        for (CakeTask ct : cakeTasks) {
            if (ct.getCompleted()) {
                completedCount++;
            }
        }
        double progress = ((double) completedCount / cakeTasks.size()) * 100;
        Integer progressInt = (int) Math.round(progress);
        return progressInt;
    }

    public List<CakeTaskDTO> getCakeTaskDTOsForCake(Long id) {
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

    public Boolean toggleTaskComplete(Long taskId) {
        Optional<CakeTask> cakeTaskOptional = cakeTaskRepository.findById(taskId);

        if (cakeTaskOptional.isPresent()) {
            CakeTask cakeTask = cakeTaskOptional.get();
            cakeTask.setCompleted(!cakeTask.getCompleted());
            cakeTaskRepository.save(cakeTask);
            return cakeTask.getCompleted();
        }
        return null;
    }

    public void resetPantryList(Long taskId){
        Optional<CakeTask> optionalTask = cakeTaskRepository.findById(taskId);
        if (optionalTask.isEmpty()){
            return;
        }
        CakeTask task = optionalTask.get();
        CakeOrder cakeOrder = task.getCakeOrder();

        cakeOrderService.buildShoppingList(
                userRecipeIngredientRepository.findAllByUserRecipe_UserRecipeId(cakeOrder.getCakeRecipe().getUserRecipeId()),
                userRecipeIngredientRepository.findAllByUserRecipe_UserRecipeId(cakeOrder.getFillingRecipe().getUserRecipeId()),
                userRecipeIngredientRepository.findAllByUserRecipe_UserRecipeId(cakeOrder.getFrostingRecipe().getUserRecipeId()),
                cakeOrder);
        task.setShoppingList(cakeOrder.getShoppingList());
        cakeTaskRepository.save(task);
    }

    public void processPantryList(Long taskId, Map<String, String> pantryData) {
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

    public double getMultiplier(Long taskId, TaskType taskType){
        Optional<CakeTask> optionalTask = cakeTaskRepository.findById(taskId);
        if (optionalTask.isEmpty()){
            return -1;
        }
        CakeTask task = optionalTask.get();
        CakeOrder cakeOrder = task.getCakeOrder();
        if(taskType.equals(TaskType.MAKE_FROSTING)) {
            double frostingMultiplier = cakeOrder.getFrostingMultiplier();
            return frostingMultiplier;
        } else if(taskType.equals(TaskType.MAKE_FILLING)){
            double fillingMultiplier = cakeOrder.getFillingMultiplier();
            return fillingMultiplier;
        } else {
            double cakeMultiplier = cakeOrder.getCakeMultiplier();
            return cakeMultiplier;
        }
    }

    public List<CakeTaskDTO> getIncompleteShoppingTasksForUser(User user) {
        // Get all CakeTaskDTOs for the user
        List<CakeTaskDTO> allTaskDTOs = getCakeTaskDTOsForUser(user);

        // Filter to only incomplete shopping tasks
        List<CakeTaskDTO> incompleteShoppingTasks = new ArrayList<>();
        for (CakeTaskDTO task : allTaskDTOs) {
            if ((task.getTaskType() == TaskType.SHOP_PANTRY || task.getTaskType() == TaskType.SHOP_STORE)
                    && !task.getCompleted()) {
                incompleteShoppingTasks.add(task);
            }
        }

        return incompleteShoppingTasks;
    }

}
