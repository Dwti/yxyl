package com.yanxiu.gphone.student.exercise.bean;

/**
 * Created by sp on 17-7-26.
 */

public class EditionChildBean {
    private String id;
    private String name;
    private boolean selected = false;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}