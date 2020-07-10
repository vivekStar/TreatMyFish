package com.webfarms.treatmyfish.bean;

import java.io.Serializable;

public class BeanImageStrings implements Serializable{

    private String imgID;
    private String imgSubCatID;
    private String imageType;
    private String imgString;


    public BeanImageStrings(String imgID, String imgSubCatID, String imageType, String imgString) {
        this.imgID = imgID;
        this.imgSubCatID = imgSubCatID;
        this.imageType = imageType;
        this.imgString = imgString;
    }

    public String getImgID() {
        return imgID;
    }

    public void setImgID(String imgID) {
        this.imgID = imgID;
    }

    public String getImgSubCatID() {
        return imgSubCatID;
    }

    public void setImgSubCatID(String imgSubCatID) {
        this.imgSubCatID = imgSubCatID;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public String getImgString() {
        return imgString;
    }

    public void setImgString(String imgString) {
        this.imgString = imgString;
    }
}
