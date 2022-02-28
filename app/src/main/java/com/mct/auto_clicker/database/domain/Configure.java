package com.mct.auto_clicker.database.domain;

import androidx.annotation.NonNull;

import com.mct.auto_clicker.database.room.entity.ConfigureEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Configure implements Serializable {

    public static final int RUN_TYPE_INFINITY = 0;
    public static final int RUN_TYPE_AMOUNT = 1;
    public static final int RUN_TYPE_TIME = 2;

    private long id;
    private String name;
    private List<Action> actions;
    private long timeDelay;
    private int runType;
    private Integer amountExec;
    private Long timeStop;
    private boolean isChoose;


    public Configure(long id, String name, List<Action> actions, long timeDelay) {
        this(id, name, actions, timeDelay, RUN_TYPE_INFINITY, null, null);
    }

    public Configure(long id, String name, List<Action> actions, long timeDelay, Integer amountExec) {
        this(id, name, actions, timeDelay, RUN_TYPE_AMOUNT, amountExec, null);
    }

    public Configure(long id, String name, List<Action> actions, long timeDelay, Long timeStop) {
        this(id, name, actions, timeDelay, RUN_TYPE_TIME, null, timeStop);
    }

    public Configure(long id, String name, List<Action> actions, long timeDelay, int runType, Integer amountExec, Long timeStop) {
        this.id = id;
        this.name = name;
        this.actions = actions;
        this.timeDelay = timeDelay;
        this.runType = runType;
        this.amountExec = amountExec;
        this.timeStop = timeStop;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Action> getActions() {
        if (actions == null) {
            actions = new ArrayList<>();
        }
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    public long getTimeDelay() {
        return timeDelay;
    }

    public void setTimeDelay(long timeDelay) {
        this.timeDelay = timeDelay;
    }

    public int getRunType() {
        return runType;
    }

    public void setRunType(int runType) {
        this.runType = runType;
    }

    public Integer getAmountExec() {
        return amountExec;
    }

    public void setAmountExec(Integer amountExec) {
        this.amountExec = amountExec;
    }

    public Long getTimeStop() {
        return timeStop;
    }

    public void setTimeStop(Long timeStop) {
        this.timeStop = timeStop;
    }

    public boolean isChoose() {
        return isChoose;
    }

    public void setChoose(boolean choose) {
        isChoose = choose;
    }

    public ConfigureEntity toEntity() {
        return new ConfigureEntity(id, name, timeDelay, runType, amountExec, timeStop);
    }

    public ConfigureEntity.ConfigureAndAction toConfigureAndAction() {
        return new ConfigureEntity.ConfigureAndAction(toEntity(), getActions().stream().map(Action::toEntity).collect(Collectors.toList()));
    }

    /**
     * Cleanup all ids contained in this event. Ideal for copying.
     */
    public void cleanUpIds() {
        id = 0L;
        actions.forEach(Action::cleanUpIds);
    }

    /**
     * @return creates a deep copy of this complete event and of its content.
     */
    public Configure deepCopy() {
        List<Action> actionsCopy = actions.stream().map(Action::deepCopy).collect(Collectors.toList());
        return new Configure(id, name, actionsCopy, timeDelay, runType, amountExec, timeStop);
    }

    @NonNull
    @Override
    public String toString() {
        return "Configure{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", actions=" + actions +
                ", timeDelay=" + timeDelay +
                ", runType=" + runType +
                ", amountExec=" + amountExec +
                ", timeStop=" + timeStop +
                '}';
    }
}
