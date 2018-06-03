package com.logo.bo;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by deepaksingh on 03/06/18.
 */

public class CategoryWrapper implements Serializable {
    private Map<String, Integer> categoryMap;

    public CategoryWrapper(Map<String, Integer> categoryMap) {
        this.categoryMap = categoryMap;
    }

    public Map<String, Integer> getCategoryMap() {
        return categoryMap;
    }

    public void setCategoryMap(Map<String, Integer> categoryMap) {
        this.categoryMap = categoryMap;
    }
}
