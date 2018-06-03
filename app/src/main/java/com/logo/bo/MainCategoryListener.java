package com.logo.bo;

import java.util.List;

/**
 * Created by deepaksingh on 03/06/18.
 */

public interface MainCategoryListener {
    void onMainCategorySelected(List<String> categories);

    void onSubCategorySelected(List<String> subCategories);
}
