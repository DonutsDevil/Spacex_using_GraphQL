package com.example.leanix.Repositry.Room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.leanix.Model.Launch;

@Database(entities = {Launch.class},version = 1,exportSchema = false)
@TypeConverters({BooleanConvertor.class, FlickerImageUrlConvertor.class})

public abstract class AppDatabase extends RoomDatabase {
    private static final Object LOCK = new Object();
    private static final String db_name = "LaunchDb";
    private static AppDatabase mInstance = null;

    public static AppDatabase getInstance(Context context) {
        if (mInstance == null) {
            synchronized (LOCK) {
                mInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, db_name).build();
            }
        }
        return mInstance;
    }

    public abstract BookmarkDAO bookmarkDAO();
}
