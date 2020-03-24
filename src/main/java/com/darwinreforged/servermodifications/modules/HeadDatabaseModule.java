package com.darwinreforged.servermodifications.modules;

import com.darwinreforged.servermodifications.DarwinServer;
import com.darwinreforged.servermodifications.modules.root.ModuleInfo;
import com.darwinreforged.servermodifications.modules.root.PluginModule;
import com.darwinreforged.servermodifications.objects.HeadDatabaseChestInterface;
import com.darwinreforged.servermodifications.objects.HeadDatabaseHead;
import com.darwinreforged.servermodifications.resources.Permissions;
import com.darwinreforged.servermodifications.resources.Translations;
import com.darwinreforged.servermodifications.util.PlayerUtils;
import com.darwinreforged.servermodifications.util.todo.FileManager;
import com.darwinreforged.servermodifications.util.todo.HeadDatabaseConfigUtil;
import com.google.gson.*;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.plugin.meta.util.NonnullByDefault;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Set;

@ModuleInfo(
        id = "headdb",
        name = "HeadDatabase",
        version = "1.0.6",
        description = "Stores custom heads for Darwin Reforged")
public class HeadDatabaseModule extends PluginModule {

    public HeadDatabaseModule() {
    }

    @Override
    public void onServerFinishLoad(GameInitializationEvent event) {
        DarwinServer.registerCommand(hdbMain, "hdb");
    }

    HeadDatabaseConfigUtil handle;

    @Override
    public void onServerStart(GameStartedServerEvent event) {
        try {
            collectHeadsFromAPI();
        } catch (IOException e) {
            DarwinServer.getLogger().debug("Failed to get heads.");
        }

        File configFile = new File(FileManager.getConfigDirectory(this).toFile(), "HeadDatabase.conf");
        handle = new HeadDatabaseConfigUtil(configFile);
    }

    static String[] uuidBlacklist = {
            "c7299fa8d44b "
    }; // UUID's causing NumberFormatExceptions, currently only one exists in the entire database

    private void collectHeadsFromAPI() throws IOException {
        int totalHeads = 0;
        for (HeadDatabaseHead.Category cat : HeadDatabaseHead.Category.values()) {
            String connectionLine = Translations.HEADS_EVOLVED_API_URL.f(cat.toString().toLowerCase().replaceAll("_", "-"));
            JsonArray array = readJsonFromUrl(connectionLine);
            DarwinServer.getLogger().debug(cat.toString() + " : " + array.size());

            for (Object head : array) {
                if (head instanceof JsonObject) {
                    JsonElement nameEl = ((JsonObject) head).get("name");
                    JsonElement uuidEl = ((JsonObject) head).get("uuid");
                    JsonElement valueEl = ((JsonObject) head).get("value");
                    JsonElement tagsEl = ((JsonObject) head).get("tags");

                    String name = nameEl.getAsString();
                    String uuid = uuidEl.getAsString();
                    String value = valueEl.getAsString();
                    String tags = tagsEl instanceof JsonNull ? "None" : tagsEl.getAsString();

                    boolean doAdd = true;
                    for (String blackedUUID : uuidBlacklist) if (blackedUUID.equals(uuid)) doAdd = false;

                    if (doAdd) {
                        new HeadDatabaseHead(name, uuid, value, tags, cat);
                        totalHeads++;
                    }
                }
            }
        }

        DarwinServer.getLogger().debug("\nCollected : " + totalHeads + " heads from MinecraftHeadDB");
    }

    private String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public JsonArray readJsonFromUrl(String url) throws IOException {
        try (InputStream is = new URL(url).openStream()) {
            System.out.println("Requesting from : " + url);
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = readAll(rd);
            System.out.println("Got result : " + jsonText);
            return (JsonArray) new JsonParser().parse(jsonText);
        }
    }

    private CommandSpec hdbOpen =
            CommandSpec.builder()
                    .permission(Permissions.HEADS_OPEN.p())
                    .executor(new openInventory())
                    .build();

    private CommandSpec hdbSearch =
            CommandSpec.builder()
                    .permission(Permissions.HEADS_SEARCH.p())
                    .arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("query"))))
                    .executor(new searchHeads())
                    .build();

    private CommandSpec hdbMain =
            CommandSpec.builder()
                    .permission(Permissions.HEADS_MAIN.p())
                    .child(hdbOpen, "open")
                    .child(hdbSearch, "find", "search")
                    .build();

    private static class searchHeads implements CommandExecutor {

        @Override
        @NonnullByDefault
        public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
            if (src instanceof Player) {
                Player player = (Player) src;
                String query = args.<String>getOne("query").get();
                if (query != "") {
                    Set<HeadDatabaseHead> headObjects = HeadDatabaseHead.getByNameAndTag(query);
                    HeadDatabaseChestInterface.openViewForSet(headObjects, player, "$search");
                }
            }
            return CommandResult.success();
        }
    }

    private static class openInventory implements CommandExecutor {

        @Override
        @NonnullByDefault
        public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
            if (src instanceof Player) {
                Player player = (Player) src;
                try {
                    new HeadDatabaseChestInterface(player);
                } catch (InstantiationException e) {
                    PlayerUtils.tell(player, Translations.OPEN_GUI_ERROR.s());
                }
            }
            return CommandResult.success();
        }
    }
}
