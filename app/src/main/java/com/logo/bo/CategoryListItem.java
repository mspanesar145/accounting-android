package com.logo.bo;

/**
 * Created by deepaksingh on 04/06/18.
 */

public class CategoryListItem {
    private String name;
    private boolean isSelected;

    public CategoryListItem(String name, boolean isSelected) {
        this.name = name;
        this.isSelected = isSelected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
