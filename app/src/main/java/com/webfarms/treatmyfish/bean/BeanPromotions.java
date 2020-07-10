package com.webfarms.treatmyfish.bean;

/**
 * Created by Ashish Zade on 3/16/2017 & 5:28 PM.
 */

public class BeanPromotions {


    /**
     * offerId : 1
     * title : Dell
     * description : Dell Model i5 1TB 4RAM
     * offerDate : 2017-03-10 17:45:35.0
     * image : null
     * rate : 1000 Rs Off
     */

    private int offerId;
    private String title;
    private String description;
    private String offerDate;
    private Object image;
    private String rate;

    public int getOfferId() {
        return offerId;
    }

    public void setOfferId(int offerId) {
        this.offerId = offerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOfferDate() {
        return offerDate;
    }

    public void setOfferDate(String offerDate) {
        this.offerDate = offerDate;
    }

    public Object getImage() {
        return image;
    }

    public void setImage(Object image) {
        this.image = image;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }
}
