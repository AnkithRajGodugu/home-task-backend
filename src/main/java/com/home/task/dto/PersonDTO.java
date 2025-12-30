package com.home.task.dto;

public class PersonDTO {

    private String name;
    private boolean canCook;
    private boolean available;

    public PersonDTO() {}

    public String getName() {
        return name;
    }

    public boolean isCanCook() {
        return canCook;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCanCook(boolean canCook) {
        this.canCook = canCook;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
