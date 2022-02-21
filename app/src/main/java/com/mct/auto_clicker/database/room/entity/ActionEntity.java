package com.mct.auto_clicker.database.room.entity;

import static androidx.room.ForeignKey.CASCADE;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;

import com.mct.auto_clicker.database.domain.Action;


@Entity(
        tableName = "action_table",
        foreignKeys = @ForeignKey(entity = ConfigureEntity.class,
                parentColumns = "id",
                childColumns = "configureId",
                onDelete = CASCADE),
        indices = {@Index("configureId")}
)
public class ActionEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public long id;
    @ColumnInfo(name = "configureId")
    public Long configureId;
    @ColumnInfo(name = "name")
    public String name;
    @ColumnInfo(name = "timeDelay")
    public Long timeDelay;
    @ColumnInfo(name = "actionDuration")
    public Long actionDuration;
    @ColumnInfo(name = "type")
    public ActionType type;

    // ActionType.CLICK
    @ColumnInfo(name = "x")
    public Integer x = null;
    @ColumnInfo(name = "y")
    public Integer y = null;
    @ColumnInfo(name = "isAntiDetection")
    public Boolean isAntiDetection = null;

    // ActionType.SWIPE | ActionType.ZOOM_IN | ActionType.ZOOM_OUT
    @ColumnInfo(name = "x1")
    public Integer x1 = null;
    @ColumnInfo(name = "y1")
    public Integer y1 = null;
    @ColumnInfo(name = "x2")
    public Integer x2 = null;
    @ColumnInfo(name = "y2")
    public Integer y2 = null;

    public ActionEntity() {
    }

    private ActionEntity(Long id, Long configureId, String name, Long timeDelay, Long actionDuration, ActionType type) {
        this.id = id;
        this.configureId = configureId;
        this.name = name;
        this.timeDelay = timeDelay;
        this.actionDuration = actionDuration;
        this.type = type;
    }

    public ActionEntity(Long id, Long configureId, String name, Long timeDelay, Long actionDuration, ActionType type, Integer x, Integer y, Boolean isAntiDetection) {
        this(id, configureId, name, timeDelay, actionDuration, type);
        this.x = x;
        this.y = y;
        this.isAntiDetection = isAntiDetection;
    }

    public ActionEntity(Long id, Long configureId, String name, Long timeDelay, Long actionDuration, ActionType type, Integer x1, Integer y1, Integer x2, Integer y2) {
        this(id, configureId, name, timeDelay, actionDuration, type);
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public Action toAction() {
        switch (type) {
            case CLICK:
                return new Action.Click(id, configureId, name, timeDelay, actionDuration, x, y, isAntiDetection);
            case SWIPE:
                return new Action.Swipe(id, configureId, name, timeDelay, actionDuration, x1, y1, x2, y2);
            case ZOOM_IN:
                return new Action.Zoom(id, configureId, name, timeDelay, actionDuration, Action.Zoom.ZOOM_IN, x1, y1, x2, y2);
            case ZOOM_OUT:
                return new Action.Zoom(id, configureId, name, timeDelay, actionDuration, Action.Zoom.ZOOM_OUT, x1, y1, x2, y2);
        }
        return null;
    }

    public enum ActionType {
        /**
         * A single tap on the screen.
         */
        CLICK,
        /**
         * A swipe on the screen.
         */
        SWIPE,
        /**
         * A zoom in on the screen.
         */
        ZOOM_IN,
        /**
         * A zoom out on the screen.
         */
        ZOOM_OUT
    }

    /**
     * Type converter to read/write the [ActionType] into the database.
     */
    public static class ActionTypeStringConverter {
        @TypeConverter
        public ActionType fromString(String value) {
            return ActionType.valueOf(value);
        }

        @TypeConverter
        public String toString(@NonNull ActionType type) {
            return type.toString();
        }
    }
}
