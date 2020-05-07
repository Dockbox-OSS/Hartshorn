package com.darwinreforged.server.core.resources;

import com.darwinreforged.server.core.init.DarwinServer;
import com.darwinreforged.server.modules.internal.darwin.DarwinServerModule;
import com.darwinreforged.server.core.util.FileUtils;
import com.darwinreforged.server.core.util.StringUtils;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public enum Permissions {

    DARWIN_STAFF("darwin.staff"),
    COMMAND_TICKET_OPEN("mmctickets.commands.open"),
    COMMAND_TICKET_CLOSE_SELF("mmctickets.commands.close.self"),
    COMMAND_TICKET_READ_SELF("mmctickets.commands.read.self"),
    COMMAND_STAFFLIST("mmctickets.commands.stafflist"),
    TICKET_STAFF("mmctickets.staff"),
    COMMAND_TICKET_CLOSE_ALL("mmctickets.commands.close.all"),
    COMMAND_TICKET_READ_ALL("mmctickets.commands.read.all"),
    COMMAND_TICKET_TELEPORT("mmctickets.commands.teleport"),
    COMMAND_TICKET_ASSIGN("mmctickets.commands.assign"),
    COMMAND_TICKET_CLAIM("mmctickets.commands.claim"),
    COMMAND_TICKET_UNCLAIM("mmctickets.commands.unclaim"),
    COMMAND_TICKET_REOPEN("mmctickets.commands.reopen"),
    COMMAND_TICKET_HOLD("mmctickets.commands.hold"),
    COMMAND_TICKET_BAN("mmctickets.commands.ban"),
    COMMAND_TICKET_COMMENT("mmctickets.commands.comment"),
    COMMAND_TICKET_EDIT_COMMENT("mmctickets.commands.edit.comment"),
    COMMAND_TICKET_RELOAD("mmctickets.commands.reload"),
    CLAIMED_TICKET_BYPASS("mmctickets.bypass.claimed"),
    FRIENDS_USE("darwinfriends.use"),
    ADD_PAINTING("paintings.add"),
    USE_PAINTING("paintings.use"),
    PAINTING_EXEMPT("paintings.exempt"),
    DAVE_RELOAD("dave.reload"),
    DAVE_MUTE("dave.mute"),
    HEADS_OPEN("hdb.open"),
    HEADS_SEARCH("hdb.open"),
    HEADS_MAIN("hdb.open"),
    PLAYER_DATA("darwin.playerdata"),
    TOGGLE_PID_BAR("darwinplotid.toggle"),
    WEATHER_PLOT("weatherplugin.command.plot"),
    WEATHER_SET("weatherplugin.command.set"),
    WEATHER_GLOBAL("weatherplugin.command.globalweather"),
    WEATHER_DEBUG("weatherplugin.command.debug"),
    BRUSH_TT_REFRESH("bt.refresh"),
    BRUSH_TT_USE("bt.use"),
    LAYERHEIGHT_USE("layerheight.use"),
    MULTI_CMD_USE("mc.use"),
    PTIME_SET("personaltime.command.set"),
    PTIME_RESET("personaltime.command.reset"),
    PTIME_STATUS("personaltime.command.status"),
    PTIME_USE("personaltime.command"),
    HOTBAR_LOAD("hb.load"),
    HOTBAR_SHARE("hb.share"),
    MODWIKI_USE("modwiki.use"),
    MODWIKI_RELOAD("modwiki.reload"),
    MODWIKI_SHARE("modwiki.share"),
    PLOTS_ADMIN_BUILD_OTHER("plots.admin.build.other"),
    PLOTS_ADMIN_BUILD_ROAD("plots.admin.build.road"),
    TRUSTLIMIT_UNLOCKED("ptl.unlocked"),
    SCHEMATIC_BRUSH_SET_USE("schematicbrush.set.use"),
    SCHEMATIC_BRUSH_LIST("schematicbrush.list"),
    SCHEMATIC_BRUSH_SET("schematicbrush.set.{0}"),
    ADMIN_BYPASS("darwin.admin.bypass-all");

    private String permission;

    Permissions(String permission) {
        this.permission = permission;
    }

    public String f(Object... args) {
        if (args.length == 0) return p();
        Map<String, String> map = new LinkedHashMap<>();
        if (args.length > 0) {
            for (int i = args.length - 1; i >= 0; i--) {
                String arg = "" + args[i];
                if (arg == null || arg.isEmpty()) map.put(String.format("{%d}", i), "");
                else map.put(String.format("{%d}", i), arg);
                if (i == 0) map.put("%s", arg);
            }
        }
        return StringUtils.replaceFromMap(p(), map);
    }

    public String p() {
        return this.permission.toLowerCase();
    }

    public static void collect() {
        DarwinServer.getModule(DarwinServerModule.class).ifPresent(module -> {
            Map<String, Object> configMap;
            File file = new File(DarwinServer.getUtilChecked(FileUtils.class).getConfigDirectory(module).toFile(), "permissions.yml");
            if (!file.exists()) {
                configMap = new HashMap<>();
                Arrays.stream(Permissions.values()).forEach(translation -> configMap.put(translation.name().toLowerCase().replaceAll("_", "."), translation.p()));
                DarwinServer.getUtilChecked(FileUtils.class).writeYaml(configMap, file);
            } else configMap = DarwinServer.getUtilChecked(FileUtils.class).getYamlData(file);

            configMap.forEach((k, v) -> {
                Permissions t = Permissions.valueOf(k.toUpperCase().replaceAll("\\.", "_"));
                if (t != null) t.c(v.toString());
            });
        });
    }

    private void c(String s) {
        this.permission = s;
    }
}
