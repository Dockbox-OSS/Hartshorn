package com.darwinreforged.servermodifications.plugins;

import com.google.inject.Inject;
import eu.crushedpixel.sponge.packetgate.api.event.PacketEvent;
import eu.crushedpixel.sponge.packetgate.api.listener.PacketListenerAdapter;
import eu.crushedpixel.sponge.packetgate.api.registry.PacketConnection;
import eu.crushedpixel.sponge.packetgate.api.registry.PacketGate;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Plugin(id = "personaltime", name = "Personal Time", description = "Allows players to set their own personal time of day", version = "1.0-PRERELEASE-1", dependencies = @Dependency(id = "packetgate"))
public class PlayerTimePlugin extends PacketListenerAdapter {

    @Inject
    private Logger logger;

    private Map<UUID, Long> timeOffsets; // <Player UUID, time offset in ticks>

    public PlayerTimePlugin() {
    }

    @Listener
    public void onInitializationEvent(GameInitializationEvent event) {
        timeOffsets = new HashMap<>();
        Optional<PacketGate> packetGateOptional = Sponge.getServiceManager().provide(PacketGate.class);
        if (packetGateOptional.isPresent()) {
            PacketGate packetGate = packetGateOptional.get();
            packetGate.registerListener(this, ListenerPriority.DEFAULT, SPacketTimeUpdate.class);
            initializeCommands();
            logger.info("Personal Time has successfully initialized");
        } else {
            logger.error("PacketGate is not installed. Personal Time depends on PacketGate in order to work");
        }
    }

    private void initializeCommands() {
        CommandSpec personalTimeSetCommand = CommandSpec.builder()
                .description(Text.of("Set your personal time"))
                .permission("personaltime.command.set")
                .arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("time"))))
                .executor((src, args) -> {
                    if (!(src instanceof Player)) {
                        src.sendMessage(Text.of("This command may only be executed by a player"));
                        return CommandResult.empty();
                    }
                    Player player = (Player) src;
                    timeOffsets.putIfAbsent(player.getUniqueId(), 0L);
                    Optional<String> optionalTime = args.getOne("time");
                    if (optionalTime.isPresent()) {
                        String time = optionalTime.get();
                        if (time.equalsIgnoreCase("day")) {
                            setPersonalTime(player, 1000);
                            return CommandResult.success();
                        } else if (time.equalsIgnoreCase("night")) {
                            setPersonalTime(player, 14000);
                            return CommandResult.success();
                        } else {
                            int intTime;
                            try {
                                intTime = Integer.parseInt(time);
                            } catch (NumberFormatException e) {
                                sendMessage(player, "'" + time + "' is not a valid number");
                                return CommandResult.empty();
                            }
                            if (intTime < 0) {
                                sendMessage(player, "The number you have entered (" + time + ") is too small, it must be at least 0");
                                return CommandResult.empty();
                            }
                            setPersonalTime(player, intTime);
                            return CommandResult.success();
                        }
                    }
                    return CommandResult.empty();
                })
                .build();

        CommandSpec personalTimeResetCommand = CommandSpec.builder()
                .description(Text.of("Reset your personal time"))
                .permission("personaltime.command.reset")
                .executor((src, args) -> {
                    if (!(src instanceof Player)) {
                        src.sendMessage(Text.of("This command may only be executed by a player"));
                        return CommandResult.empty();
                    }
                    Player player = (Player) src;
                    resetPersonalTime(player);
                    return CommandResult.success();
                })
                .build();

        CommandSpec personalTimeStatusCommand = CommandSpec.builder()
                .description(Text.of("Get the status of your personal time"))
                .permission("personaltime.command.status")
                .executor((src, args) -> {
                    if (!(src instanceof Player)) {
                        src.sendMessage(Text.of("This command may only be executed by a player"));
                        return CommandResult.empty();
                    }
                    Player player = (Player) src;
                    timeOffsets.putIfAbsent(player.getUniqueId(), 0L);
                    long ticksAhead = timeOffsets.get(player.getUniqueId());
                    if (ticksAhead == 0) {
                        sendMessage(player, "Your time is currently in sync with the server's");
                    } else {
                        sendMessage(player, "Your time is currently running " + ticksAhead + " ticks ahead of the server's");
                    }
                    return CommandResult.success();
                })
                .build();

        CommandSpec personalTimeCommand = CommandSpec.builder()
                .description(Text.of("The one command for PersonalTime"))
                .permission("personaltime.command")
                .child(personalTimeSetCommand, "set")
                .child(personalTimeResetCommand, "reset")
                .child(personalTimeStatusCommand, "status")
                .build();

        Sponge.getCommandManager().register(this, personalTimeCommand, "personaltime", "ptime");
    }

    private void sendMessage(Player player, String text) {
        player.sendMessage(Text.of(TextColors.GREEN, "[", TextColors.RED, "PersonalTime", TextColors.GREEN, "] ", TextColors.YELLOW, text));
    }

    private String ticksToRealTime(long ticks) {
        int hours = (int) (ticks / 1000.0) + 6;
        int minutes = (int) (((ticks % 1000) / 1000.0) * 60.0);

        String suffix = "AM";

        if (hours >= 12) {
            hours -= 12;
            suffix = "PM";
            if (hours >= 12) {
                hours -= 12;
                suffix = "AM";
            }
        }

        if (hours == 0) {
            hours += 12;
        }

        return hours + ":" + String.format("%02d", minutes) + " " + suffix;
    }

    private void setPersonalTime(Player player, long ticks) {
        World world = player.getWorld();
        long worldTime = world.getProperties().getWorldTime();
        long desiredPersonalTime = (long) Math.ceil(worldTime / 24000.0f) * 24000 + ticks; // Fast forward to the next '0' time and add the desired number of ticks
        long timeOffset = desiredPersonalTime - worldTime;
        timeOffsets.put(player.getUniqueId(), timeOffset);

        sendMessage(player, "Set time to " + ticks + " (" + ticksToRealTime(ticks % 24000) + ")");
    }

    private void resetPersonalTime(Player player) {
        timeOffsets.put(player.getUniqueId(), 0L);
        sendMessage(player, "Your time is now synchronized with the server's");
    }

    @Override
    public void onPacketWrite(PacketEvent packetEvent, PacketConnection connection) {
        if (!(packetEvent.getPacket() instanceof SPacketTimeUpdate)) {
            return;
        }

        UUID playerUuid = connection.getPlayerUUID();
        timeOffsets.putIfAbsent(playerUuid, 0L);

        SPacketTimeUpdate packet = (SPacketTimeUpdate) packetEvent.getPacket();
        PacketBuffer packetBuffer = new PacketBuffer(Unpooled.buffer(16));
        try {
            packet.writePacketData(packetBuffer);
        } catch (IOException e) {
            logger.error("Failed to read packet buffer");
            return;
        }

        long totalWorldTime = packetBuffer.readLong();
        long worldTime = packetBuffer.readLong();

        long personalWorldTime;
        if (worldTime < 0) {
            personalWorldTime = worldTime - timeOffsets.get(playerUuid); // gamerule doDaylightCycle is false, which makes worldTime negative
        } else {
            personalWorldTime = worldTime + timeOffsets.get(playerUuid);
        }

        packetEvent.setPacket(new SPacketTimeUpdate(totalWorldTime, personalWorldTime, true));
    }
}
