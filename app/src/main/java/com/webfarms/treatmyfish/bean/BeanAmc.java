package com.webfarms.treatmyfish.bean;

/**
 * Created by Ashish Zade on 3/15/2017 & 2:42 PM.
 */

public class BeanAmc {

    /**
     * machineConfigId : 3
     * motherboard : xyz
     * ram : 8GB
     * hdd : 1TB
     * monitor : LED
     * dvd : abc
     * keyboard : aaa
     * graphicscard : aaa
     * amcId : 2
     */

    private int machineConfigId;
    private String motherboard;
    private String ram;
    private String hdd;
    private String monitor;
    private String dvd;
    private String keyboard;
    private String graphicscard;
    private int amcId;

    public int getMachineConfigId() {
        return machineConfigId;
    }

    public void setMachineConfigId(int machineConfigId) {
        this.machineConfigId = machineConfigId;
    }

    public String getMotherboard() {
        return motherboard;
    }

    public void setMotherboard(String motherboard) {
        this.motherboard = motherboard;
    }

    public String getRam() {
        return ram;
    }

    public void setRam(String ram) {
        this.ram = ram;
    }

    public String getHdd() {
        return hdd;
    }

    public void setHdd(String hdd) {
        this.hdd = hdd;
    }

    public String getMonitor() {
        return monitor;
    }

    public void setMonitor(String monitor) {
        this.monitor = monitor;
    }

    public String getDvd() {
        return dvd;
    }

    public void setDvd(String dvd) {
        this.dvd = dvd;
    }

    public String getKeyboard() {
        return keyboard;
    }

    public void setKeyboard(String keyboard) {
        this.keyboard = keyboard;
    }

    public String getGraphicscard() {
        return graphicscard;
    }

    public void setGraphicscard(String graphicscard) {
        this.graphicscard = graphicscard;
    }

    public int getAmcId() {
        return amcId;
    }

    public void setAmcId(int amcId) {
        this.amcId = amcId;
    }
}
