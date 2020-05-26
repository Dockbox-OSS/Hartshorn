package com.darwinreforged.server.core.resources.translations;

import com.darwinreforged.server.core.resources.ConfigSetting;

@ConfigSetting("friends")
public class FriendsTranslations {

    public static final Translation ACCEPTING_TP = Translation.create("$2Accept teleports from friends : $1{0}");
    public static final Translation FRIEND_TELEPORTED = Translation.create("$2{0} $1teleported to your location");
    public static final Translation NO_TP_NOT_FRIENDS = Translation.create("$4You are not friends with that user so you cannot teleport to them");
    public static final Translation ALREADY_FRIENDS = Translation.create("You are already friends with $2{0}");
    public static final Translation FRIEND_ADDED = Translation.create("You are now freinds with $2{0}");
    public static final Translation REQUEST_SENT = Translation.create("$1A friend request was sent to $2{0}");
    public static final Translation REQUEST_RECEIVED = Translation.create("$2{0} $1has requested to befriend you");
    public static final Translation FRIEND_REMOVED = Translation.create("$3{0} $1was removed as friend");
    public static final Translation FRIEND_ACCEPT_BUTTON = Translation.create("&f&mAccept");
    public static final Translation FRIEND_ACCEPT_BUTTON_HOVER = Translation.create("$1Click to accept request from {0}");
    public static final Translation FRIEND_DENY_BUTTON = Translation.create("&f&mDeny");
    public static final Translation FRIEND_DENY_BUTTON_HOVER = Translation.create("$1Click to deny request from {0}");
    public static final Translation FRIEND_ROW_REQUEST = Translation.create("$1{0} $2- Request");
    public static final Translation FRIEND_LIST_TITLE = Translation.create("$1Friends");

    private FriendsTranslations() {
    }

}
