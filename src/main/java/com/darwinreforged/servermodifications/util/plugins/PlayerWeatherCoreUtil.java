package com.darwinreforged.servermodifications.util.plugins;

import com.darwinreforged.servermodifications.util.PlayerUtils;
import com.flowpowered.math.vector.Vector3d;
import eu.crushedpixel.sponge.packetgate.api.registry.PacketConnection;
import eu.crushedpixel.sponge.packetgate.api.registry.PacketGate;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.network.play.server.SPacketSpawnGlobalEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import java.util.*;

public class PlayerWeatherCoreUtil {
  // Doesn't contain CLEAR as this is the same as the server weather
  // - remove from hashmap when setting to clear instead
  public enum Weather {
    UNKNOWN(-1, "Unknown"),
    RESET(0, "Clear"),
    RAINING(1, "Rain"),
    LIGHTNING(2, "Lightning (no rain)"),
    LIGHTNINGSTORM(3, "Lightning (storm)");

    private static final HashMap<Integer, Weather> map = new HashMap<>(values().length - 1, 1);

    static {
      for (Weather weather : values()) map.put(weather.value, weather);
    }

    private int value;
    private String displayName;

    Weather(int value, String displayName){
      this.value =  value;
      this.displayName = displayName;
    }

    public int getValue()
    {
      return value;
    }

    public String getDisplayName() {
      return displayName;
    }

    public static Weather of(int value){

      Weather weather = map.get(value);
      if (weather == null){
        throw new IllegalArgumentException("Invalid weather value: " + value);
      }

      return weather;
    }
  }

  public static Weather parseWeather(String value) {
    switch (value.toLowerCase()) {
      case "rain":
      case "rainy":
      case "raining":
      case "snowing":
      case "snow":
        return Weather.RAINING;

      case "lightning":
      case "thunder":
        return Weather.LIGHTNING;

      case "storm":
      case "lightningstorm":
      case "thunderstorm":
        return Weather.LIGHTNINGSTORM;

      case "reset":
      case "clear":
      case "sunny":
      case "undo":
        return Weather.RESET;

      default:
        return Weather.UNKNOWN;
    }
  }

  private static HashMap<UUID, Weather> playerWeather = new HashMap<>();
  private static ArrayList<UUID> lightningPlayers = new ArrayList<>();

  public static boolean globalWeatherOff = false;

  public static void addToPlayerWeather(UUID uuid, Weather weather) {
    playerWeather.put(uuid, weather);
    if (weather == Weather.LIGHTNINGSTORM || weather == Weather.LIGHTNING) {
      addLightningPlayer(uuid);
    }
    else {
      removeLightningPlayer(uuid);
    }
  }

  public static void removePlayerWeather(UUID uuid) {
    playerWeather.remove(uuid);
    removeLightningPlayer(uuid);
  }

  public static boolean playerWeatherContains(UUID uuid) {
    return playerWeather.containsKey(uuid);
  }

  public static Set<UUID> getPlayerWeatherUUIDs(){
    return playerWeather.keySet();
  }

  public static Weather getPlayersWeather(UUID uuid){
    Weather weather = playerWeather.get(uuid);
    if (weather == null) { return Weather.RESET; }
    return weather;
  }

  public static void addLightningPlayer(UUID uuid) {
    if (!lightningPlayers.contains(uuid)) {
      lightningPlayers.add(uuid);

      if (lightningPlayers.size() == 1) {
        //Start Scheduler as it was previously disabled (no online players with
        //lightning as their weather type)
        PlayerWeatherLightningUtil.startlightningScheduler();
      }
    }
  }

  public static void removeLightningPlayer(UUID uuid) {
    if (lightningPlayers.contains(uuid)) {
      lightningPlayers.remove(uuid);

      if (lightningPlayers.size() == 0) {
        //Stop scheduler as there are no players online with Lightning as their weather type
        PlayerWeatherLightningUtil.stopLightningScheduler();
      }
    }
  }

  public static boolean lightningPlayersContains(UUID uuid) {
    return lightningPlayers.contains(uuid);
  }

  public static ArrayList<UUID> getLightningPlayers(){
    return lightningPlayers;
  }


  public static void sendPlayerWeatherPacket(UUID uuid, Weather weather) {

    if (globalWeatherOff && weather != Weather.RESET){
      Optional<Player> optionalPlayer = Sponge.getServer().getPlayer(uuid);
      optionalPlayer.ifPresent(player -> PlayerUtils.sendMessage(player, "Sorry, but pweather is currently disabled"));
      return;
    }

    Optional<PacketGate> packetGateOptional = Sponge.getServiceManager().provide(PacketGate.class);
    if (packetGateOptional.isPresent()) {
      PacketGate packetGate = packetGateOptional.get();
      Optional<PacketConnection> optionalConnection = packetGate.connectionByUniqueId(uuid);
      if (optionalConnection.isPresent())
      {
        PacketConnection connection = optionalConnection.get();
        // 1: End Raining, 2: Begin Raining
        switch (weather)
        {
          case RAINING:
            // 0f is unused for start / stop raining gameChangeState packets. This is just a
            // filler.
            connection.sendPacket(new SPacketChangeGameState(2, 0f));

            return;

          case RESET:
            // Change weather back to clear (server weather)
            connection.sendPacket(new SPacketChangeGameState(1, 0f));
            return;

          case LIGHTNING:
            Optional<Vector3d> optionalLightningPosition = PlayerWeatherLightningUtil.determineLightningPosition(uuid);

            MinecraftServer minecraftServer = FMLCommonHandler.instance().getMinecraftServerInstance();
            if (minecraftServer == null) return;
            net.minecraft.world.World forgeWorld = minecraftServer.getPlayerList().getPlayerByUUID(uuid).world;

            if (optionalLightningPosition.isPresent() && forgeWorld != null)
            {
              Vector3d lightningPosition = optionalLightningPosition.get();

              EntityLightningBolt lightningBolt = new EntityLightningBolt(forgeWorld,
                      lightningPosition.getX(), lightningPosition.getY(), lightningPosition.getZ(), false);

              connection.sendPacket(new SPacketSpawnGlobalEntity(lightningBolt));
            }
        }
      }
    }
  }
}
