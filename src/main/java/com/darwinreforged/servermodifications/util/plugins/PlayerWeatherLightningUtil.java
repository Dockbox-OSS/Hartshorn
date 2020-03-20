package com.darwinreforged.servermodifications.util.plugins;

import com.darwinreforged.servermodifications.plugins.PlayerWeatherPlugin;
import com.darwinreforged.servermodifications.util.LocationUtils;
import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


public class PlayerWeatherLightningUtil
{

    private static Task lightningTask;

    public static Optional<Vector3d> determineLightningPosition(UUID uuid)
    {
        Optional<Player> optionalPlayer = Sponge.getServer().getPlayer(uuid);
        if (optionalPlayer.isPresent())
        {

            Player player = optionalPlayer.get();
            return determineLightningPosition(player);
        }
        return Optional.empty(); //No player was found
    }

    public static Optional<Vector3d> determineLightningPosition(Player player)
    {
        final int MAX_DISTANCE = 50;

        World world = player.getWorld();
        Vector3d position = player.getTransform().getPosition();

        Random random = new Random();
        //2 * MAX_DISTANCE - MAX_DISTANCE will choose a random int from -MAX_DISTANCE to +MAX_DISTANCE
        int xPos = position.getFloorX() + random.nextInt(2 * MAX_DISTANCE) - MAX_DISTANCE;
        int zPos = position.getFloorZ() + random.nextInt(2 * MAX_DISTANCE) - MAX_DISTANCE;
        int yPos = LocationUtils.getHighestPoint(xPos, zPos, world);

        if (yPos == -1) return Optional.empty(); //No block was found

        Vector3d lightningStrikePosition = new Vector3d(xPos, yPos + 1, zPos);
        return Optional.of(lightningStrikePosition);

    }

    public static void startlightningScheduler(){
        lightningTask = Task.builder()
            .name("pweather - lightning scheduler")
            .interval(1, TimeUnit.SECONDS)
            .execute(PlayerWeatherLightningUtil::lightningMethod)
            .submit(PlayerWeatherPlugin.getPlugin());
    }

    public static void stopLightningScheduler(){
        if (lightningTask != null)
        {
            lightningTask.cancel();
            lightningTask = null;
        }
    }

    public static String lightningSchedulerStatus(){
        if (lightningTask != null) return "Lightning scheduler active with: "
                + PlayerWeatherCoreUtil.getLightningPlayers().size() + " players";
        return "Lightning scheduler inactive with: " + PlayerWeatherCoreUtil.getLightningPlayers().size() + " players";
    }

    private static void lightningMethod(){
        Random random = new Random();
        //This method is called once every second, with a 20% chance of lightning striking
        // Minecrafts has a 1/100,000 chance every frame.
        if (random.nextFloat() < 0.4f) {
            for (UUID uuid : PlayerWeatherCoreUtil.getLightningPlayers()) {
                Optional<Player> oPlayer = Sponge.getServer().getPlayer(uuid);
                PlayerWeatherCoreUtil.sendPlayerWeatherPacket(uuid, PlayerWeatherCoreUtil.Weather.LIGHTNING);
            }
        }
    }
}
