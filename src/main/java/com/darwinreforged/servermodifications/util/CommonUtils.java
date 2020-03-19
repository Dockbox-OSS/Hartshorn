package com.darwinreforged.servermodifications.util;

import com.darwinreforged.servermodifications.objects.InternalPluginContainer;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

class CommonUtils {

    static String getPluginId(Object plugin) {
        PluginContainer container = getPlugin(plugin);
        String pluginId = container.getId();

        if (pluginId == null) throw new RuntimeException("Object is not a plugin");
        else return pluginId;
    }

    static PluginContainer getPlugin(Object plugin) {
        String pluginId = null;
        if (plugin instanceof PluginContainer) {
            return (PluginContainer) plugin;
        } else {
            Plugin pluginAnn = plugin.getClass().getAnnotation(Plugin.class);
            return new InternalPluginContainer(pluginAnn);
        }
    }

}
