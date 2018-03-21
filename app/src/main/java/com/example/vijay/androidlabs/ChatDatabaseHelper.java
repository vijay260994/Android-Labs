package com.example.vijay.androidlabs;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.SQLClientInfoException;

/**
 * Created by Vijay on 2018-03-07.
 */

public class ChatDatabaseHelper extends SQLiteOpenHelper
{
    public static final int VERSION_NUM= 3;
    public static final String DATABASE_NAME = "Messages";
    public static final String TABLE_NAME = "tblMsg";
    public static final String KEY_ID = "id";
    public static final String KEY_MESSAGE = "MESSAGE";

//    final static String CREATE_QUERY= "CREATE TABLE " + TABLE_NAME + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "  + KEY_MESSAGE + " TEXT);";

    public ChatDatabaseHelper(Context ctx)
    {
        super(ctx, DATABASE_NAME, null, VERSION_NUM);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "  + KEY_MESSAGE + " TEXT);" );
        Log.i("ChatDatabaseHelper", "Calling onCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME );
        onCreate(db);
        Log.i("ChatDatabaseHelper", "Calling onUpgrade, oldVersion=" + oldVer + ", newVersion=" + newVer);
    }
}