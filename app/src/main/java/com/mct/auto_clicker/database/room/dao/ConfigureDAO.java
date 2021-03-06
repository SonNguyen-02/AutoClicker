package com.mct.auto_clicker.database.room.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.mct.auto_clicker.database.room.entity.ConfigureEntity;

import java.util.List;

@Dao
public interface ConfigureDAO {

    @Transaction
    @Query("SELECT COUNT(*) FROM configure_table")
    long getCountConfigure();

    @Transaction
    @Query("SELECT * FROM configure_table ORDER BY id DESC")
    List<ConfigureEntity.ConfigureAndAction> getConfigureAndAction();

    @Transaction
    @Query("SELECT * FROM configure_table WHERE id=:configureId")
    ConfigureEntity.ConfigureAndAction getConfigureAndAction(Long configureId);

    @Transaction
    @Query("SELECT COUNT(*) FROM configure_table WHERE name=:name LIMIT 1")
    int isHasConfigureName(String name);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Long add(ConfigureEntity configureEntity);

    @Update
    void update(ConfigureEntity configureEntity);

    @Delete
    void delete(ConfigureEntity configureEntity);

    @Delete
    void deletes(List<ConfigureEntity> configureEntities);
}
