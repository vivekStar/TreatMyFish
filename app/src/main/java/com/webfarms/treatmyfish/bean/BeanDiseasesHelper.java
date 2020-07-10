package com.webfarms.treatmyfish.bean;

/**
 * Created by WEBFARMSPC2 on 3/23/2018.
 */

public class BeanDiseasesHelper {

    String suggestedTreatment;
    String managementStrategie;

    public BeanDiseasesHelper(String suggestedTreatment, String managementStrategie) {
        this.suggestedTreatment = suggestedTreatment;
        this.managementStrategie = managementStrategie;
    }

    public String getSuggestedTreatment() {
        return suggestedTreatment;
    }

    public void setSuggestedTreatment(String suggestedTreatment) {
        this.suggestedTreatment = suggestedTreatment;
    }

    public String getManagementStrategie() {
        return managementStrategie;
    }

    public void setManagementStrategie(String managementStrategie) {
        this.managementStrategie = managementStrategie;
    }
}
