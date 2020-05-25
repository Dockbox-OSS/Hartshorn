package com.darwinreforged.server.modules.extensions.chat.modularwiki;

public class WikiObject {

    private String id;
    private String name;
    private String permission;
    private String[] description;
    private boolean hide = false;

    public WikiObject() {
    }

    public WikiObject(String id, String name, String permission, String[] description, boolean hide) {
        this.id = id;
        this.name = name;
        this.permission = permission;
        this.description = description;
        this.hide = hide;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPermission() {
        return permission;
    }

    public String[] getDescription() {
        return description;
    }

    public boolean isHide() {
        return hide;
    }
}
