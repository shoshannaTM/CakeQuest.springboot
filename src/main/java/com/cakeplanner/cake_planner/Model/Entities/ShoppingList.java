package com.cakeplanner.cake_planner.Model.Entities;

import jakarta.persistence.*;
import jakarta.persistence.Id;

import java.util.ArrayList;
import java.util.List;

@Entity
public class ShoppingList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    private CakeOrder cakeOrder;

    @OneToMany(mappedBy = "shoppingList", cascade = CascadeType.ALL)
    private List<ShoppingListItem> items = new ArrayList<>();

    public ShoppingList() {
    }

    public ShoppingList(CakeOrder cakeOrder, List<ShoppingListItem> items) {
        this.cakeOrder = cakeOrder;
        this.items = (items != null) ? items : new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public CakeOrder getCakeOrder() {
        return cakeOrder;
    }

    public void setCakeOrder(CakeOrder cakeOrder) {
        this.cakeOrder = cakeOrder;
    }

    public List<ShoppingListItem> getItems() {
        return items;
    }

    public void setItems(List<ShoppingListItem> items) {
        this.items = items;
    }

    public void addItem(ShoppingListItem item) {
        item.setShoppingList(this);
        this.items.add(item);
    }
}

