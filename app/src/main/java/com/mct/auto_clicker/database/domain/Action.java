package com.mct.auto_clicker.database.domain;

import android.content.res.Configuration;
import android.graphics.Point;
import android.util.Pair;

import androidx.annotation.NonNull;

import com.mct.auto_clicker.database.room.entity.ActionEntity;

import java.io.Serializable;
import java.util.Objects;

public abstract class Action implements Serializable {

    private long id;
    private long configureId;
    private String name;
    private long timeDelay;
    private long actionDuration;

    /**
     * <b>Action</b>.
     *
     * @param id             the unique identifier for the action. Use 0 for creating a new action. Default value is 0.
     * @param configureId    the identifier of the event for this action.
     * @param name           the name of the action.
     * @param timeDelay      the time delay apter run action in milliseconds
     * @param actionDuration the duration run action in milliseconds.
     */

    public Action(long id, long configureId, String name, long timeDelay, long actionDuration) {
        this.id = id;
        this.configureId = configureId;
        this.name = name;
        this.timeDelay = timeDelay;
        this.actionDuration = actionDuration;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getConfigureId() {
        return configureId;
    }

    public void setConfigureId(long configureId) {
        this.configureId = configureId;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTimeDelay() {
        return timeDelay;
    }

    public void setTimeDelay(long timeDelay) {
        this.timeDelay = timeDelay;
    }

    public long getActionDuration() {
        return actionDuration;
    }

    public void setActionDuration(long actionDuration) {
        this.actionDuration = actionDuration;
    }

    /**
     * getTotalDuration is time to next action
     */
    public long getTotalDuration() {
        return timeDelay + actionDuration;
    }

    /**
     * Cleanup all ids contained in this action. Ideal for copying.
     */
    public void cleanUpIds() {
        id = 0L;
        configureId = 0L;
    }

    public Pair<Integer, Integer> changeOrientation(int orientation, Point screenSize, int x, int y) {
        int newX, newY;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            // ngang sang doc
            newX = screenSize.x - y;
            newY = x;
        } else {
            // doc sang ngang
            newX = y;
            newY = screenSize.y - x;
        }
        return new Pair<>(newX, newY);
    }

    /**
     * @return the entity equivalent of this action.
     */
    public abstract ActionEntity toEntity();

    /**
     * @return creates a deep copy of this action.
     */
    public abstract Action deepCopy();

    public abstract void changeOrientationAction(int orientation, Point screenSize);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Action)) return false;
        Action action = (Action) o;
        return id == action.id && configureId == action.configureId && timeDelay == action.timeDelay && actionDuration == action.actionDuration && name.equals(action.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, configureId, name, timeDelay, actionDuration);
    }

    @NonNull
    @Override
    public String toString() {
        return "Action{" + "id=" + id + ", configureId=" + configureId + ", name='" + name + '\'' + ", timeDelay=" + timeDelay + ", actionDuration=" + actionDuration + '}';
    }

    public static class Click extends Action {

        private int x;
        private int y;
        private boolean isAntiDetection;

        /**
         * <b>Click action</b>.
         *
         * @param id              the unique identifier for the action. Use 0 for creating a new action. Default value is 0.
         * @param configureId     the identifier of the event for this action.
         * @param name            the name of the action.
         * @param timeDelay       the time delay before run action in milliseconds
         * @param clickDuration   the duration between the click down and up in milliseconds.
         * @param x               the x position of the click.
         * @param y               the y position of the click.
         * @param isAntiDetection the y position of the click.
         */
        public Click(long id, long configureId, String name, long timeDelay, long clickDuration, int x, int y, boolean isAntiDetection) {
            super(id, configureId, name, timeDelay, clickDuration);
            this.x = x;
            this.y = y;
            this.isAntiDetection = isAntiDetection;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public boolean isAntiDetection() {
            return isAntiDetection;
        }

        public void setAntiDetection(boolean antiDetection) {
            isAntiDetection = antiDetection;
        }

        @Override
        public ActionEntity toEntity() {
            return new ActionEntity(getId(), getConfigureId(), getName(), getTimeDelay(), getActionDuration(), ActionEntity.ActionType.CLICK, x, y, isAntiDetection);
        }

        @Override
        public Action deepCopy() {
            return new Click(getId(), getConfigureId(), getName(), getTimeDelay(), getActionDuration(), x, y, isAntiDetection);
        }

        @Override
        public void changeOrientationAction(int orientation, Point screenSize) {
            Pair<Integer, Integer> result = changeOrientation(orientation, screenSize, x, y);
            x = result.first;
            y = result.second;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Click)) return false;
            if (!super.equals(o)) return false;
            Click click = (Click) o;
            return x == click.x && y == click.y && isAntiDetection == click.isAntiDetection;
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), x, y, isAntiDetection);
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + "Click{" + "x=" + x + ", y=" + y + "}";
        }
    }

    public static class Swipe extends Action {

        private int fromX, fromY, toX, toY;

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

        public Swipe(long id, long configureId, String name, long timeDelay, long swipeDuration, int fromX, int fromY, int toX, int toY) {
            super(id, configureId, name, timeDelay, swipeDuration);
            this.fromX = fromX;
            this.fromY = fromY;
            this.toX = toX;
            this.toY = toY;
        }

        public int getFromX() {
            return fromX;
        }

        public void setFromX(int fromX) {
            this.fromX = fromX;
        }

        public int getFromY() {
            return fromY;
        }

        public void setFromY(int fromY) {
            this.fromY = fromY;
        }

        public int getToX() {
            return toX;
        }

        public void setToX(int toX) {
            this.toX = toX;
        }

        public int getToY() {
            return toY;
        }

        public void setToY(int toY) {
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

        @Override
        public void changeOrientationAction(int orientation, Point screenSize) {
            Pair<Integer, Integer> result = changeOrientation(orientation, screenSize, fromX, fromY);
            fromX = result.first;
            fromY = result.second;
            result = changeOrientation(orientation, screenSize, toX, toY);
            toX = result.first;
            toY = result.second;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Swipe)) return false;
            if (!super.equals(o)) return false;
            Swipe swipe = (Swipe) o;
            return fromX == swipe.fromX && fromY == swipe.fromY && toX == swipe.toX && toY == swipe.toY;
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), fromX, fromY, toX, toY);
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

        private int zoomType, x1, y1, x2, y2;

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
        public Zoom(long id, long configureId, String name, long timeDelay, long zoomDuration, int zoomType, int x1, int y1, int x2, int y2) {
            super(id, configureId, name, timeDelay, zoomDuration);
            this.zoomType = zoomType;
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }

        public int getZoomType() {
            return zoomType;
        }

        public void setZoomType(int zoomType) {
            this.zoomType = zoomType;
        }

        public int getX1() {
            return x1;
        }

        public void setX1(int x1) {
            this.x1 = x1;
        }

        public int getY1() {
            return y1;
        }

        public void setY1(int y1) {
            this.y1 = y1;
        }

        public int getX2() {
            return x2;
        }

        public void setX2(int x2) {
            this.x2 = x2;
        }

        public int getY2() {
            return y2;
        }

        public void setY2(int y2) {
            this.y2 = y2;
        }

        /**
         * getMidPoint
         * return a array has two element
         * arr[0] -> x
         * arr[1] -> y
         */
        public float[] getMidPoint() {
            float[] arr = new float[2];
            arr[0] = (x1 + x2) / 2f;
            arr[1] = (y1 + y2) / 2f;
            return arr;
        }

        @Override
        public ActionEntity toEntity() {
            return new ActionEntity(getId(), getConfigureId(), getName(), getTimeDelay(), getActionDuration(), zoomType == ZOOM_IN ? ActionEntity.ActionType.ZOOM_IN : ActionEntity.ActionType.ZOOM_OUT, x1, y1, x2, y2);
        }

        @Override
        public Action deepCopy() {
            return new Zoom(getId(), getConfigureId(), getName(), getTimeDelay(), getActionDuration(), zoomType, x1, y1, x2, y2);
        }

        @Override
        public void changeOrientationAction(int orientation, Point screenSize) {
            Pair<Integer, Integer> result = changeOrientation(orientation, screenSize, x1, y1);
            x1 = result.first;
            y1 = result.second;
            result = changeOrientation(orientation, screenSize, x2, y2);
            x2 = result.first;
            y2 = result.second;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Zoom)) return false;
            if (!super.equals(o)) return false;
            Zoom zoom = (Zoom) o;
            return zoomType == zoom.zoomType && x1 == zoom.x1 && y1 == zoom.y1 && x2 == zoom.x2 && y2 == zoom.y2;
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), zoomType, x1, y1, x2, y2);
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + "Zoom{" + "zoomType=" + zoomType + ", x1=" + x1 + ", y1=" + y1 + ", x2=" + x2 + ", y2=" + y2 + "} ";
        }
    }

}

