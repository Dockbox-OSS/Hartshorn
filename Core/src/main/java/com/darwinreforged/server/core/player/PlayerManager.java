package com.darwinreforged.server.core.player;

import com.darwinreforged.server.core.DarwinServer;
import com.darwinreforged.server.core.chat.Pagination;
import com.darwinreforged.server.core.chat.Text;
import com.darwinreforged.server.core.files.FileManager;
import com.darwinreforged.server.core.internal.Utility;
import com.darwinreforged.server.core.player.inventory.DarwinItem;
import com.darwinreforged.server.core.player.state.GameModes;
import com.darwinreforged.server.core.types.living.MessageReceiver;
import com.darwinreforged.server.core.types.living.Target;
import com.darwinreforged.server.core.types.location.DarwinLocation;
import com.darwinreforged.server.modules.internal.darwin.DarwinServerModule;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Utility("Common player actions and (persistent) storage")
public abstract class PlayerManager {

    private static final Map<UUID, DarwinPlayer> PLAYER_STORAGE = new HashMap<>();
    private static PlayerManager playerUtils;

    public DarwinPlayer getPlayer(UUID uuid) {
        if (PLAYER_STORAGE.containsKey(uuid)) return PLAYER_STORAGE.get(uuid);

        updateUtil();
        String name = playerUtils.getPlayer(uuid).getName();
        return getPlayer(uuid, name);
    }

    public DarwinPlayer getPlayer(UUID uuid, String lastknownName) {
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
        if (playerUtils == null) playerUtils = DarwinServer.getUtilChecked(PlayerManager.class);
    }

    private static void updateStorage() {
        FileManager fu = DarwinServer.getUtilChecked(FileManager.class);
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


    /**
     Broadcast.

     @param message
     the message
     */
    public abstract void broadcast(Text message);

    /**
     Broadcast if permitted.

     @param message
     the message
     @param permission
     the permission
     */
    public abstract void broadcastIfPermitted(Text message, String permission);

    /**
     Tell.

     @param receiver
     the receiver
     @param message
     the message
     */
    public abstract void tell(MessageReceiver receiver, Text message);

    /**
     Tell no markup.

     @param receiver
     the receiver
     @param message
     the message
     */
    public abstract void tellNoMarkup(MessageReceiver receiver, Text message);

    /**
     Is online boolean.

     @param player
     the player

     @return the boolean
     */
    public boolean isOnline(DarwinPlayer player) {
        return isOnline(player.getUniqueId());
    }

    public abstract boolean isOnline(UUID uuid);

    /**
     Kick.

     @param player
     the player
     */
    public abstract void kick(DarwinPlayer player);

    /**
     Has permission boolean.

     @param player
     the player
     @param permission
     the permission

     @return the boolean
     */
    public abstract boolean hasPermission(DarwinPlayer player, String permission);

    /**
     Gets location.

     @param player
     the player

     @return the location
     */
    public abstract Optional<DarwinLocation> getLocation(DarwinPlayer player);

    /**
     Gets item in hand.

     @param player
     the player
     @param primaryHand
     the primary hand

     @return the item in hand
     */
    public abstract DarwinItem<?> getItemInHand(DarwinPlayer player, boolean primaryHand);

    /**
     Gets item in hand.

     @param player
     the player

     @return the item in hand
     */
    public DarwinItem<?> getItemInHand(DarwinPlayer player) {
        return getItemInHand(player, true);
    }

    /**
     Gets console id.

     @return the console id
     */
    public UUID getConsoleId() {
        return UUID.fromString("00000010-0010-0010-0010-000000000010");
    }

    /**
     Is console boolean.

     @param player
     the player

     @return the boolean
     */
    public boolean isConsole(Target player) {
        return (player.getUniqueId().equals(getConsoleId()));
    }

    /**
     Gets game mode.

     @param player
     the player

     @return the game mode
     */
    public abstract GameModes getGameMode(DarwinPlayer player);

    /**
     Sets game mode.

     @param mode
     the mode
     @param player
     the player
     */
    public abstract void setGameMode(GameModes mode, DarwinPlayer player);

    /**
     Execute cmd.

     @param cmd
     the cmd
     @param target
     the target
     */
    public abstract void executeCmd(String cmd, Target target);

    /**
     Gets online players.

     @return the online players
     */
    public abstract List<DarwinPlayer> getOnlinePlayers();

    /**
     Gets player.

     @param player
     the player

     @return the player
     */
    public abstract Optional<DarwinPlayer> getPlayer(String player);

    /**
     Send pagination.

     @param receiver
     the receiver
     @param pagination
     the pagination
     */
    public abstract void sendPagination(MessageReceiver receiver, Pagination pagination);

    public abstract String getPlayerName(UUID uuid);
}
