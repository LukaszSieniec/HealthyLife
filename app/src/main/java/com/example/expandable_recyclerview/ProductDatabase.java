package com.example.expandable_recyclerview;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Product.class}, exportSchema = false, version = 1)
public abstract class ProductDatabase extends RoomDatabase {

    private static final String databaseName = "Products.db";
    private static ProductDatabase single_instance;

    public static ProductDatabase getInstance(Context context) {

        if(single_instance == null) {

            single_instance = Room.databaseBuilder(context.getApplicationContext(), ProductDatabase.class, databaseName).allowMainThreadQueries().build();
        }

        return single_instance;
    }

    public abstract ProductDao productDao();
}
