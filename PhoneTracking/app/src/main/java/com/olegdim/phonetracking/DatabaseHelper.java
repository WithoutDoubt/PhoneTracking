package com.olegdim.phonetracking;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.os.Build;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Oleg on 13.03.2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "PhoneTrack.db";
//  UTNMEAData Table Description
    public static final String NMEA_TABLE_NAME = "UTNMEAData";
    public static final String NMEA_TABLE_COL_ID = "ID";
    public static final String NMEA_TABLE_COL_TIME = "Time";
    public static final String NMEA_TABLE_COL_NMEA = "NMEAData";
    //  UTLocation Table Description
    public static final String LOC_TABLE_NAME = "UTLocation";
    public static final String LOC_TABLE_COL_ID = "ID";
    public static final String LOC_TABLE_COL_TIME = "Time";
    public static final String LOC_TABLE_COL_LONG = "Longitude";
    public static final String LOC_TABLE_COL_LAT = "Latitude";
    public static final String LOC_TABLE_COL_ALT = "Altitude";
    public static final String LOC_TABLE_COL_BEARING = "Bearing";
    public static final String LOC_TABLE_COL_ELAPSEDREALTIMENANOS = "ElapsedRTNanos";
    public static final String LOC_TABLE_COL_ACC = "Accuracy";
    public static final String LOC_TABLE_COL_PROVIDER = "Provider";
    public static final String LOC_TABLE_COL_SPEED = "Speed";
    public static final String LOC_TABLE_COL_LOCTIME = "LocationTime";
//
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + NMEA_TABLE_NAME + " (" +
                        NMEA_TABLE_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        NMEA_TABLE_COL_TIME + " TEXT, " +
                        NMEA_TABLE_COL_NMEA + " TEXT " +
                   ")");

        db.execSQL("CREATE TABLE " + LOC_TABLE_NAME + " (" +
                LOC_TABLE_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                LOC_TABLE_COL_TIME + " TEXT, " +
                LOC_TABLE_COL_LONG + " REAL, " +
                LOC_TABLE_COL_LAT + " REAL, " +
                LOC_TABLE_COL_ALT + " REAL, " +
                LOC_TABLE_COL_ACC + " REAL, " +
                LOC_TABLE_COL_BEARING + " REAL, " +
                LOC_TABLE_COL_ELAPSEDREALTIMENANOS + " REAL, " +
                LOC_TABLE_COL_PROVIDER + " REAL, " +
                LOC_TABLE_COL_SPEED + " REAL, " +
                LOC_TABLE_COL_LOCTIME + " TEXT " +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + LOC_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + NMEA_TABLE_NAME);
        onCreate(db);
    }

    public boolean saveNMEAData(NMEAData data) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (db != null) {
            ContentValues val = new ContentValues();
//            if (data.get_id() > 0) {
//                val.put(NMEA_TABLE_COL_ID, data.get_id());
//            }
            val.put(NMEA_TABLE_COL_TIME, new SimpleDateFormat("yyyyMMddHHmmss").format(data.get_time()));
            val.put(NMEA_TABLE_COL_NMEA, data.get_data());
            if (data.get_id() > 0) {
                return db.update(NMEA_TABLE_NAME, val, NMEA_TABLE_COL_ID + "=?s", new String[] {String.valueOf(data.get_id())}) > 0;
            } else {
                return db.insert(NMEA_TABLE_NAME, null, val) > 0;
            }
        } else {
            return false;
        }
    }

    public boolean saveLocationData(LocationData data) {
        Log.d(Constants.LOG_TAG, "saveLocationData");

        SQLiteDatabase db = this.getWritableDatabase();
        if (db != null) {
            Log.d(Constants.LOG_TAG, "db != null");
            ContentValues val = new ContentValues();
//            if (data.get_id() > 0) {
//                val.put(LOC_TABLE_COL_ID, data.get_id());
//            }
            String strTime = new SimpleDateFormat("yyyyMMddHHmmss").format(data.get_time());
            val.put(LOC_TABLE_COL_TIME, strTime);
            val.put(LOC_TABLE_COL_LAT, data.get_location().getLatitude());
            val.put(LOC_TABLE_COL_LONG, data.get_location().getLongitude());
            val.put(LOC_TABLE_COL_ALT, data.get_location().getAltitude());
            val.put(LOC_TABLE_COL_BEARING, data.get_location().getBearing());

            if (Build.VERSION.SDK_INT >= 17) {
                val.put(LOC_TABLE_COL_ELAPSEDREALTIMENANOS, data.get_location().getElapsedRealtimeNanos());
            } else {
                val.put(LOC_TABLE_COL_ELAPSEDREALTIMENANOS, 0);
            }
            val.put(LOC_TABLE_COL_ACC, data.get_location().getAccuracy());
            val.put(LOC_TABLE_COL_PROVIDER, data.get_location().getProvider());
            val.put(LOC_TABLE_COL_SPEED, data.get_location().getSpeed());

            strTime = new SimpleDateFormat("yyyyMMddHHmmss").format(data.get_location().getTime());
            val.put(LOC_TABLE_COL_LOCTIME, strTime);
            if (data.get_id() > 0) {
                Log.d(Constants.LOG_TAG, "update");
                return db.update(LOC_TABLE_NAME, val, LOC_TABLE_COL_ID + "=?s", new String[] {String.valueOf(data.get_id())}) > 0;
            } else {
                Log.d(Constants.LOG_TAG, "insert");
                return db.insert(LOC_TABLE_NAME, null, val) > 0;
            }
        } else {
            return false;
        }
    }

    public Cursor getLocationData() {
        Cursor res = null;
        SQLiteDatabase db = this.getWritableDatabase();

        if (db != null) {
            res = db.rawQuery("SELECT * FROM " + LOC_TABLE_NAME, null);
        }

        return res;
    }

    public Cursor getNMEAData() {
        Cursor res = null;
        SQLiteDatabase db = this.getWritableDatabase();

        if (db != null) {
            res = db.rawQuery("SELECT * FROM " + NMEA_TABLE_NAME, null);
        }

        return res;
    }

}

