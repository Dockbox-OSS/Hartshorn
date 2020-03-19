package com.darwinreforged.servermodifications.plugins;

import org.slf4j.Logger;

import java.util.List;
import java.util.Properties;

public class DavePluginWrapper {

    private static DavePlugin singleton;

    public static void setSingleton(DavePlugin davePlugin) {
        if (davePlugin != null)
            DavePluginWrapper.singleton = davePlugin;
        else throw new NullPointerException("Cannot set a null singleton!");
    }

    public static DavePlugin getSingleton() {
        return singleton;
    }

    public static Logger getLogger() {
        return singleton.getLogger();
    }

    public static Properties getSettingsProperties() {
        return singleton.getSettingsProperties();
    }

    public static Properties getMessagesProperties() {
        return singleton.getMessagesProperties();
    }

    public static List<String> getMutedPlayers() {
        return singleton.getPlayerWhoMutedDave();
    }

}
