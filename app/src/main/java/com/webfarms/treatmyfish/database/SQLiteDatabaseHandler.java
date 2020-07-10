package com.webfarms.treatmyfish.database;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;


import com.webfarms.treatmyfish.utils.CommonUtil;

import java.io.File;
import java.util.LinkedList;


/**
 * This Class has all the basic implementation of Android Database now you just
 * have to extend this class (extend SQLiteDatabaseHandler) and Must define an
 * explicit constructor SQLiteDatabaseHandler() and define tow methods
 * createTable() and updateDatabase();
 *
 * @author Abhijeet
 */

public abstract class SQLiteDatabaseHandler {

    private class DbHelper extends SQLiteOpenHelper {

        public DbHelper(Context context) {
            super(context, databaseName, null, databaseVersion);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // TODO Auto-generated method stub

            // SQLiteDatabase.loadLibs(context);
            File databaseFile = ourContext.getDatabasePath(databaseName);
            databaseFile.mkdirs();
            databaseFile.delete();
            db = SQLiteDatabase.openOrCreateDatabase(databaseFile, null);
            // database = SQLiteDatabase.openOrCreateDatabase(databaseFile,
            // "android#123", null);
            db.setVersion(databaseVersion);
            createTable(db);
            db.close();

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO Auto-generated method stub
            updateDatabase(ourContext, db, oldVersion, newVersion);
        }
    }

    protected DbHelper ourHelper;
    protected final Context ourContext;
    protected SQLiteDatabase ourDatabase;
    private String databaseName;
    private int databaseVersion;

    public SQLiteDatabaseHandler(Context c, String databaseName,
                                 int databaseVersion) {
        ourContext = c;
        this.databaseName = databaseName;
        this.databaseVersion = databaseVersion;
    }

    final protected void close() {
        ourHelper.close();
    }

    /**
     * This is abstract method, This method is used for creating Tables into the
     * database. use - db.execSQL("SQL query"); method to create new table into
     * DB
     *
     * @param db
     */
    public abstract void createTable(SQLiteDatabase db);

