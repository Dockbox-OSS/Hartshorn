package com.darwinreforged.servermodifications.util;

import com.darwinreforged.servermodifications.modules.root.ModuleInfo;
import com.darwinreforged.servermodifications.objects.InternalPluginContainer;
import org.spongepowered.api.plugin.PluginContainer;

public class PluginUtils {

    public static String getPluginId(Object plugin) {
        PluginContainer container = getPlugin(plugin);
        String pluginId = container.getId();

        if (pluginId == null || pluginId.isEmpty()) throw new RuntimeException("Object is not a plugin");
        else return pluginId;
    }

    public static PluginContainer getPlugin(Object plugin) {
        if (plugin instanceof PluginContainer) {
            return (PluginContainer) plugin;
        } else {
            ModuleInfo pluginAnn = plugin.getClass().getAnnotation(ModuleInfo.class);
            return new InternalPluginContainer(pluginAnn);
        }
    }

}
