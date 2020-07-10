package com.webfarms.treatmyfish.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;
import com.webfarms.treatmyfish.bean.BeanDiseasesHelper;
import com.webfarms.treatmyfish.bean.BeanImageStrings;
import com.webfarms.treatmyfish.bean.BeanImageUrls;

import java.util.ArrayList;

/**
 * Created by WEBFARMSPC2 on 3/23/2018.
 */

public class DatabaseHelper extends SQLiteAssetHelper {

    int count = 1;

    SQLiteDatabase database;

    private Context context;

    private static final String TABLE_DISEASES = "_tbl_diseases";
    private static final String TABLE_DESC = "_tbl_description";
    private static final String TABLE_CLINICAL_SIGNS = "_tbl_clinical_signs";
    private static final String TABLE_DIAGNOSTICS = "_tbL_diagnostics";

    private static final String TABLE_IMG_URLS = "tblImageUrls";//imgID,imgSubCatID,imgFileNm,imgUrl,imgType,imgPicType,imgDloadSts
    private static final String TABLE_IMG_COUNTER = "tblImgCounter";//counter,imgCount,date
    private static final String TABLE_IMAGES = "tblImages";//imgID,imgSubCatID,imgPicType,imgString
    private static final String TABLE_IMG_DESC = "tblImgDesc";//descImgID,subCatID,URL,status,fileName

    /*CREATE TABLE `tblImageToDisplay` ( `imgID` INTEGER NOT NULL, `imgSubCatID` INTEGER NOT NULL, `imgBlob` BLOB NOT NULL, `imgString` TEXT NOT NULL )*/
    /*CREATE TABLE "tblImgCounter" ( `counter` INTEGER NOT NULL, `imgCount` INTEGER NOT NULL, `date` TEXT NOT NULL )*/
    /*CREATE TABLE `tblImgDesc` ( `descImgID` INTEGER NOT NULL, `subCatID` INTEGER NOT NULL, `URL` TEXT NOT NULL, `status` TEXT NOT NULL, `fileName` TEXT NOT NULL, PRIMARY KEY(`descImgID`) )*/


    private static final String TABLE_STATE = "_tbl_state";// columns     STATE_ID   STATE
    private static final String TABLE_DISTRICT = "_tbl_district";// columns     DISTRICT_ID     DISTRICT      STATE_ID
    private static final String TABLE_CITY = "_tbl_city";// columns     CITY_ID     CITY      DISTRICT_ID


    private static final String DB_NAME = "diseases.db";

    private static final String DB_IMAGE = "cifa.db";

    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {

        super(context, DB_NAME, null, DATABASE_VERSION);

    }

    public ArrayList<String> getTopicsList(String category) {

        SQLiteDatabase database = getReadableDatabase();

        ArrayList<String> topicsList = new ArrayList<>();

        String columns[] = {"CATEGORY", "TOPICS"};

        Cursor cursor = database.query(TABLE_DISEASES, columns, "CATEGORY=?", new String[]{category}, null, null, null);

        int count = cursor.getCount();

        if (count > 0) {

            cursor.moveToFirst();
            do {
                String cat = cursor.getString(0);
                String topic = cursor.getString(1);

                topicsList.add(topic);
            } while (cursor.moveToNext());


            return topicsList;
        } else {
            return null;
        }
    }


    public ArrayList<BeanDiseasesHelper> getDiseaseDetails(String topic) {

        SQLiteDatabase database = getReadableDatabase();

        ArrayList<BeanDiseasesHelper> diseasesBeanList = new ArrayList<>();

        String columns[] = {"TOPIC", "SUGESTED_TREAT", "MGMT"};

        Cursor cursor = database.query(TABLE_DESC, columns, "TOPIC=?", new String[]{topic}, null, null, null);

        int count = cursor.getCount();

        if (count > 0) {

            cursor.moveToFirst();
            do {

                String tp = cursor.getString(0);
                String treatment = cursor.getString(1);
                String mgmt = cursor.getString(2);

                diseasesBeanList.add(new BeanDiseasesHelper(treatment, mgmt));

            } while (cursor.moveToNext());

            return diseasesBeanList;
        } else {
            return null;
        }
    }

    public ArrayList<String> getClinicalSigns(String topic) {

        SQLiteDatabase database = getReadableDatabase();

        ArrayList<String> clinicalSignsList = new ArrayList<>();

        String columns[] = {"DISEAES", "SIGNS"};

        Cursor cursor = database.query(TABLE_CLINICAL_SIGNS, columns, "DISEAES=?", new String[]{topic}, null, null, null);

        int count = cursor.getCount();

        if (count > 0) {

            cursor.moveToFirst();
            do {

                String diseases = cursor.getString(0);
                String signs = cursor.getString(1);

                clinicalSignsList.add(signs);
            } while (cursor.moveToNext());

            return clinicalSignsList;
        } else {
            return null;
        }
    }

