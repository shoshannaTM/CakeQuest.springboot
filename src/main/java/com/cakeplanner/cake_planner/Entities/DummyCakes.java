package com.cakeplanner.cake_planner.Entities;


public class DummyCakes {
private String name;
private String cakeFlavor;
private String fillingFlavor;
private String frostingFlavor;
private String dueDate;

private int progress;

private int id;

    public DummyCakes(String name, String cakeFlavor, String fillingFlavor,
                      String frostingFlavor, String dueDate,
                      int progress, int id) {
        this.name = name;
        this.cakeFlavor = cakeFlavor;
        this.fillingFlavor = fillingFlavor;
        this.frostingFlavor = frostingFlavor;
        this.dueDate = dueDate;
        this.progress = progress;
        this.id = id;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCakeFlavor() {
        return cakeFlavor;
    }

    public void setCakeFlavor(String cakeFlavor) {
        this.cakeFlavor = cakeFlavor;
    }

    public String getFillingFlavor() {
        return fillingFlavor;
    }

    public void setFillingFlavor(String fillingFlavor) {
        this.fillingFlavor = fillingFlavor;
    }

    public String getFrostingFlavor() {
        return frostingFlavor;
    }

    public void setFrostingFlavor(String frostingFlavor) {
        this.frostingFlavor = frostingFlavor;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
