package com.webfarms.treatmyfish.bean;

import java.io.Serializable;

public class BeanImageUrls implements Serializable {

    private String imageId;
    private String subCatId;
    private String fileName;
    private String imageUrl;
    private String imageType;
    private String imgPicType;
    private String imgDloadSts;

    public BeanImageUrls(String imageId, String subCatId, String fileName,
                         String imageUrl, String imageType, String imgPicType, String imgDloadSts) {
        this.imageId = imageId;
        this.subCatId = subCatId;
        this.fileName = fileName;
        this.imageUrl = imageUrl;
        this.imageType = imageType;
        this.imgPicType = imgPicType;
        this.imgDloadSts = imgDloadSts;
    }


    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getSubCatId() {
        return subCatId;
    }

    public void setSubCatId(String subCatId) {
        this.subCatId = subCatId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public String getImgPicType() {
        return imgPicType;
    }

    public void setImgPicType(String imgPicType) {
        this.imgPicType = imgPicType;
    }

    public String getImgDloadSts() {
        return imgDloadSts;
    }

    public void setImgDloadSts(String imgDloadSts) {
        this.imgDloadSts = imgDloadSts;
    }
}
