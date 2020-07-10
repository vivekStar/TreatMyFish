package com.webfarms.treatmyfish.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.provider.BaseColumns;

import com.webfarms.treatmyfish.bean.BeanIssueList;

import java.util.LinkedList;


/**
 * Created by Ashish Zade on 2/17/2017 & 11:26 AM.
 */

public class TableIssueList {

    Context c;

    public TableIssueList(Context c) {
        this.c = c;
    }


    public static abstract class DataIssueList implements BaseColumns {

        public static final String TABLE = "IssueList";

        public static final String ISSUE_KEY_issueId = "issueId";
        public static final String ISSUE_KEY_issueName = "issueName";
        public static final String ISSUE_KEY_active = "active";
        public static final String ISSUE_KEY_createdOn = "createdOn";


        public static final String[] projectionString = new String[]{
                ISSUE_KEY_issueId, ISSUE_KEY_issueName, ISSUE_KEY_active, ISSUE_KEY_createdOn};
    }

    public static final String SQL_ISSUE = "CREATE TABLE 'IssueList' ( 'issueId' INTEGER,'issueName' TEXT, 'active' TEXT,'createdOn' TEXT,PRIMARY KEY(issueId));";


    public void insert(String query) {
        DatabaseHandler db = new DatabaseHandler(c);
        try {
            db.execSQL(query);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void insert(BeanIssueList bean) {
        DatabaseHandler db = new DatabaseHandler(c);
        db.insert(DataIssueList.TABLE, bean.getContentValues());
    }

    public void insertAll(LinkedList<BeanIssueList> list) {
        for (int i = 0; i < list.size(); i++) {
            insert(list.get(i));
        }
    }

    public LinkedList<ContentValues> selectAll() {
        String sql = "select * from IssueList";
        DatabaseHandler db = new DatabaseHandler(c);
        return db.select(sql);
    }

    public LinkedList<ContentValues> selectIssue() {
        String sql = "select * from IssueList";
        DatabaseHandler db = new DatabaseHandler(c);
        return db.select(sql);
    }

    public void deleteAllCategory() {
        DatabaseHandler db = new DatabaseHandler(c);
        db.deleteAll(DataIssueList.TABLE);
    }


}