    public ArrayList<String> getDiagnosticsList(String category) {


        SQLiteDatabase database = getReadableDatabase();

        ArrayList<String> diagnostList = new ArrayList<>();

        String columns[] = {"DISEASES", "DIAGNOSTICS"};

        Cursor cursor = database.query(TABLE_DIAGNOSTICS, columns, "DISEASES=?", new String[]{category}, null, null, null);

        int count = cursor.getCount();

        if (count > 0) {

            cursor.moveToFirst();
            do {

                String dis = cursor.getString(0);
                String diagn = cursor.getString(1);

                diagnostList.add(diagn);

            } while (cursor.moveToNext());

            return diagnostList;
        } else {
            return null;
        }
    }

    public boolean addImageUrlToDB(BeanImageUrls img) {


        database = getWritableDatabase();

        ContentValues values = new ContentValues();
        //columns   itemId,itemName
        values.put("imgID", img.getImageId());
        values.put("imgSubCatID", img.getSubCatId());
        values.put("imgFileNm", img.getFileName());
        values.put("imgUrl", img.getImageUrl());
        values.put("imgType", "new");
        values.put("imgPicType", img.getImgPicType());
        values.put("imgDloadSts", img.getImgDloadSts());


        long count = database.insert(TABLE_IMG_URLS, null, values);


        if (count > 1) {
            return true;
        } else {
            return false;
        }
    }

    public ArrayList<String> getImageUrls(String imgSubCatID) {


        SQLiteDatabase database = getReadableDatabase();
        String columns[] = {"imgSubCatID", "imgUrl"};
        Cursor cursor = database.query(TABLE_IMG_URLS, columns, "imgSubCatID=?", new String[]{imgSubCatID}, null, null, null);

        String imgSCId = null, imgToDis = null;

        ArrayList<String> imagesArrList = null;
        int count = cursor.getCount();


        if (count > 0) {

            imagesArrList = new ArrayList<>(count);
            cursor.moveToFirst();
            do {
                imgSCId = cursor.getString(0);
                imgToDis = cursor.getString(1);
                imagesArrList.add(imgToDis);
            } while (cursor.moveToNext());

            return imagesArrList;
        } else {
            return imagesArrList;
        }
    }

    public ArrayList<BeanImageUrls> getAllImageUrls() {


        SQLiteDatabase database = getReadableDatabase();
        String columns[] = {"imgID", "imgSubCatID", "imgFileNm", "imgUrl", "imgType", "imgPicType", "imgDloadSts"};
        Cursor cursor = database.query(TABLE_IMG_URLS, columns, null, null, null, null, null);

        String imgID = null, imgSubCatID = null, imgFileNm = null, imgUrl = null, imgType = null, imgPicType = null, imgDloadSts = null;

        ArrayList<BeanImageUrls> imagesArrList = null;
        int count = cursor.getCount();
        imagesArrList = new ArrayList<>();


        if (count > 0) {

            cursor.moveToFirst();
            do {
                imgID = cursor.getString(0);
                imgSubCatID = cursor.getString(1);
                imgFileNm = cursor.getString(2);
                imgUrl = cursor.getString(3);

                imgType = cursor.getString(4);
                imgPicType = cursor.getString(5);
                imgDloadSts = cursor.getString(6);
                BeanImageUrls imageUrls = new BeanImageUrls(imgID, imgSubCatID, imgFileNm, imgUrl, imgType, imgPicType, imgDloadSts);
                imagesArrList.add(imageUrls);
            } while (cursor.moveToNext());


            return imagesArrList;
        } else {
            return imagesArrList;
        }

    }

    public boolean insertImageCounter(String counter, String date) {

        /*CREATE TABLE "tblImgCounter" ( `counter` INTEGER NOT NULL, `imgCount` INTEGER NOT NULL, `date` TEXT NOT NULL )*/

        database = getWritableDatabase();

        ContentValues values = new ContentValues();
        //columns   itemId,itemName
        values.put("counter", counter);
        values.put("imgCount", counter);
        values.put("date", date);

        long count = database.insert(TABLE_IMG_COUNTER, null, values);

        if (count > 1) {
            return true;
        } else {
            return false;
        }
    }

