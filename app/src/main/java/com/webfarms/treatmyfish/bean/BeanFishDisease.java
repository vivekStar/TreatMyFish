package com.webfarms.treatmyfish.bean;

import java.util.ArrayList;

/**
 * Created by WEBFARMSPC2 on 3/31/2018.
 */

public class BeanFishDisease {


    ArrayList<String>clinicalSignsList;
    ArrayList<String>diagnosticsList;
    String suggestedTreatment;
    String managementStrategie;


    public BeanFishDisease(ArrayList<String> clinicalSignsList, ArrayList<String> diagnosticsList, String suggestedTreatment, String managementStrategie) {
        this.clinicalSignsList = clinicalSignsList;
        this.diagnosticsList = diagnosticsList;
        this.suggestedTreatment = suggestedTreatment;
        this.managementStrategie = managementStrategie;
    }

    public ArrayList<String> getClinicalSignsList() {
        return clinicalSignsList;
    }

    public void setClinicalSignsList(ArrayList<String> clinicalSignsList) {
        this.clinicalSignsList = clinicalSignsList;
    }

    public ArrayList<String> getDiagnosticsList() {
        return diagnosticsList;
    }

    public void setDiagnosticsList(ArrayList<String> diagnosticsList) {
        this.diagnosticsList = diagnosticsList;
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
