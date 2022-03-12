package com.mct.auto_clicker.overlays.mainmenu.menu;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.mct.auto_clicker.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum MenuItemType {

    MENU_ITEM_PLAY_PAUSE(1, R.drawable.ic_start, R.string.content_desc_play_pause_configure),
    MENU_ITEM_ADD_TOUCH(2, R.drawable.ic_plus, R.string.content_desc_add_touch),
    MENU_ITEM_ADD_SWIPE(3, R.drawable.ic_swipe, R.string.content_desc_add_swipe),
    MENU_ITEM_ADD_ZOOM_IN(4, R.drawable.ic_zoom_in, R.string.content_desc_add_zoom_in),
    MENU_ITEM_ADD_ZOOM_OUT(5, R.drawable.ic_zoom_out, R.string.content_desc_add_zoom_out),
    MENU_ITEM_REMOVE(6, R.drawable.ic_minus, R.string.content_desc_remove_action),
    MENU_ITEM_SETTING(7, R.drawable.ic_setting, R.string.content_desc_setting_configure),

    MENU_ITEM_OPEN_RECENT(11, R.drawable.ic_recent, R.string.content_desc_open_recent),
    MENU_ITEM_GO_HOME(12, R.drawable.ic_home, R.string.content_desc_go_home),
    MENU_ITEM_GO_BACK(13, R.drawable.ic_go_back, R.string.content_desc_go_back),
    MENU_ITEM_OPEN_NOTIFICATIONS(14, R.drawable.ic_notification, R.string.content_desc_open_notification),
    MENU_ITEM_SHOW_HIDDEN(15, R.drawable.ic_eye, R.string.content_desc_show_hidden),

    MENU_ITEM_EXISTS(16, R.drawable.ic_close, R.string.content_desc_exists_the_configure),
    MENU_ITEM_EXPAND_COLLAPSE(17, R.drawable.ic_collapse, R.string.content_desc_collapse_expand_floating_menu);

    final int id; // distinct id
    int icon;
    final int iconDesc; // like image description

    MenuItemType(int id, @DrawableRes int icon, @StringRes int iconDesc) {
        this.id = id;
        this.icon = icon;
        this.iconDesc = iconDesc;
    }

    public int getId() {
        return id;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getIcon() {
        return icon;
    }

    public int getIconDesc() {
        return iconDesc;
    }


    @NonNull
    public static List<MenuItemType> getDefaultExpandItem() {
        List<MenuItemType> items = new ArrayList<>();
        items.add(MENU_ITEM_PLAY_PAUSE);
        items.add(MENU_ITEM_ADD_TOUCH);
        items.add(MENU_ITEM_ADD_SWIPE);
        items.add(MENU_ITEM_ADD_ZOOM_IN);
        items.add(MENU_ITEM_ADD_ZOOM_OUT);
        items.add(MENU_ITEM_REMOVE);
        items.add(MENU_ITEM_SETTING);
        items.add(MENU_ITEM_SHOW_HIDDEN);
        items.add(MENU_ITEM_EXISTS);
        items.add(MENU_ITEM_EXPAND_COLLAPSE);
        return items;
    }

    @NonNull
    public static List<MenuItemType> getDefaultCollapseItem() {
        List<MenuItemType> items = new ArrayList<>();
        items.add(MENU_ITEM_PLAY_PAUSE);
        items.add(MENU_ITEM_SHOW_HIDDEN);
        items.add(MENU_ITEM_EXISTS);
        items.add(MENU_ITEM_EXPAND_COLLAPSE);
        return items;
    }

    @Nullable
    public static MenuItemType findItemById(int id) {
        for (MenuItemType item : values()) {
            if (item.id == id) {
                return item;
            }
        }
        return null;
    }

    @NonNull
    public static List<MenuItemType> findItemByIds(@NonNull String... ids) {
        List<MenuItemType> items = new ArrayList<>();
        for (String id : ids) {
            MenuItemType item = findItemById(Integer.parseInt(id));
            if (item != null) {
                items.add(item);
            }
        }
        return items;
    }

    public static List<MenuItemType> getListItemDisable(@NonNull List<MenuItemType> listItemEnable) {
        return Arrays.stream(values()).filter(item -> !listItemEnable.contains(item)).collect(Collectors.toList());
    }

    public static boolean isCanDisable(MenuItemType item) {
        return item != MenuItemType.MENU_ITEM_EXPAND_COLLAPSE && item != MenuItemType.MENU_ITEM_PLAY_PAUSE;
    }

    public static boolean isCanRemove(MenuItemType item) {
        return item != MenuItemType.MENU_ITEM_EXPAND_COLLAPSE && item != MenuItemType.MENU_ITEM_PLAY_PAUSE;
    }

}
