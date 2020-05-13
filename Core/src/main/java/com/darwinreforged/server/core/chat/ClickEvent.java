package com.darwinreforged.server.core.chat;

import com.google.gson.JsonObject;

/**
 The type Click event.
 */
public final class ClickEvent {

    /**
     The enum Click action.
     */
    public enum ClickAction {
        /**
         Open url click action.
         */
        OPEN_URL,
        /**
         Run command click action.
         */
        RUN_COMMAND,
        /**
         Suggest command click action.
         */
        SUGGEST_COMMAND,

        /**
         The Change page.
         */
// For Books
        CHANGE_PAGE;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    private ClickAction clickAction;
    private String value;

    /**
     Instantiates a new Click event.

     @param clickAction
     the click action
     @param value
     the value
     */
    public ClickEvent(ClickAction clickAction, String value) {
        this.clickAction = clickAction;
        this.value = value;
    }


    /**
     To json json object.

     @return the json object
     */
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


    /**
     From json click event.

     @param object
     the object

     @return the click event
     */
    public static ClickEvent fromJson(JsonObject object) {
        String action = object.getAsJsonPrimitive("action").getAsString();
        String value = object.getAsJsonPrimitive("value").getAsString();

        return new ClickEvent(ClickAction.valueOf(action), value);
    }

    public ClickAction getClickAction() {
        return clickAction;
    }

    public String getValue() {
        return value;
    }
}
