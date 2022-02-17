package com.mct.auto_clicker.database.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.mct.auto_clicker.database.room.dao.ActionDAO;
import com.mct.auto_clicker.database.room.dao.ConfigureDAO;
import com.mct.auto_clicker.database.room.entity.ActionEntity;
import com.mct.auto_clicker.database.room.entity.ConfigureEntity;


@Database(entities = {ActionEntity.class, ConfigureEntity.class}, version = 1)
@TypeConverters(ActionEntity.ActionTypeStringConverter.class)
public abstract class AutoClickDatabase extends RoomDatabase {

    private static final String DB_NAME = "auto_click.db";
    private static AutoClickDatabase instance;

    public static synchronized AutoClickDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context, AutoClickDatabase.class, DB_NAME).allowMainThreadQueries().build();
        }
        return instance;
    }

    public abstract ConfigureDAO configureDAO();

    public abstract ActionDAO actionDAO();

}
