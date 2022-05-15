package edu.hanu.mycart;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "cart.db";
    private static final int DB_VERSION = 1;

    public DBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    //this is called the first time a dbs is accessed. There should be code in there to create a new dbs
    @Override
    public void onCreate(SQLiteDatabase db) {
        // create table
        db.execSQL("CREATE TABLE cart (id Integer PRIMARY KEY AUTOINCREMENT, " +
                " title VARCHAR, productId Integer, image BLOB, price Integer, quantity Integer)");
    }

    //this is called if the databse version number changes
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
       db.execSQL("DROP TABLE IF EXISTS cart");

       onCreate(db);
    }




}
