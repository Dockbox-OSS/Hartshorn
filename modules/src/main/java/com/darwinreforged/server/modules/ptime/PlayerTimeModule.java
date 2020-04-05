package com.darwinreforged.server.modules.ptime;

import com.darwinreforged.server.api.DarwinServer;
import com.darwinreforged.server.api.modules.ModuleInfo;
import com.darwinreforged.server.api.modules.PluginModuleNative;
import com.darwinreforged.server.api.resources.Permissions;
import com.darwinreforged.server.api.resources.Translations;
import com.darwinreforged.server.api.utils.PlayerUtils;
import com.darwinreforged.server.mcp.protocol.Protocol;
import com.darwinreforged.server.mcp.protocol.ProtocolBuffer;
import com.darwinreforged.server.mcp.protocol.ProtocolGate;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import eu.crushedpixel.sponge.packetgate.api.event.PacketEvent;
import eu.crushedpixel.sponge.packetgate.api.listener.PacketListenerAdapter;
import eu.crushedpixel.sponge.packetgate.api.registry.PacketConnection;
import eu.crushedpixel.sponge.packetgate.api.registry.PacketGate;
import io.netty.buffer.Unpooled;

@ModuleInfo(id = "personaltime", name = "Personal Time", description = "Allows players to set their own personal time of day", version = "1.0-PRERELEASE-1", dependencies = @Dependency(id = "packetgate"))
public class PlayerTimeModule extends PacketListenerAdapter implements PluginModuleNative {

    private Map<UUID, Long> timeOffsets; // <Player UUID, time offset in ticks>

    public PlayerTimeModule() {
    }

    @Override
    public void onServerFinishLoad(GameInitializationEvent event) {
        timeOffsets = new HashMap<>();
        Optional<PacketGate> packetGateOptional = Sponge.getServiceManager().provide(PacketGate.class);
        if (packetGateOptional.isPresent()) {
            PacketGate packetGate = packetGateOptional.get();
            ProtocolGate.provide(packetGate).registerListener(this, ListenerPriority.DEFAULT, Protocol.TIME_UPDATE);
            initializeCommands();
            DarwinServer.getLogger().info("Personal Time has successfully initialized");
        } else {
            DarwinServer.getLogger().error("PacketGate is not installed. Personal Time depends on PacketGate in order to work");
        }
    }

    private void initializeCommands() {
        CommandSpec personalTimeSetCommand = CommandSpec.builder()
                .permission(Permissions.PTIME_SET.p())
                .arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("time"))))
                .executor((src, args) -> {
                    if (!(src instanceof Player)) {
                        PlayerUtils.tell(src, Translations.PLAYER_ONLY_COMMAND.t());
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
                                PlayerUtils.tell(player, Translations.PTIME_INVALID_NUMBER.ft(time));
                                return CommandResult.empty();
                            }
                            if (intTime < 0) {
                                PlayerUtils.tell(player, Translations.PTIME_NUMBER_TOO_SMALL.ft(time));
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
                .permission(Permissions.PTIME_RESET.p())
                .executor((src, args) -> {
                    if (!(src instanceof Player)) {
                        PlayerUtils.tell(src, Translations.PLAYER_ONLY_COMMAND.t());
                        return CommandResult.empty();
                    }
                    Player player = (Player) src;
                    resetPersonalTime(player);
                    return CommandResult.success();
                })
                .build();

        CommandSpec personalTimeStatusCommand = CommandSpec.builder()
                .permission(Permissions.PTIME_STATUS.p())
                .executor((src, args) -> {
                    if (!(src instanceof Player)) {
                        PlayerUtils.tell(src, Translations.PLAYER_ONLY_COMMAND.t());
                        return CommandResult.empty();
                    }
                    Player player = (Player) src;
                    timeOffsets.putIfAbsent(player.getUniqueId(), 0L);
                    long ticksAhead = timeOffsets.get(player.getUniqueId());
                    if (ticksAhead == 0) {
                        PlayerUtils.tell(player, Translations.PTIME_IN_SYNC.t());
                    } else {
                        PlayerUtils.tell(player, Translations.PTIME_AHEAD.ft(ticksAhead));
                    }
                    return CommandResult.success();
                })
                .build();

        CommandSpec personalTimeCommand = CommandSpec.builder()
                .permission(Permissions.PTIME_USE.p())
                .child(personalTimeSetCommand, "set")
                .child(personalTimeResetCommand, "reset")
                .child(personalTimeStatusCommand, "status")
                .build();

        DarwinServer.registerCommand(personalTimeCommand, "personaltime", "ptime");
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
    }

    private void resetPersonalTime(Player player) {
        timeOffsets.put(player.getUniqueId(), 0L);
    }

    @Override
    public void onPacketWrite(PacketEvent packetEvent, PacketConnection connection) {
        Protocol.TimeUpdate protocol = new Protocol.TimeUpdate(packetEvent);

        if (protocol.isEmpty()) return;

        UUID playerUuid = connection.getPlayerUUID();
        timeOffsets.putIfAbsent(playerUuid, 0L);

        ProtocolBuffer protocolBuffer = new ProtocolBuffer(Unpooled.buffer(16));

        try {
            protocol.writePacketData(protocolBuffer);
        } catch (IOException e) {
            DarwinServer.getLogger().error("Failed to read packet buffer : " + e.getMessage());
            return;
        }

        long totalWorldTime = protocolBuffer.readLong();
        long worldTime = protocolBuffer.readLong();

        long personalWorldTime;
        if (worldTime < 0) {
            personalWorldTime = worldTime - timeOffsets.get(playerUuid); // gamerule doDaylightCycle is false, which makes worldTime negative
        } else {
            personalWorldTime = worldTime + timeOffsets.get(playerUuid);
        }

        ProtocolGate.updateEvent(packetEvent, new Protocol.TimeUpdate(totalWorldTime, personalWorldTime, true));
    }


    // Unused, but required by contract due to use of Native Plugin Module
    @Listener
    public void onServerStart(GameStartedServerEvent event) {
    }
}
