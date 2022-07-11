package com.xandria.tech.model;

import java.util.ArrayList;
import java.util.List;

public class CategoryModel {
    private String categoryName;
    private final List<String> subCategories = new ArrayList<>();

    public CategoryModel(){}
    public CategoryModel(String categoryName, List<String> subCategories){
        setCategoryName(categoryName);
        setSubCategories(subCategories);
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<String> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(List<String> subCategories) {
        if (subCategories != null) this.subCategories.addAll(subCategories);
    }
}
