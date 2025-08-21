package com.cakeplanner.cake_planner.Model.Services;

import com.cakeplanner.cake_planner.Model.Entities.*;
import com.cakeplanner.cake_planner.Model.Entities.Enums.TaskType;
import com.cakeplanner.cake_planner.Model.Repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class ShoppingService {
    private final CakeTaskRepository cakeTaskRepository;
    private final CakeOrderRepository cakeOrderRepository;
    private final SpoonacularService spoonacularService;
    private final ShoppingListRepository shoppingListRepository;

    public ShoppingService(CakeTaskRepository cakeTaskRepository,
                           CakeOrderRepository cakeOrderRepository,
                           SpoonacularService spoonacularService,
                           ShoppingListRepository shoppingListRepository) {
        this.cakeTaskRepository = cakeTaskRepository;
        this.cakeOrderRepository = cakeOrderRepository;
        this.spoonacularService = spoonacularService;
        this.shoppingListRepository = shoppingListRepository;
    }


    public void idsToShoppingList(List<Long> cakeIds, User user){
        if (cakeIds == null || cakeIds.isEmpty()) return;

        List<ShoppingList> lists = new ArrayList<>();
        List<LocalDateTime> dueList = new ArrayList<>();

        String dietaryRestrictions = "";
        String listName = "";
        //get all shopping lists
        for(Long cakeId : cakeIds){
            Optional<CakeOrder> cakeOrderOptional = cakeOrderRepository.findById(cakeId);
            if(cakeOrderOptional.isEmpty()){
                continue;
            }
                CakeOrder cakeOrder = cakeOrderOptional.get();
                lists.add(cakeOrder.getShoppingList());
                if (cakeOrder.getDueDate() != null) {
                    dueList.add(cakeOrder.getDueDate());
                }
                if (cakeOrder.getCakeName() != null) {
                    listName = listName + cakeOrder.getCakeName() + " ";
                }
                if (cakeOrder.getDietaryRestriction() != null && !cakeOrder.getDietaryRestriction().isBlank()) {
                    dietaryRestrictions = dietaryRestrictions + cakeOrder.getDietaryRestriction() + " ";
                }
            }

        ShoppingList merged = lists.get(0);
        for(int i = 1; i < lists.size(); i++){
            merged = mergeLists(merged, lists.get(i));
        }

        LocalDateTime due = dueList.get(0);
        for(int i = 1; i < dueList.size(); i++){
             due = closestDueDate(due, dueList.get(i));
        }

        shoppingListRepository.save(merged);
        customPantryTask(user, listName, merged, dietaryRestrictions, due);
        customStoreTask(user, listName, merged, dietaryRestrictions, due);
    }

    public LocalDateTime closestDueDate(LocalDateTime a, LocalDateTime b){
        if(a.isEqual(b) || a.isBefore(b)){
            return a;
        }
        return b;
    }

    public void customPantryTask(User user, String listName, ShoppingList shoppingList, String dietaryRestriction, LocalDateTime due) {
        String pantryTaskName = "Check Your Pantry For Ingredients For " + listName;
        CakeTask pantry = new CakeTask(user, null, pantryTaskName, TaskType.SHOP_PANTRY,
                                        shoppingList, dietaryRestriction, due, false);
        cakeTaskRepository.save(pantry);
    }

    public void customStoreTask(User user, String listName, ShoppingList shoppingList, String dietaryRestriction, LocalDateTime due) {
        String storeTaskName = "Buy Ingredients For " + listName;
        CakeTask store = new CakeTask(user, null, storeTaskName, TaskType.SHOP_STORE,
                shoppingList, dietaryRestriction, due, false);
        cakeTaskRepository.save(store);
    }

    public ShoppingList mergeLists(ShoppingList a, ShoppingList b) {
        ShoppingList merged = new ShoppingList(null, new ArrayList<>());
        Map<String, ShoppingListItem> byName = new HashMap<>();

        buildShoppingList(byName, a.getItems(), merged);
        buildShoppingList(byName, b.getItems(), merged);

        merged.getItems().addAll(byName.values());
        return merged;
    }

    private void buildShoppingList(Map<String, ShoppingListItem> byName,
                                      List<ShoppingListItem> items,
                                      ShoppingList targetList) {
        if (items == null) return;
        for (ShoppingListItem item : items) {
            String name = item.getName().trim().toLowerCase();
            String unitB = item.getUnit();
            double amount = item.getAmount();

            ShoppingListItem existing = byName.get(name);
            if (existing == null) {
                byName.put(name, new ShoppingListItem(name, amount, unitB, targetList));
            } else {
                String unitA = existing.getUnit();
                if (unitA.equalsIgnoreCase(unitB)) {
                    existing.setAmount(existing.getAmount() + amount);
                } else {
                    double rate = spoonacularService.getConversionRate(name, unitB, unitA);
                    existing.setAmount(existing.getAmount() + (amount * rate));
                }
            }
        }
    }
}
