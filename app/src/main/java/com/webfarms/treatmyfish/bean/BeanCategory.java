package com.webfarms.treatmyfish.bean;

public class BeanCategory {

    String id;
    String category;

    public BeanCategory(String id, String category) {
        this.id = id;
        this.category = category;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
