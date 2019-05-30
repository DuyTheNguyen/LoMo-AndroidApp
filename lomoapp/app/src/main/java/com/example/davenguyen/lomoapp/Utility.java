package com.example.davenguyen.lomoapp;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.widget.Toast;

import java.text.ParseException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Created by Dave Nguyen on 09-Oct-17.
 * This class is used for storing general function.
 */

public class Utility {
    //For making toast
    public static void MakeLongToastToast(Context ct, String message) {
        Toast.makeText(ct, message, Toast.LENGTH_LONG).show();
    }

    //Calculate the number of coloumns
    public static int calculateNoOfColumns(Context context, int widthofImage) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / widthofImage);
        return noOfColumns;
    }

    //Convert milisecond to date
    public static String getDate(long milliSeconds) {
        String dateString;
        SimpleDateFormat formatter = new SimpleDateFormat("kk:mm - dd/MM/yyyy");
        formatter.setTimeZone(TimeZone.getTimeZone("Australia/Melbourne"));
        dateString = formatter.format(new Date(milliSeconds));
        return dateString;
    }

}
