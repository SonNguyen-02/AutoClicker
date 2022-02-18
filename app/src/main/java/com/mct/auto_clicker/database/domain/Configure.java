package com.mct.auto_clicker.database.domain;

import androidx.annotation.NonNull;

import com.mct.auto_clicker.database.room.entity.ActionEntity;
import com.mct.auto_clicker.database.room.entity.ConfigureEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Configure {
    private Long id;
    private String name;
    private List<Action> actions;
    private Integer amountExec;
    private Long timeStop;
    private boolean runByTimeStop;

    private Configure(Long id, String name, List<Action> actions) {
        this.id = id;
        this.name = name;
        this.actions = actions;
    }

    public Configure(Long id, String name, List<Action> actions, Integer amountExec) {
        this(id, name, actions);
        this.amountExec = amountExec;
        this.runByTimeStop = false;
    }

    public Configure(Long id, String name, List<Action> actions, Long timeStop) {
        this(id, name, actions);
        this.timeStop = timeStop;
        this.runByTimeStop = true;
    }

    public Configure(Long id, String name, List<Action> actions, Integer amountExec, Long timeStop, boolean runByTimeStop) {
        this(id, name, actions);
        this.amountExec = amountExec;
        this.timeStop = timeStop;
        this.runByTimeStop = runByTimeStop;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
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

    public boolean isRunByTimeStop() {
        return runByTimeStop;
    }

    public void setRunByTimeStop(boolean runByTimeStop) {
        this.runByTimeStop = runByTimeStop;
    }

    public ConfigureEntity toEntity() {
        return new ConfigureEntity(id, name, amountExec, timeStop, runByTimeStop);
    }

    public ConfigureEntity.ConfigureAndAction toConfigureAndAction() {
        return new ConfigureEntity.ConfigureAndAction(toEntity(), actions.stream().map(Action::toEntity).collect(Collectors.toList()));
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
        return new Configure(id, name, actionsCopy, amountExec, timeStop, runByTimeStop);
    }

    @NonNull
    @Override
    public String toString() {
        return "Configure{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", actions=" + actions +
                ", amountExec=" + amountExec +
                ", timeStop=" + timeStop +
                ", runByTimeStop=" + runByTimeStop +
                '}';
    }
}
