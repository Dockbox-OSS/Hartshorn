package com.darwinreforged.server.core.entities.chat;

import com.google.gson.JsonObject;

public final class ClickEvent {

    public enum ClickAction {
        OPEN_URL,
        RUN_COMMAND,
        SUGGEST_COMMAND,

        // For Books
        CHANGE_PAGE;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    private ClickAction clickAction;
    private String value;

    public ClickEvent(ClickAction clickAction, String value) {
        this.clickAction = clickAction;
        this.value = value;
    }


    public JsonObject toJson() {
        JsonObject object = new JsonObject();

        object.addProperty("action", clickAction.toString());

        // CHANGE_PAGE is an integer, the rest are Strings.
        if (this.clickAction == ClickAction.CHANGE_PAGE) {
            object.addProperty("value", Integer.valueOf(value));
        } else {
            object.addProperty("value", value);
        }

        return object;
    }


    public static ClickEvent fromJson(JsonObject object) {
        String action = object.getAsJsonPrimitive("action").getAsString();
        String value = object.getAsJsonPrimitive("value").getAsString();

        return new ClickEvent(ClickAction.valueOf(action), value);
    }
}
