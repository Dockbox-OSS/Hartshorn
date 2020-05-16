package com.darwinreforged.server.core.player;

import com.darwinreforged.server.core.DarwinServer;
import com.darwinreforged.server.core.resources.Translations;
import com.darwinreforged.server.core.types.living.Target;
import com.darwinreforged.server.core.util.FileUtils;
import com.darwinreforged.server.core.util.PlayerUtils;
import com.darwinreforged.server.modules.internal.darwin.DarwinServerModule;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlayerManager {

    private static final Map<UUID, DarwinPlayer> PLAYER_STORAGE = new HashMap<>();
    private static PlayerUtils playerUtils;

    public static DarwinPlayer getPlayer(UUID uuid) {
        if (PLAYER_STORAGE.containsKey(uuid)) return PLAYER_STORAGE.get(uuid);

        updateUtil();
        String name = playerUtils.getPlayer(uuid)
                .map(Target::getName).orElse(Translations.UNKNOWN_PLAYER.s());
        return getPlayer(uuid, name);
    }

    public static DarwinPlayer getPlayer(UUID uuid, String lastknownName) {
        DarwinPlayer player;
        if (PLAYER_STORAGE.containsKey(uuid)) {
            player = PLAYER_STORAGE.get(uuid);
        } else {
            player = new DarwinPlayer(uuid, lastknownName);
            PLAYER_STORAGE.put(uuid, player);
            updateStorage();
        }
        return player;
    }

    private static void updateUtil() {
        if (playerUtils == null) playerUtils = DarwinServer.getUtilChecked(PlayerUtils.class);
    }

    @SuppressWarnings("unchecked")
    private static void updateStorage() {
        FileUtils fu = DarwinServer.getUtilChecked(FileUtils.class);
        File dataPath = fu.getDataDirectory(DarwinServerModule.class, "storage").toFile();
//        File playerStorageFile = new File(dataPath, "player-storage.yml");

//        List<PlayerStorageModel> existingPlayers = fu.getYamlDataFromFile(playerStorageFile, ArrayList.class, new ArrayList<PlayerStorageModel>());
//        existingPlayers.stream().filter(p -> !PLAYER_STORAGE.containsKey(p.getUniqueId()))
//                .forEach(p -> PLAYER_STORAGE.put(p.getUniqueId(), new DarwinPlayer(p.getUniqueId(), p.getName())));

        List<PlayerStorageModel> storedPlayers = PLAYER_STORAGE.values().stream().map(p -> new PlayerStorageModel(p.getName(), p.getUniqueId())).collect(Collectors.toList());
        fu.writeYamlDataToFile(storedPlayers, new File(dataPath, "player-storage.yml"));
    }

    private static final class PlayerStorageModel {
        private String name;
        private UUID uuid;

        public PlayerStorageModel(String name, UUID uuid) {
            this.name = name;
            this.uuid = uuid;
        }

        public String getName() {
            return name;
        }

        public UUID getUniqueId() {
            return uuid;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
