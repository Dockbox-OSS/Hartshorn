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

/**
 The enum Permissions.
 */
public enum Permissions {

    /**
     Darwin staff permissions.
     */
    DARWIN_STAFF("darwin.staff"),
    /**
     Command ticket open permissions.
     */
    COMMAND_TICKET_OPEN("mmctickets.commands.open"),
    /**
     Command ticket close self permissions.
     */
    COMMAND_TICKET_CLOSE_SELF("mmctickets.commands.close.self"),
    /**
     Command ticket read self permissions.
     */
    COMMAND_TICKET_READ_SELF("mmctickets.commands.read.self"),
    /**
     Command stafflist permissions.
     */
    COMMAND_STAFFLIST("mmctickets.commands.stafflist"),
    /**
     Ticket staff permissions.
     */
    TICKET_STAFF("mmctickets.staff"),
    /**
     Command ticket close all permissions.
     */
    COMMAND_TICKET_CLOSE_ALL("mmctickets.commands.close.all"),
    /**
     Command ticket read all permissions.
     */
    COMMAND_TICKET_READ_ALL("mmctickets.commands.read.all"),
    /**
     Command ticket teleport permissions.
     */
    COMMAND_TICKET_TELEPORT("mmctickets.commands.teleport"),
    /**
     Command ticket assign permissions.
     */
    COMMAND_TICKET_ASSIGN("mmctickets.commands.assign"),
    /**
     Command ticket claim permissions.
     */
    COMMAND_TICKET_CLAIM("mmctickets.commands.claim"),
    /**
     Command ticket unclaim permissions.
     */
    COMMAND_TICKET_UNCLAIM("mmctickets.commands.unclaim"),
    /**
     Command ticket reopen permissions.
     */
    COMMAND_TICKET_REOPEN("mmctickets.commands.reopen"),
    /**
     Command ticket hold permissions.
     */
    COMMAND_TICKET_HOLD("mmctickets.commands.hold"),
    /**
     Command ticket ban permissions.
     */
    COMMAND_TICKET_BAN("mmctickets.commands.ban"),
    /**
     Command ticket comment permissions.
     */
    COMMAND_TICKET_COMMENT("mmctickets.commands.comment"),
    /**
     Command ticket edit comment permissions.
     */
    COMMAND_TICKET_EDIT_COMMENT("mmctickets.commands.edit.comment"),
    /**
     Command ticket reload permissions.
     */
    COMMAND_TICKET_RELOAD("mmctickets.commands.reload"),
    /**
     Claimed ticket bypass permissions.
     */
    CLAIMED_TICKET_BYPASS("mmctickets.bypass.claimed"),
    /**
     Friends use permissions.
     */
    FRIENDS_USE("darwinfriends.use"),
    /**
     Add painting permissions.
     */
    ADD_PAINTING("paintings.add"),
    /**
     Use painting permissions.
     */
    USE_PAINTING("paintings.use"),
    /**
     Painting exempt permissions.
     */
    PAINTING_EXEMPT("paintings.exempt"),
    /**
     Dave reload permissions.
     */
    DAVE_RELOAD("dave.reload"),
    /**
     Dave mute permissions.
     */
    DAVE_MUTE("dave.mute"),
    /**
     Heads open permissions.
     */
    HEADS_OPEN("hdb.open"),
    /**
     Heads search permissions.
     */
    HEADS_SEARCH("hdb.open"),
    /**
     Heads main permissions.
     */
    HEADS_MAIN("hdb.open"),
    /**
     Player data permissions.
     */
    PLAYER_DATA("darwin.playerdata"),
    /**
     Toggle pid bar permissions.
     */
    TOGGLE_PID_BAR("darwinplotid.toggle"),
    /**
     Weather plot permissions.
     */
    WEATHER_PLOT("weatherplugin.command.plot"),
    /**
     Weather set permissions.
     */
    WEATHER_SET("weatherplugin.command.set"),
    /**
     Weather global permissions.
     */
    WEATHER_GLOBAL("weatherplugin.command.globalweather"),
    /**
     Weather debug permissions.
     */
    WEATHER_DEBUG("weatherplugin.command.debug"),
    /**
     Brush tt refresh permissions.
     */
    BRUSH_TT_REFRESH("bt.refresh"),
    /**
     Brush tt use permissions.
     */
    BRUSH_TT_USE("bt.use"),
    /**
     Layerheight use permissions.
     */
    LAYERHEIGHT_USE("layerheight.use"),
    /**
     Multi cmd use permissions.
     */
    MULTI_CMD_USE("mc.use"),
    /**
     Ptime set permissions.
     */
    PTIME_SET("personaltime.command.set"),
    /**
     Ptime reset permissions.
     */
    PTIME_RESET("personaltime.command.reset"),
    /**
     Ptime status permissions.
     */
    PTIME_STATUS("personaltime.command.status"),
    /**
     Ptime use permissions.
     */
    PTIME_USE("personaltime.command"),
    /**
     Hotbar load permissions.
     */
    HOTBAR_LOAD("hb.load"),
    /**
     Hotbar share permissions.
     */
    HOTBAR_SHARE("hb.share"),
    /**
     Modwiki use permissions.
     */
    MODWIKI_USE("modwiki.use"),
    /**
     Modwiki reload permissions.
     */
    MODWIKI_RELOAD("modwiki.reload"),
    /**
     Modwiki share permissions.
     */
    MODWIKI_SHARE("modwiki.share"),
    /**
     Plots admin build other permissions.
     */
    PLOTS_ADMIN_BUILD_OTHER("plots.admin.build.other"),
    /**
     Plots admin build road permissions.
     */
    PLOTS_ADMIN_BUILD_ROAD("plots.admin.build.road"),
    /**
     Trustlimit unlocked permissions.
     */
    TRUSTLIMIT_UNLOCKED("ptl.unlocked"),
    /**
     Schematic brush set use permissions.
     */
    SCHEMATIC_BRUSH_SET_USE("schematicbrush.set.use"),
    /**
     Schematic brush list permissions.
     */
    SCHEMATIC_BRUSH_LIST("schematicbrush.list"),
    /**
     Schematic brush set permissions.
     */
    SCHEMATIC_BRUSH_SET("schematicbrush.set.{0}"),
    /**
     Wu add permissions.
     */
    WU_ADD("darwin.admin.worldunload"),
    /**
     Admin bypass permissions.
     */
    ADMIN_BYPASS("darwin.admin.bypass-all");

    private String permission;

    Permissions(String permission) {
        this.permission = permission;
    }

    /**
     F string.

     @param args
     the args

     @return the string
     */
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

    /**
     P string.

     @return the string
     */
    public String p() {
        return this.permission.toLowerCase();
    }

    /**
     Collect.
     */
    public static void collect() {
        DarwinServer.getModule(DarwinServerModule.class).ifPresent(module -> {
            Map<String, Object> configMap;
            File file = new File(DarwinServer.getUtilChecked(FileUtils.class).getConfigDirectory(module).toFile(), "permissions.yml");
            if (!file.exists()) {
                configMap = new HashMap<>();
                Arrays.stream(Permissions.values()).forEach(translation -> configMap.put(translation.name().toLowerCase().replaceAll("_", "."), translation.p()));
                DarwinServer.getUtilChecked(FileUtils.class).writeYamlDataToFile(configMap, file);
            } else configMap = DarwinServer.getUtilChecked(FileUtils.class).getYamlDataFromFile(file);

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
