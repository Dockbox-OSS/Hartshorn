package com.darwinreforged.server.core.entities.chat;

import com.google.gson.JsonObject;

public final class HoverEvent {
    public enum HoverAction {
        SHOW_TEXT,
        SHOW_ITEM,
        SHOW_ENTITY;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    private HoverAction hoverAction;
    private String value;

    public HoverEvent(HoverAction hoverAction, String value) {
        this.hoverAction = hoverAction;
        this.value = value;
    }


    public JsonObject toJson() {
        JsonObject object = new JsonObject();

        object.addProperty("action", hoverAction.toString());
        object.addProperty("value", value);

        return object;
    }


    public static HoverEvent fromJson(JsonObject object) {
        String action = object.getAsJsonPrimitive("action").getAsString();
        String value = object.getAsJsonPrimitive("value").getAsString();

        return new HoverEvent(HoverAction.valueOf(action), value);
    }
}
