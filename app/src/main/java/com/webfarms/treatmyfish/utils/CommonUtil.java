package com.webfarms.treatmyfish.utils;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class CommonUtil {

    /**
     * This method device Full name into FirstName,middleName, lastName
     *
     * @param name - Full name Example Ashish Arun Zade
     * @return String array Exa arr[0] = "Ashish" arr[1] = "Arun" and arr[2]
     * = "Zade"
     */

    public static String[] getDivideFullName(String name) {
        name = name.trim();
        int index = name.lastIndexOf(' ');
        String fname = "", lname = "", mname = "";
        if (index > 0) {
            fname = name.substring(0, index);
            lname = name.substring(index + 1, name.length());
        }

        name = fname.trim();
        index = name.indexOf(' ');
        if (index > 0) {
            fname = name.substring(0, index);
            mname = name.substring(index + 1, name.length());
        }

        return new String[]{fname, lname, mname};
    }

    /**
     * This method converts Date String from different format to dd/MM/yyy
     *
     * @param dateStr   : this is date String which user want to convert
     * @param formatStr : this is format of that dateStr Ex: dd-MMM-yyyy hh:mm:ss
     * @return it returns
     */
    public static String convertToDateString(String dateStr, String formatStr) {
        SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
        SimpleDateFormat sdfTo = new SimpleDateFormat("dd-MMM-yyyy");

        String newDateStr = null;
        Date date;
        try {
            if (dateStr == null) {
                date = new Date(System.currentTimeMillis());
            } else {
                date = sdf.parse(dateStr);
            }
            newDateStr = sdfTo.format(date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            printError(e);
        }

        return newDateStr;
    }

    /**
     * This method converts Date String from different format to dd/MM/yyy
     *
     * @param dateStr   : this is date String which user want to convert
     * @param formatStr : this is format of that dateStr Ex: dd-MMM-yyyy hh:mm:ss
     * @return it returns
     */
    public static String convertToDBDate(String dateStr, String formatStr) {
        SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
        SimpleDateFormat sdfTo = new SimpleDateFormat("yyyy-MM-dd");

        String newDateStr = null;
        Date date;
        try {
            if (dateStr == null) {
                date = new Date(System.currentTimeMillis());
            } else {
                date = sdf.parse(dateStr);
            }
            newDateStr = sdfTo.format(date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            printError(e);
        }

        return newDateStr;
    }

    /**
     * This method converts Date String from different format to dd/MM/yyy
     *
     * @param dateStr   : this is date String which user want to convert
     * @param formatStr : this is format of that dateStr Ex: dd-MMM-yyyy hh:mm:ss
     * @return it returns
     */
    public static String convertStringDateFormat(String dateStr, String formatStr, String toFormatStr) {
        SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
        SimpleDateFormat sdfTo = new SimpleDateFormat(toFormatStr);

        String newDateStr = null;
        Date date;
        try {
            if (dateStr == null) {
                date = new Date(System.currentTimeMillis());
            } else {
                date = sdf.parse(dateStr);
            }
            newDateStr = sdfTo.format(date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            printError(e);
        }

        return newDateStr;
    }

    /**
     * Return String as per given format
     *
     * @param date      Date object
     * @param formatStr return format dateStr Ex: dd-MMM-yyyy hh:mm:ss
     * @return String date format
     */
    public static String formatDate(Date date, String formatStr) {
        SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
        String newDateStr = sdf.format(date);
        return newDateStr;
    }

    public static String getFileNameFromURL(String url) {
        String fileName = url.substring(url.lastIndexOf('/') + 1, url.length());
        return fileName;
    }

    public static String getFormatURL(String url) {
        if (url != null) {
            url = url.replaceAll(" ", "%20");
        } else {
            return "";
        }
        return url;
    }

    public static void printError(Exception e) {
        e.printStackTrace();
    }

    public static void printMessage(String msg) {
        if (msg != null) {
        }
    }

    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    /**
     * This method shows custom toast which is showing some UI
     *
     * @param context
     * is Context of activity
     * @param msg
     * : message to show
     * @param isError
     * : set 'true' when you want to show error, set 'false' when
     * want to show information
     */


    private static Handler h;
    public static boolean toastFlag;

    private static Runnable thread = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            toastFlag = false;
        }
    };

    public static boolean isNetworkAvailable(Context con) {

        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) con
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected;
        if (activeNetwork != null)
            isConnected = activeNetwork.isConnectedOrConnecting();
        else
            isConnected = false;

        return haveConnectedWifi || haveConnectedMobile || isConnected;
    }

    public static String getSharePreferenceString(Context context, String key,
                                                  String defaultValue) {
        SharedPreferences setting = PreferenceManager
                .getDefaultSharedPreferences(context);

        return setting.getString(key, defaultValue);
    }

    public static void setSharePreferenceString(Context context, String key,
                                                String value) {
        SharedPreferences setting = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = setting.edit();

        if (value.equals("")) {
            editor.remove(key);
        } else {
            editor.putString(key, value);
        }
        editor.commit();
    }

    public static String getIMEI(Context context) {

        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String imei = null;
        if (tm != null) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null;
            }
            imei = tm.getDeviceId();
            if (imei == null || imei.length() == 0)
                imei = Build.SERIAL;
        }
        return imei;
        // return "359299057653516";
    }

    public static void setLocaleHindi(Context context) {
        Locale locale1 = new Locale("hi");
        Locale.setDefault(locale1);
        Configuration config = new Configuration();
        config.locale = locale1;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }

    public static void setLocaleEnglish(Context context) {

        Locale locale = new Locale("en");
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        context.getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());
    }

    public static void setLocaleOriya(Context context) {
        Locale locale1 = new Locale("or");
        Locale.setDefault(locale1);
        Configuration config = new Configuration();
        config.locale = locale1;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }


}
