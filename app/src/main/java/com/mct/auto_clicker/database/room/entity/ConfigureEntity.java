package com.mct.auto_clicker.database.room.entity;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Relation;

import com.mct.auto_clicker.database.domain.Action;
import com.mct.auto_clicker.database.domain.Configure;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity(tableName = "configure_table")
public class ConfigureEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public long id;
    @ColumnInfo(name = "name")
    public String name;
    @ColumnInfo(name = "timeDelay")
    public long timeDelay;
    @ColumnInfo(name = "runType")
    public int runType;
    @ColumnInfo(name = "amountExec")
    public Integer amountExec;
    @ColumnInfo(name = "timeStop")
    public Long timeStop;

    public ConfigureEntity() {
    }

    public ConfigureEntity(long id, String name, long timeDelay, int runType, Integer amountExec, Long timeStop) {
        this.id = id;
        this.name = name;
        this.timeDelay = timeDelay;
        this.runType = runType;
        this.amountExec = amountExec;
        this.timeStop = timeStop;
    }

    public static class ConfigureAndAction {
        @Embedded
        public ConfigureEntity configure;
        @Relation(
                parentColumn = "id",
                entityColumn = "configureId"
        )
        public List<ActionEntity> actions;

        public ConfigureAndAction(ConfigureEntity configure, List<ActionEntity> actions) {
            this.configure = configure;
            this.actions = actions;
        }

        public Configure toConfigure() {
            List<Action> actionsList = actions.stream().map(ActionEntity::toAction).collect(Collectors.toList());
            return new Configure(configure.id, configure.name, actionsList, configure.timeDelay, configure.runType, configure.amountExec, configure.timeStop);
        }
    }

}

