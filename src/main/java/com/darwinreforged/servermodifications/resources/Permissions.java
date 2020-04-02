package com.darwinreforged.servermodifications.resources;

import com.darwinreforged.servermodifications.DarwinServer;
import com.darwinreforged.servermodifications.modules.*;
import com.darwinreforged.servermodifications.modules.internal.ConfigModule;
import com.darwinreforged.servermodifications.modules.root.PluginModuleNative;
import com.darwinreforged.servermodifications.util.todo.FileManager;
import com.intellectualcrafters.plot.util.StringMan;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public enum Permissions {

    DARWIN_STAFF(PlotTrustLimitModule.Internal.class, "darwin.staff"),
    COMMAND_TICKET_OPEN(TicketModule.class, "mmctickets.commands.open"),
    COMMAND_TICKET_CLOSE_SELF(TicketModule.class, "mmctickets.commands.close.self"),
    COMMAND_TICKET_READ_SELF(TicketModule.class, "mmctickets.commands.read.self"),
    COMMAND_STAFFLIST(TicketModule.class, "mmctickets.commands.stafflist"),
    TICKET_STAFF(TicketModule.class, "mmctickets.staff"),
    COMMAND_TICKET_CLOSE_ALL(TicketModule.class, "mmctickets.commands.close.all"),
    COMMAND_TICKET_READ_ALL(TicketModule.class, "mmctickets.commands.read.all"),
    COMMAND_TICKET_TELEPORT(TicketModule.class, "mmctickets.commands.teleport"),
    COMMAND_TICKET_ASSIGN(TicketModule.class, "mmctickets.commands.assign"),
    COMMAND_TICKET_CLAIM(TicketModule.class, "mmctickets.commands.claim"),
    COMMAND_TICKET_UNCLAIM(TicketModule.class, "mmctickets.commands.unclaim"),
    COMMAND_TICKET_REOPEN(TicketModule.class, "mmctickets.commands.reopen"),
    COMMAND_TICKET_HOLD(TicketModule.class, "mmctickets.commands.hold"),
    COMMAND_TICKET_BAN(TicketModule.class, "mmctickets.commands.ban"),
    COMMAND_TICKET_COMMENT(TicketModule.class, "mmctickets.commands.comment"),
    COMMAND_TICKET_EDIT_COMMENT(TicketModule.class, "mmctickets.commands.edit.comment"),
    COMMAND_TICKET_RELOAD(TicketModule.class, "mmctickets.commands.reload"),
    CLAIMED_TICKET_BYPASS(TicketModule.class, "mmctickets.bypass.claimed"),
    FRIENDS_USE(FriendsModule.class, "darwinfriends.use"),
    ADD_PAINTING(PaintingsModule.class, "paintings.add"),
    USE_PAINTING(PaintingsModule.class, "paintings.use"),
    PAINTING_EXEMPT(PaintingsModule.class, "paintings.exempt"),
    DAVE_RELOAD(DaveChatModule.class, "dave.reload"),
    DAVE_MUTE(DaveChatModule.class, "dave.mute"),
    HEADS_OPEN(HeadDatabaseModule.class, "hdb.open"),
    HEADS_SEARCH(HeadDatabaseModule.class, "hdb.open"),
    HEADS_MAIN(HeadDatabaseModule.class, "hdb.open"),
    PLAYER_DATA(UserDataModule.class, "darwin.playerdata"),
    TOGGLE_PID_BAR(PlotIdBarModule.class, "darwinplotid.toggle"),
    WEATHER_PLOT(PlayerWeatherModule.class, "weatherplugin.command.plot"),
    WEATHER_SET(PlayerWeatherModule.class, "weatherplugin.command.set"),
    WEATHER_GLOBAL(PlayerWeatherModule.class, "weatherplugin.command.globalweather"),
    WEATHER_DEBUG(PlayerWeatherModule.class, "weatherplugin.command.debug"),
    BRUSH_TT_REFRESH(BrushTooltipsModule.class, "bt.refresh"),
    BRUSH_TT_USE(BrushTooltipsModule.class, "bt.use"),
    LAYERHEIGHT_USE(LayerHeightModule.class, "layerheight.use"),
    MULTI_CMD_USE(MultiCommandModule.class, "mc.use"),
    PTIME_SET(PlayerTimeModule.class, "personaltime.command.set"),
    PTIME_RESET(PlayerTimeModule.class, "personaltime.command.reset"),
    PTIME_STATUS(PlayerTimeModule.class, "personaltime.command.status"),
    PTIME_USE(PlayerTimeModule.class, "personaltime.command"),
    HOTBAR_LOAD(HotbarShareModule.class, "hb.load"),
    HOTBAR_SHARE(HotbarShareModule.class, "hb.share"),
    MODWIKI_USE(ModularWikiModule.class, "modwiki.use"),
    MODWIKI_RELOAD(ModularWikiModule.class, "modwiki.reload"),
    MODWIKI_SHARE(ModularWikiModule.class, "modwiki.share"),
    PLOTS_ADMIN_BUILD_OTHER(WaterFlowModule.External.class, "plots.admin.build.other"),
    PLOTS_ADMIN_BUILD_ROAD(WaterFlowModule.External.class, "plots.admin.build.road"),
    TRUSTLIMIT_UNLOCKED(PlotTrustLimitModule.class, "ptl.unlocked"),
    SCHEMATIC_BRUSH_SET_USE(SchematicBrushModule.class, "schematicbrush.set.use"),
    SCHEMATIC_BRUSH_LIST(SchematicBrushModule.class, "schematicbrush.list"),
    SCHEMATIC_BRUSH_SET(SchematicBrushModule.class, "schematicbrush.set.{0}");

    private final Class<? extends PluginModuleNative> module;
    private String permission;

    Permissions(Class<? extends PluginModuleNative> module, String permission) {
        this.module = module;
        this.permission = permission;
    }

    public static Permissions[] getModulePermissions(Class<? extends PluginModuleNative> module) {
        return Arrays.stream(Permissions.values()).filter(perm -> perm.module.equals(module)).toArray(Permissions[]::new);
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
        return StringMan.replaceFromMap(p(), map);
    }

    public String p() {
        return this.permission.toLowerCase();
    }

    public Class<? extends PluginModuleNative> m() {
        return this.module;
    }

    public static void collect() {
        DarwinServer.getModule(ConfigModule.class).ifPresent(module -> {
            Map<String, Object> configMap;
            File file = new File(FileManager.getConfigDirectory(module).toFile(), "permissions.yml");
            if (!file.exists()) {
                configMap = new HashMap<>();
                Arrays.stream(Permissions.values()).forEach(translation -> configMap.put(translation.name().toLowerCase().replaceAll("_", "."), translation.p()));
                FileManager.writeYaml(configMap, file);
            } else configMap = FileManager.getYamlData(file);

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
