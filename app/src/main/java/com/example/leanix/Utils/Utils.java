package com.example.leanix.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Utils {
    // CHECK NETWORK DEFAULTS
    public static final int NO_INTERNET_CONNECTIVITY = 0;
    public static final int THERES_INTERNET_CONNECTIVITY = 1;

    // SORTING DEFAULTS
    public static final int SORT_BY_MISSION_NAME_LAUNCH_LIST = 111;
    public static final int SORT_BY_LAUNCH_DATE_LAUNCH_LIST = 222;

    public static final int SORT_BY_MISSION_NAME_BOOKMARK_LIST = 333;
    public static final int SORT_BY_LAUNCH_DATE_BOOKMARK_LIST = 444;

    // SHARED PREFERENCE DEFAULTS
    private static final String PREFERENCE_FILE_NAME = "Launch list preference";
    public static final String GRID_VIEW_VALUE = "GridView";
    public static final String LIST_VIEW_VALUE = "ListView";
    private static final String LIST_VIEW_KEY = "which List View?";

    //ROOM DATABASE DEFAULTS
    public static final int BOOKMARK_KEY = 101;
    public static final int LAUNCH_SUCCESS = 200;
    public static final int LAUNCH_FAIL = 100;

    //SERVER ERROR
    public static final String ERROR_FETCHING_API = "Error";

    public static int getConnectivityStatusString(Context context) {
        int status = -1;
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            if (activeNetwork.getType() != ConnectivityManager.TYPE_WIFI ||
                    activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                status = THERES_INTERNET_CONNECTIVITY;
            }

        } else {
            status = NO_INTERNET_CONNECTIVITY;

        }
        return status;
    }

    public static void setListViewOption(Context context, String value) {
        SharedPreferences sharedPref = context
                .getSharedPreferences(PREFERENCE_FILE_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(LIST_VIEW_KEY,value);
        editor.apply();
    }

    public static String getListViewOption(Context context) {
        SharedPreferences sharedPref = context
                .getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        return sharedPref.getString(LIST_VIEW_KEY,LIST_VIEW_VALUE);
    }

}