    // inserting single bean
    public long[] insertAll(String tabalName, LinkedList<ContentValues> cv) {
        open();
        long ids[] = new long[cv.size()];
        for (int i = 0; i < cv.size(); i++) {
            try {
                ids[i] = ourDatabase.insert(tabalName, null, cv.get(i));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        close();
        return ids;
    }

    public long insert(String tabalName, ContentValues cv) {
        long id = 0;
        open();
        try {
            id = ourDatabase.insert(tabalName, null, cv);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        close();
        return id;
    }

    /**
     * This method Execute a single SQL statement Insert, Update or Delete
     *
     * @param query
     */
    public void execSQL(String query) throws SQLException {
        open();
        ourDatabase.execSQL(query);
        close();
    }

    /**
     * Count all rows from Table
     *
     * @param table
     * @param primaryKey
     * @return count value
     */
    public int countRows(String table, String primaryKey) {
        String sql = "select count( " + primaryKey + " ) as total from "
                + primaryKey;
        open();
        Cursor c = ourDatabase.rawQuery(sql, null);
        int total = 0;
        try {
            int iCId = c.getColumnIndex("total");
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                try {
                    total = c.getInt(iCId);
                } catch (Exception e) {
                    total = 0;
                }
            }

        } catch (Exception e) {
            CommonUtil.printError(e);
            total = 0;
        } finally {
            c.close();
            close();
        }
        return total;
    }

    final protected SQLiteDatabaseHandler open() throws SQLException {
        // SQLiteDatabase.loadLibs(ourContext);
        try {
            ourHelper = new DbHelper(ourContext);
            ourDatabase = ourHelper.getWritableDatabase();
        } catch (Exception e) {
            CommonUtil.printError(e);
        }
        return this;
    }

    // inserting single bean
    public long[] replaceAll(String tabalName, LinkedList<ContentValues> cv) {
        open();
        long ids[] = new long[cv.size()];
        for (int i = 0; i < cv.size(); i++) {
            try {
                ids[i] = ourDatabase.replace(tabalName, null, cv.get(i));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        close();
        return ids;
    }

    // inserting single bean
    public long replace(String tabalName, ContentValues cv) {
        open();
        long ids = 0;
        try {
            ids = ourDatabase.replace(tabalName, null, cv);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        close();
        return ids;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public LinkedList<ContentValues> select(String sqlQuery) {
        LinkedList<ContentValues> cvList = new LinkedList<ContentValues>();
        Cursor c = null;
        open();
        try {
            c = ourDatabase.rawQuery(sqlQuery, null);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            CommonUtil.printError(e);
            return cvList;
        }

        if (c == null) {
            return cvList;
        }

        String columnName[] = c.getColumnNames();

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            ContentValues cv = new ContentValues();

            for (String clm : columnName) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    int type = c.getType(c.getColumnIndex(clm));
                    switch (type) {
                        case Cursor.FIELD_TYPE_FLOAT:
                            cv.put(clm, c.getDouble(c.getColumnIndex(clm)));
                            break;
                        case Cursor.FIELD_TYPE_INTEGER:
                            cv.put(clm, c.getLong(c.getColumnIndex(clm)));
                            break;
                        case Cursor.FIELD_TYPE_STRING:
                            cv.put(clm, c.getString(c.getColumnIndex(clm)));
                            break;
                    }
                } else {
                    cv.put(clm, c.getString(c.getColumnIndex(clm)));
                }
            }
            cvList.add(cv);
        }

        c.close();
        close();

        return cvList;
    }

    /**
     * Query the given table, returning a List of AnswerBean
     *
     * @param table         The table name to compile the query against.
     * @param column        A list of which columns to return. Passing null will return
     *                      all columns, which is discouraged to prevent reading data from
     *                      storage that isn't going to be used.
     * @param selection     A filter declaring which rows to return, formatted as an SQL
     *                      WHERE clause (excluding the WHERE itself). Passing null will
     *                      return all rows for the given table.
     * @param selectionArgs You may include ?s in selection, which will be replaced by the
     *                      values from selectionArgs, in order that they appear in the
     *                      selection. The values will be bound as Strings.
     * @param groupBy       A filter declaring how to group rows, formatted as an SQL
     *                      GROUP BY clause (excluding the GROUP BY itself). Passing null
     *                      will cause the rows to not be grouped.
     * @param having        A filter declare which row groups to include in the cursor, if
     *                      row grouping is being used, formatted as an SQL HAVING clause
     *                      (excluding the HAVING itself). Passing null will cause all row
     *                      groups to be included, and is required when row grouping is
     *                      not being used.
     * @param orderBy       How to order the rows, formatted as an SQL ORDER BY clause
     *                      (excluding the ORDER BY itself). Passing null will use the
     *                      default sort order, which may be unordered.
     * @return the LinkedList value
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public LinkedList<ContentValues> select(String table, String[] column,
                                            String selection, String[] selectionArgs, String groupBy,
                                            String having, String orderBy) {
        LinkedList<ContentValues> cvList = new LinkedList<ContentValues>();
        Cursor c = null;
        open();
        try {
            c = ourDatabase.query(table, column, selection == null ? null
                            : selection, selectionArgs == null ? null : selectionArgs,
                    groupBy == null ? null : groupBy, having == null ? null
                            : having, orderBy == null ? null : orderBy);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            CommonUtil.printError(e);
            return cvList;
        }

        if (c == null) {
            return cvList;
        }

        String columnName[] = c.getColumnNames();

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            ContentValues cv = new ContentValues();

            for (String clm : columnName) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    int type = c.getType(c.getColumnIndex(clm));
                    switch (type) {
                        case Cursor.FIELD_TYPE_FLOAT:
                            cv.put(clm, c.getDouble(c.getColumnIndex(clm)));
                            break;
                        case Cursor.FIELD_TYPE_INTEGER:
                            cv.put(clm, c.getLong(c.getColumnIndex(clm)));
                            break;
                        case Cursor.FIELD_TYPE_STRING:
                            cv.put(clm, c.getString(c.getColumnIndex(clm)));
                            break;
                    }
                } else {
                    cv.put(clm, c.getString(c.getColumnIndex(clm)));
                }
            }
            cvList.add(cv);
        }

        c.close();
        close();

        return cvList;
    }

    public int deleteAll(String table) {
        open();
        int i = ourDatabase.delete(table, null, null);
        close();
        return i;
    }

    public abstract void updateDatabase(Context ourContext, SQLiteDatabase db,
                                        int oldVersion, int newVersion);

}
