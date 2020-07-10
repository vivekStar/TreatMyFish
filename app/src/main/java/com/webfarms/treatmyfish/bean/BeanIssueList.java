package com.webfarms.treatmyfish.bean;

import android.content.ContentValues;

import com.webfarms.treatmyfish.database.TableIssueList;

import java.io.Serializable;

/**
 * Created by Ashish Zade on 2/17/2017 & 11:35 AM.
 */

public class BeanIssueList implements Serializable {


    /**
     * issueId : 8
     * issueName : Always Restarts
     * active : Y
     * createdOn : 2017-02-16 18:58:20.0
     */

    private int issueId;
    private String issueName;
    private String active;
    private String createdOn;

    public int getIssueId() {
        return issueId;
    }

    public void setIssueId(int issueId) {
        this.issueId = issueId;
    }

    public String getIssueName() {
        return issueName;
    }

    public void setIssueName(String issueName) {
        this.issueName = issueName;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public ContentValues getContentValues() {
        ContentValues cv = new ContentValues();

        cv.put(TableIssueList.DataIssueList.ISSUE_KEY_issueId, getIssueId());
        cv.put(TableIssueList.DataIssueList.ISSUE_KEY_issueName, getIssueName());
        cv.put(TableIssueList.DataIssueList.ISSUE_KEY_active, getActive());
        cv.put(TableIssueList.DataIssueList.ISSUE_KEY_createdOn, getCreatedOn());

        return cv;
    }

    public void setContentValue(ContentValues cv) {
        setIssueId(cv.getAsInteger(TableIssueList.DataIssueList.ISSUE_KEY_issueId));
        setIssueName(cv.getAsString(TableIssueList.DataIssueList.ISSUE_KEY_issueName));
        setActive(cv.getAsString(TableIssueList.DataIssueList.ISSUE_KEY_active));
        setCreatedOn(cv.getAsString(TableIssueList.DataIssueList.ISSUE_KEY_createdOn));
    }

}
