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

    @Query("SELECT * FROM action_table")
    List<ActionEntity> getAllActions();

    @Query("SELECT * FROM action_table WHERE id = :actionId")
    ActionEntity getAction(Long actionId);

    @Query("SELECT * FROM action_table WHERE configureId = :configureId")
    List<ActionEntity> getActionsByConfigure(Long configureId);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Long add(ActionEntity action);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void adds(List<ActionEntity> actions);

    @Update
    void update(ActionEntity action);

    @Update
    void updates(List<ActionEntity> actions);

    @Delete
    void delete(ActionEntity action);

    @Delete
    void deletes(List<ActionEntity> actions);

}
