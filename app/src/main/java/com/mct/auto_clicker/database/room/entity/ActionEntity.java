package com.mct.auto_clicker.database.room.entity;

import static androidx.room.ForeignKey.CASCADE;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
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
    @ColumnInfo(name = "timeDelay")
    public Long timeDelay;
    @ColumnInfo(name = "actionDuration")
    public Long actionDuration;
    @ColumnInfo(name = "type")
    public ActionType type;

    // ActionType.CLICK | ActionType.GLOBAL_ACTION
    @ColumnInfo(name = "x")
    public Integer x = null;
    @ColumnInfo(name = "y")
    public Integer y = null;

    // ActionType.CLICK
    @ColumnInfo(name = "isAntiDetection")
    public Boolean isAntiDetection = null;

    // ActionType.GLOBAL_ACTION
    @ColumnInfo(name = "globalActionCode")
    public Integer globalActionCode;

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

    @Ignore
    private ActionEntity(Long id, Long configureId, Long timeDelay, Long actionDuration, ActionType type) {
        this.id = id;
        this.configureId = configureId;
        this.timeDelay = timeDelay;
        this.actionDuration = actionDuration;
        this.type = type;
    }

    @Ignore
    public ActionEntity(Long id, Long configureId, Long timeDelay, Long actionDuration, ActionType type, Integer x, Integer y, Boolean isAntiDetection) {
        this(id, configureId, timeDelay, actionDuration, type);
        this.x = x;
        this.y = y;
        this.isAntiDetection = isAntiDetection;
    }

    @Ignore
    public ActionEntity(Long id, Long configureId, Long timeDelay, Long actionDuration, ActionType type, Integer x1, Integer y1, Integer x2, Integer y2) {
        this(id, configureId, timeDelay, actionDuration, type);
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    @Ignore
    public ActionEntity(long id, Long configureId, Long timeDelay, Long actionDuration, ActionType type, Integer x, Integer y, Integer globalActionCode) {
        this.id = id;
        this.configureId = configureId;
        this.timeDelay = timeDelay;
        this.actionDuration = actionDuration;
        this.type = type;
        this.x = x;
        this.y = y;
        this.globalActionCode = globalActionCode;
    }

    public Action toAction() {
        switch (type) {
            case CLICK:
                return new Action.Click(id, configureId, timeDelay, actionDuration, x, y, isAntiDetection);
            case SWIPE:
                return new Action.Swipe(id, configureId, timeDelay, actionDuration, x1, y1, x2, y2);
            case ZOOM_IN:
                return new Action.Zoom(id, configureId, timeDelay, actionDuration, Action.Zoom.ZOOM_IN, x1, y1, x2, y2);
            case ZOOM_OUT:
                return new Action.Zoom(id, configureId, timeDelay, actionDuration, Action.Zoom.ZOOM_OUT, x1, y1, x2, y2);
            case GLOBAL_ACTION:
                return new Action.GlobalAction(id, configureId, timeDelay, x, y, Action.GlobalAction.GlobalType.valueOf(globalActionCode));
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
        ZOOM_OUT,
        /**
         * A global action.
         */
        GLOBAL_ACTION,
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
