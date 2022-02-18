package com.mct.auto_clicker.database;

import android.content.Context;

import com.mct.auto_clicker.database.domain.Action;
import com.mct.auto_clicker.database.domain.Configure;
import com.mct.auto_clicker.database.room.AutoClickDatabase;

import java.util.List;

public abstract class Repository {

    private static Repository INSTANCE;

    public synchronized static Repository getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new RepositoryImpl(AutoClickDatabase.getInstance(context));
        }
        return INSTANCE;
    }

    public abstract List<Configure> getAllConfigures();

    public abstract Configure getConfigure(Long configureId);

    public abstract Long addConfigure(Configure configure);

    public abstract void updateConfigure(Configure configure);

    public abstract void deleteConfigure(Configure configure);

    public abstract void deleteConfigures(List<Configure> configures);

    public abstract List<Action> getAllActions();

    public abstract List<Action> getActionsByConfigure(Long configureId);

    public abstract Action getAction(Long actionId);

    public abstract Long addAction(Action action);

    public abstract void updateAction(Action action);

    public abstract void deleteAction(Action action);

    public abstract void deleteActions(List<Action> actions);


}
