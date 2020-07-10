package com.webfarms.treatmyfish.bean;

public class BeamSubCategory {

    String subCategoryId;
    String subName;

    public BeamSubCategory(String subCategoryId, String subName) {
        this.subCategoryId = subCategoryId;
        this.subName = subName;
    }

    public String getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(String subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public String getSubName() {
        return subName;
    }

    public void setSubName(String subName) {
        this.subName = subName;
    }
}
