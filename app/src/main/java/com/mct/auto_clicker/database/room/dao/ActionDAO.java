package com.mct.auto_clicker.database.room.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;


import com.mct.auto_clicker.database.room.entity.ActionEntity;

import java.util.List;

@Dao
public interface ActionDAO {

    @Query("SELECT * FROM action_table WHERE id = :actionId")
    ActionEntity getAction(Long actionId);

    @Query("SELECT * FROM action_table WHERE configureId = :configureId")
    List<ActionEntity> getActions(Long configureId);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Long add(ActionEntity action);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void addActions(List<ActionEntity> actions);

    @Update
    void update(ActionEntity action);

    @Update
    void updateActions(List<ActionEntity> actions);

    @Delete
    void delete(ActionEntity action);

    @Update
    void deleteActions(List<ActionEntity> actions);

}
