package com.webfarms.treatmyfish.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;


public class DatabaseHandler extends SQLiteDatabaseHandler {


    public static final String DATABASE_NAME = "database.db";
    public static final int DATABASE_VERSION = 1;

    public DatabaseHandler(Context c) {
        super(c, DATABASE_NAME, DATABASE_VERSION);
        // TODO Auto-generated constructor stub

    }

    @Override
    public void createTable(SQLiteDatabase db) {
        // TODO Auto-generated method stub

        // added inside DB version 1
        db.execSQL(TableIssueList.SQL_ISSUE);
        //	db.execSQL(TableCategory.SQL_CAT);

    }

    @Override
    public void updateDatabase(Context ourContext, SQLiteDatabase db,
                               int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        // db.execSQL("DROP TABLE IF IT EXISTS " + DATABASE_NAME);
        // createTable(db);
        switch (oldVersion) {

        }
    }

    public void initDatabase() {
        open();
        close();
    }
}
