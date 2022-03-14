package com.mct.auto_clicker.overlays.mainmenu.menu;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MenuPreference {

    private static final String PREFERENCE_NAME = "AUTO_CLICKER.SHARED.MENU";

    public static final int MAX_ITEM_SHOW = 10;

    // general setting
    public static final int MIN_ALPHA = 64;
    public static final int MAX_ALPHA = 100;

    public static final int DEFAULT_BUTTON_ACTION_ALPHA = MAX_ALPHA;
    private static final String K_BUTTON_ACTION_ALPHA = "k_button_action_alpha";

    public static final int DEFAULT_BUTTON_MENU_ALPHA = MAX_ALPHA;
    private static final String K_BUTTON_MENU_ALPHA = "k_button_menu_alpha";
    // target setting
    public static final int MIN_BUTTON_ACTION_SIZE = 70;
    public static final int MAX_BUTTON_ACTION_SIZE = 110;
    private static final int DEFAULT_BUTTON_ACTION_SIZE = 90;
    private static final String K_BUTTON_ACTION_SIZE = "k_action_size";

    // float menu setting
    public static final int MIN_BUTTON_MENU_SIZE = 90;
    public static final int MAX_BUTTON_MENU_SIZE = 110;
    private static final int DEFAULT_BUTTON_MENU_SIZE = 100;
    private static final String K_BUTTON_MENU_SIZE = "k_button_menu_size";

    private static final int DEFAULT_MENU_ORIENTATION = RecyclerView.VERTICAL;
    private static final String K_MENU_ORIENTATION = "k_menu_orientation";

    private static final int DEFAULT_MENU_SB_POSITION = 0;
    private static final String K_MENU_SB_POSITION = "k_menu_sb_position";

    private static final int DEFAULT_MENU_SB_COLOR = -16777216;
    private static final String K_MENU_SB_COLOR = "k_menu_sb_color";

    private static final String K_LIST_MENU_ITEM = "k_list_menu_item";
    private static final String SEPARATOR = "_";

    private final SharedPreferences mSharedPreferences;
    private final SharedPreferences.Editor mEditor;
    private static MenuPreference instance;

    private MenuPreference(@NonNull Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }

    public synchronized static MenuPreference getInstance(Context mContext) {
        if (instance == null) {
            instance = new MenuPreference(mContext);
        }
        return instance;
    }

    public int getButtonActionAlpha() {
        return mSharedPreferences.getInt(K_BUTTON_ACTION_ALPHA, DEFAULT_BUTTON_ACTION_ALPHA);
    }

    public MenuPreference setButtonActionAlpha(float alpha) {
        if (alpha >= 0 && alpha <= 1) {
            alpha *= 100;
        }
        if (alpha >= MIN_ALPHA && alpha <= MAX_ALPHA) {
            mEditor.putInt(K_BUTTON_ACTION_ALPHA, (int) alpha);
        }
        return this;
    }

    public int getButtonMenuAlpha() {
        return mSharedPreferences.getInt(K_BUTTON_MENU_ALPHA, DEFAULT_BUTTON_MENU_ALPHA);
    }

    public MenuPreference setButtonMenuAlpha(float alpha) {
        if (alpha >= 0 && alpha <= 1) {
            alpha *= 100;
        }
        if (alpha >= MIN_ALPHA && alpha <= MAX_ALPHA) {
            mEditor.putInt(K_BUTTON_MENU_ALPHA, (int) alpha);
        }
        return this;
    }

    public int getButtonActionSize() {
        return mSharedPreferences.getInt(K_BUTTON_ACTION_SIZE, DEFAULT_BUTTON_ACTION_SIZE);
    }

    public MenuPreference setButtonActionSize(int value) {
        if (value < MIN_BUTTON_ACTION_SIZE) value = MIN_BUTTON_ACTION_SIZE;
        if (value > MAX_BUTTON_ACTION_SIZE) value = MAX_BUTTON_ACTION_SIZE;
        mEditor.putInt(K_BUTTON_ACTION_SIZE, value);
        return this;
    }

    public int getButtonMenuSize() {
        return mSharedPreferences.getInt(K_BUTTON_MENU_SIZE, DEFAULT_BUTTON_MENU_SIZE);
    }

    public MenuPreference setButtonMenuSize(int value) {
        if (value < MIN_BUTTON_MENU_SIZE) value = MIN_BUTTON_MENU_SIZE;
        if (value > MAX_BUTTON_MENU_SIZE) value = MAX_BUTTON_MENU_SIZE;
        mEditor.putInt(K_BUTTON_MENU_SIZE, value);
        return this;
    }

    public int getMenuOrientation() {
        return mSharedPreferences.getInt(K_MENU_ORIENTATION, DEFAULT_MENU_ORIENTATION);
    }

    public MenuPreference setMenuOrientation(int value) {
        if (value == RecyclerView.VERTICAL || value == RecyclerView.HORIZONTAL) {
            mEditor.putInt(K_MENU_ORIENTATION, value);
        } else {
            mEditor.putInt(K_MENU_ORIENTATION, DEFAULT_MENU_ORIENTATION);
        }
        return this;
    }

    public float getMenuSbPosition() {
        return mSharedPreferences.getFloat(K_MENU_SB_POSITION, DEFAULT_MENU_SB_POSITION);
    }

    public MenuPreference setMenuSbPosition(float value) {
        mEditor.putFloat(K_MENU_SB_POSITION, value);
        return this;
    }

    public int getMenuColor() {
        return mSharedPreferences.getInt(K_MENU_SB_COLOR, DEFAULT_MENU_SB_COLOR);
    }

    public MenuPreference setMenuColor(int value) {
        mEditor.putInt(K_MENU_SB_COLOR, value);
        return this;
    }


    public List<MenuItemType> getListMenuItem() {
        String listItemStr = mSharedPreferences.getString(K_LIST_MENU_ITEM, "");
        if (listItemStr.isEmpty()) {
            return MenuItemType.getDefaultExpandItem();
        }
        String[] ids = listItemStr.split(SEPARATOR);
        return MenuItemType.findItemByIds(ids);
    }

    public MenuPreference setListMenuItem(@NonNull List<MenuItemType> items) {
        StringBuilder listItemStr = new StringBuilder();
        for (MenuItemType item : items) {
            if (item != null) {
                listItemStr.append(item.id).append(SEPARATOR);
            }
        }
        mEditor.putString(K_LIST_MENU_ITEM,
                listItemStr.length() != 0 ? listItemStr.substring(0, listItemStr.length() - 1) : "");
        return this;
    }

    public void commit() {
        mEditor.commit();
    }

    public void clear() {
        mEditor.clear().commit();
    }

}
