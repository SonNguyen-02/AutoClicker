package com.mct.auto_clicker.database.domain;

import androidx.annotation.NonNull;

import com.mct.auto_clicker.database.room.entity.ActionEntity;

public abstract class Action {

    private Long id;
    private Long configureId;
    private String name;
    private Long timeDelay;
    private Long actionDuration;

    /**
     * <b>Action</b>.
     *
     * @param id             the unique identifier for the action. Use 0 for creating a new action. Default value is 0.
     * @param configureId    the identifier of the event for this action.
     * @param name           the name of the action.
     * @param timeDelay      the time delay before run action in milliseconds
     * @param actionDuration the duration run action in milliseconds.
     */

    public Action(Long id, Long configureId, String name, Long timeDelay, Long actionDuration) {
        this.id = id;
        this.configureId = configureId;
        this.name = name;
        this.timeDelay = timeDelay;
        this.actionDuration = actionDuration;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getConfigureId() {
        return configureId;
    }

    public void setConfigureId(Long configureId) {
        this.configureId = configureId;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTimeDelay() {
        return timeDelay;
    }

    public void setTimeDelay(Long timeDelay) {
        this.timeDelay = timeDelay;
    }

    public Long getActionDuration() {
        return actionDuration;
    }

    public void setActionDuration(Long actionDuration) {
        this.actionDuration = actionDuration;
    }

    /**
     * Cleanup all ids contained in this action. Ideal for copying.
     */
    public void cleanUpIds() {
        id = 0L;
        configureId = 0L;
    }

    /**
     * @return the entity equivalent of this action.
     */
    public abstract ActionEntity toEntity();

    /**
     * @return creates a deep copy of this action.
     */
    public abstract Action deepCopy();

    @NonNull
    @Override
    public String toString() {
        return "Action{" + "id=" + id + ", configureId=" + configureId + ", name='" + name + '\'' + ", timeDelay=" + timeDelay + ", actionDuration=" + actionDuration + '}';
    }

    public static class Click extends Action {

        private Integer x;
        private Integer y;

        /**
         * <b>Click action</b>.
         *
         * @param id            the unique identifier for the action. Use 0 for creating a new action. Default value is 0.
         * @param configureId   the identifier of the event for this action.
         * @param name          the name of the action.
         * @param timeDelay     the time delay before run action in milliseconds
         * @param clickDuration the duration between the click down and up in milliseconds.
         * @param x             the x position of the click.
         * @param y             the y position of the click.
         */
        public Click(Long id, Long configureId, String name, Long timeDelay, Long clickDuration, Integer x, Integer y) {
            super(id, configureId, name, timeDelay, clickDuration);
            this.x = x;
            this.y = y;
        }

        public Integer getX() {
            return x;
        }

        public void setX(Integer x) {
            this.x = x;
        }

        public Integer getY() {
            return y;
        }

        public void setY(Integer y) {
            this.y = y;
        }

        @Override
        public ActionEntity toEntity() {
            return new ActionEntity(getId(), getConfigureId(), getName(), getTimeDelay(), getActionDuration(), ActionEntity.ActionType.CLICK, x, y);
        }

        @Override
        public Action deepCopy() {
            return new Click(getId(), getConfigureId(), getName(), getTimeDelay(), getActionDuration(), x, y);
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + "Click{" + "x=" + x + ", y=" + y + "}";
        }
    }

    public static class Swipe extends Action {

        private Integer fromX, fromY, toX, toY;

        /**
         * Swipe action.
         *
         * @param id            the unique identifier for the action. Use 0 for creating a new action. Default value is 0.
         * @param configureId   the identifier of the event for this action.
         * @param name          the name of the action.
         * @param timeDelay     the time delay before run action
         * @param swipeDuration the duration between the swipe start and end in milliseconds.
         * @param fromX         the x position of the swipe start.
         * @param fromY         the y position of the swipe start.
         * @param toX           the x position of the swipe end.
         * @param toY           the y position of the swipe end.
         */

        public Swipe(Long id, Long configureId, String name, Long timeDelay, Long swipeDuration, Integer fromX, Integer fromY, Integer toX, Integer toY) {
            super(id, configureId, name, timeDelay, swipeDuration);
            this.fromX = fromX;
            this.fromY = fromY;
            this.toX = toX;
            this.toY = toY;
        }

        public Integer getFromX() {
            return fromX;
        }

        public void setFromX(Integer fromX) {
            this.fromX = fromX;
        }

        public Integer getFromY() {
            return fromY;
        }

        public void setFromY(Integer fromY) {
            this.fromY = fromY;
        }

        public Integer getToX() {
            return toX;
        }

        public void setToX(Integer toX) {
            this.toX = toX;
        }

        public Integer getToY() {
            return toY;
        }

        public void setToY(Integer toY) {
            this.toY = toY;
        }

        @Override
        public ActionEntity toEntity() {
            return new ActionEntity(getId(), getConfigureId(), getName(), getTimeDelay(), getActionDuration(), ActionEntity.ActionType.SWIPE, fromX, fromY, toX, toY);
        }

        @Override
        public Action deepCopy() {
            return new Swipe(getId(), getConfigureId(), getName(), getTimeDelay(), getActionDuration(), fromX, fromY, toX, toY);
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + "Swipe{" + "fromX=" + fromX + ", fromY=" + fromY + ", toX=" + toX + ", toY=" + toY + "} ";
        }
    }

    public static class Zoom extends Action {

        public static final int ZOOM_IN = 0;
        public static final int ZOOM_OUT = 1;

        private Integer zoomType, x1, y1, x2, y2;

        /**
         * Swipe action.
         *
         * @param id           the unique identifier for the action. Use 0 for creating a new action. Default value is 0.
         * @param configureId  the identifier of the event for this action.
         * @param name         the name of the action.
         * @param timeDelay    the time delay before run action
         * @param zoomDuration the duration between the swipe start and end in milliseconds.
         * @param zoomType     the type of zoom action.
         * @param x1           the x1 position of the zoom point 1.
         * @param y1           the y1 position of the zoom point 1.
         * @param x2           the x2 position of the zoom point 2.
         * @param y2           the y2 position of the zoom point 2.
         */
        public Zoom(Long id, Long configureId, String name, Long timeDelay, Long zoomDuration, Integer zoomType, Integer x1, Integer y1, Integer x2, Integer y2) {
            super(id, configureId, name, timeDelay, zoomDuration);
            this.zoomType = zoomType;
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }

        public Integer getZoomType() {
            return zoomType;
        }

        public void setZoomType(Integer zoomType) {
            this.zoomType = zoomType;
        }

        public Integer getX1() {
            return x1;
        }

        public void setX1(Integer x1) {
            this.x1 = x1;
        }

        public Integer getY1() {
            return y1;
        }

        public void setY1(Integer y1) {
            this.y1 = y1;
        }

        public Integer getX2() {
            return x2;
        }

        public void setX2(Integer x2) {
            this.x2 = x2;
        }

        public Integer getY2() {
            return y2;
        }

        public void setY2(Integer y2) {
            this.y2 = y2;
        }

        @Override
        public ActionEntity toEntity() {
            return new ActionEntity(getId(), getConfigureId(), getName(), getTimeDelay(), getActionDuration(), zoomType == ZOOM_IN ? ActionEntity.ActionType.ZOOM_IN : ActionEntity.ActionType.ZOOM_OUT, x1, y1, x2, y2);
        }

        @Override
        public Action deepCopy() {
            return new Zoom(getId(), getConfigureId(), getName(), getTimeDelay(), getActionDuration(), zoomType, x1, y1, x2, y2);
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + "Zoom{" + "zoomType=" + zoomType + ", x1=" + x1 + ", y1=" + y1 + ", x2=" + x2 + ", y2=" + y2 + "} ";
        }
    }

}

