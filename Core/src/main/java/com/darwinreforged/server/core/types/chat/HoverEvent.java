package com.darwinreforged.server.core.types.chat;

import com.google.gson.JsonObject;

/**
 The type Hover event.
 */
public final class HoverEvent {
    /**
     The enum Hover action.
     */
    public enum HoverAction {
        /**
         Show text hover action.
         */
        SHOW_TEXT,
        /**
         Show item hover action.
         */
        SHOW_ITEM,
        /**
         Show entity hover action.
         */
        SHOW_ENTITY;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    private HoverAction hoverAction;
    private String value;

    /**
     Instantiates a new Hover event.

     @param hoverAction
     the hover action
     @param value
     the value
     */
    public HoverEvent(HoverAction hoverAction, String value) {
        this.hoverAction = hoverAction;
        this.value = value;
    }


    /**
     To json json object.

     @return the json object
     */
    public JsonObject toJson() {
        JsonObject object = new JsonObject();

        object.addProperty("action", hoverAction.toString());
        object.addProperty("value", value);

        return object;
    }


    /**
     From json hover event.

     @param object
     the object

     @return the hover event
     */
    public static HoverEvent fromJson(JsonObject object) {
        String action = object.getAsJsonPrimitive("action").getAsString();
        String value = object.getAsJsonPrimitive("value").getAsString();

        return new HoverEvent(HoverAction.valueOf(action), value);
    }
}