    public String getImageCounter() {

        SQLiteDatabase database = getReadableDatabase();
        String columns[] = {"counter"};
        Cursor cursor = database.query(TABLE_IMG_COUNTER, columns, null, null, null, null, null);

        String counter = null;

        int count = cursor.getCount();

        if (count > 0) {
            cursor.moveToFirst();
            do {
                counter = cursor.getString(0);
            } while (cursor.moveToNext());
            return counter;
        } else {
            return counter;
        }

    }

    public boolean updateImageCounter(String counter) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("counter", counter);
//        String whereClause = "tofrom=? AND amount=? AND date=? AND type=?";
//        String whereArgs[] = {from, amount, date, type};
        int cnt = db.update(TABLE_IMG_COUNTER, contentValues, null, null);

        if (cnt > 0) {
            return true;
        } else {
            return false;
        }

    }

    public boolean addImageStringToDB(BeanImageStrings img) {


        database = getWritableDatabase();

        ContentValues values = new ContentValues();
        //columns   itemId,itemName
        values.put("imgID", img.getImgID());
        values.put("imgSubCatID", img.getImgSubCatID());
        values.put("imgString", img.getImgString());
        values.put("imgPicType", img.getImageType());

        long count = database.insert(TABLE_IMAGES, null, values);


        if (count > 1) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("imgType", "old");
            contentValues.put("imgDloadSts", "YES");
            String whereClause = "imgID=?";
            String whereArgs[] = {img.getImgID()};
            int cnt = database.update(TABLE_IMG_URLS, contentValues, whereClause, whereArgs);

            return true;
        } else {
            return false;
        }
    }


    public void printImageUrls() {

        SQLiteDatabase database = getReadableDatabase();
        String columns[] = {"imgID", "imgSubCatID", "imgString", "imgPicType"};
        Cursor cursor = database.query(TABLE_IMAGES, columns, null, null, null, null, null);

        String imgID = null, imgSubCatID = null, imgString = null, imgPicType = null;
        int count = cursor.getCount();


        if (count > 0) {

            cursor.moveToFirst();
            do {
                imgID = cursor.getString(0);
                imgSubCatID = cursor.getString(1);
                imgString = cursor.getString(2);
                imgPicType = cursor.getString(3);

            } while (cursor.moveToNext());


        }

    }

    public ArrayList<BeanImageStrings> getImagesString(String subCatId) {

        SQLiteDatabase database = getReadableDatabase();
        String columns[] = {"imgID", "imgSubCatID", "imgString", "imgPicType"};
        Cursor cursor = database.query(TABLE_IMAGES, columns, "imgSubCatID=?", new String[]{subCatId}, null, null, null);

        String imgID = null, imgSubCatID = null, imgString = null, imgPicType = null;

        int count = cursor.getCount();

        ArrayList<BeanImageStrings> arrList = new ArrayList<>();

        if (count > 0) {
            cursor.moveToFirst();
            do {
                imgID = cursor.getString(0);
                imgSubCatID = cursor.getString(1);
                imgString = cursor.getString(2);
                imgPicType = cursor.getString(3);
                BeanImageStrings objImgStr = new BeanImageStrings(imgID, imgSubCatID, imgPicType, imgString);
                arrList.add(objImgStr);
            } while (cursor.moveToNext());


            return arrList;
        } else {
            return arrList;
        }

    }

    public ArrayList<String> getOnlyImagesString(String subCatId, String imgPicType) {

        printImageUrls();

        SQLiteDatabase database = getReadableDatabase();
        String columns[] = {"imgString"};
        Cursor cursor = database.query(TABLE_IMAGES, columns, "imgSubCatID=? AND imgPicType=?", new String[]{subCatId, imgPicType}, null, null, null);

        String imgID = null, imgSubCatID = null, imgString = null;

        int count = cursor.getCount();

        ArrayList<String> arrList = new ArrayList<>();

        if (count > 0) {
            cursor.moveToFirst();
            do {
                imgString = cursor.getString(0);
                arrList.add(imgString);
            } while (cursor.moveToNext());


            return arrList;
        } else {
            return arrList;
        }

    }

    public boolean deleteSelImages(String imageId) {

        int cnt = 0, cnt1 = 0;

        SQLiteDatabase db = getReadableDatabase();

        String whereClause = "imgID=?";
        String whereArgs[] = {imageId};
        cnt = db.delete(TABLE_IMAGES, whereClause, whereArgs);
        cnt1 = db.delete(TABLE_IMG_URLS, whereClause, whereArgs);


        int count = cnt + cnt1;
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean deleteImagesData() {

        int cnt = 0, cnt1 = 0;

        SQLiteDatabase db = getReadableDatabase();


        cnt = db.delete(TABLE_IMAGES, null, null);
        cnt1 = db.delete(TABLE_IMG_URLS, null, null);

        int count = cnt + cnt1;
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

}
