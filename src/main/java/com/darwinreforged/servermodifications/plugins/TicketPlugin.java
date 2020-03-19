package com.darwinreforged.servermodifications.plugins;

import com.darwinreforged.servermodifications.commands.tickets.*;
import com.darwinreforged.servermodifications.listeners.TicketLoginAndDiscordListener;
import com.darwinreforged.servermodifications.objects.TicketData;
import com.darwinreforged.servermodifications.objects.TicketPlayerData;
import com.darwinreforged.servermodifications.permissions.TicketPermissions;
import com.darwinreforged.servermodifications.translations.TicketMessages;
import com.darwinreforged.servermodifications.util.TicketUtil;
import com.darwinreforged.servermodifications.util.config.TicketConfig;
import com.darwinreforged.servermodifications.util.database.DataStoreManager;
import com.darwinreforged.servermodifications.util.database.IDataStore;
import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameAboutToStartServerEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.darwinreforged.servermodifications.objects.TicketStatus.*;


@Plugin(
        id = "mmctickets",
        name = "MMCTickets",
        version = "2.0.7",
        description = "A real time ticket system")
public class TicketPlugin {

    @Inject
    private Logger logger;

    @Inject
    @DefaultConfig(sharedRoot = false)
    public Path defaultConf;

    @Inject
    @ConfigDir(sharedRoot = false)
    public Path ConfigDir;

    public TicketConfig config;
    public TicketMessages messages;

    private CommandManager cmdManager = Sponge.getCommandManager();

    private ArrayList<String> waitTimer;
    private DataStoreManager dataStoreManager;

    public TicketPlugin() {
    }

    @Listener
    public void Init(GameInitializationEvent event) throws IOException, ObjectMappingException {
        Sponge.getEventManager().registerListeners(this, new TicketLoginAndDiscordListener(this));

        TypeSerializers.getDefaultSerializers()
                .registerType(TypeToken.of(TicketData.class), new TicketData.TicketSerializer());
        TypeSerializers.getDefaultSerializers()
                .registerType(TypeToken.of(TicketPlayerData.class), new TicketPlayerData.TicketPlayerDataSerializer());

        config = new TicketConfig(this);
        messages = new TicketMessages(this);
        loadCommands();
    }

    @Listener
    public void onServerAboutStart(GameAboutToStartServerEvent event) {
        dataStoreManager = new DataStoreManager(this);
        if (dataStoreManager.load()) {
            getLogger().info("MMCTickets datastore Loaded");
        } else {
            getLogger().error("Unable to load a datastore please check your Console/Config!");
        }
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        getLogger().info("MMCTickets Loaded");
        getLogger().info("Tickets loaded: " + getDataStore().getTicketData().size());
        getLogger().info("Notifications loaded: " + getDataStore().getNotifications().size());
        getLogger().info("PlayerData loaded: " + getDataStore().getPlayerData().size());

        this.waitTimer = new ArrayList<String>();

        // start ticket nag timer
        nagTimer();
    }

    @Listener
    public void onPluginReload(GameReloadEvent event) throws IOException, ObjectMappingException {
        this.config = new TicketConfig(this);
        this.messages = new TicketMessages(this);
        dataStoreManager = new DataStoreManager(this);
        loadDataStore();
    }

    public void loadDataStore() {
        if (dataStoreManager.load()) {
            getLogger().info("MMCTickets datastore Loaded");
        } else {
            getLogger().error("Unable to load a datastore please check your Console/Config!");
        }
    }

    public void setDataStoreManager(DataStoreManager dataStoreManager) {
        this.dataStoreManager = dataStoreManager;
    }

    private void loadCommands() {
        // /stafflist
        CommandSpec staffList =
                CommandSpec.builder()
                        .description(Text.of("List online staff members"))
                        .executor(new TicketStaffCommand(this))
                        .build();

        // /ticket read self
        CommandSpec readSelf =
                CommandSpec.builder()
                        .description(Text.of("Display a list of all tickets the player owns"))
                        .executor(new TicketReadSelfCommand(this))
                        .build();

        // /ticket read closed
        CommandSpec readClosed =
                CommandSpec.builder()
                        .description(Text.of("Display a list of all closed tickets"))
                        .executor(new TicketReadClosedCommand(this))
                        .permission(TicketPermissions.COMMAND_TICKET_READ_ALL)
                        .build();

        // /ticket read held
        CommandSpec readHeld =
                CommandSpec.builder()
                        .description(Text.of("Display a list of all held tickets"))
                        .executor(new TicketReadHeldCommand(this))
                        .permission(TicketPermissions.COMMAND_TICKET_READ_ALL)
                        .build();

        // /ticket read (ticketID)
        CommandSpec ticketRead =
                CommandSpec.builder()
                        .description(Text.of("Read all ticket or give more detail of a specific ticket"))
                        .executor(new TicketReadCommand(this))
                        .child(readClosed, "closed")
                        .child(readHeld, "held")
                        .child(readSelf, "self")
                        .arguments(GenericArguments.optional(GenericArguments.integer(Text.of("ticketID"))))
                        .build();

        // /ticket close (ticketID) (comment)
        CommandSpec ticketClose =
                CommandSpec.builder()
                        .description(Text.of("Close a ticket"))
                        .executor(new TicketCloseCommand(this))
                        .arguments(
                                GenericArguments.integer(Text.of("ticketID")),
                                GenericArguments.optional(
                                        GenericArguments.remainingJoinedStrings(Text.of("comment"))))
                        .build();

        // /ticket open
        CommandSpec ticketOpen =
                CommandSpec.builder()
                        .description(Text.of("Open a ticket"))
                        .executor(new TicketOpenCommand(this))
                        .arguments(GenericArguments.remainingJoinedStrings(Text.of("message")))
                        .permission(TicketPermissions.COMMAND_TICKET_OPEN)
                        .build();

        // /ticket ban (username)
        CommandSpec ticketBan =
                CommandSpec.builder()
                        .description(Text.of("Ban a player from being able to create new tickets"))
                        .executor(new TicketBanCommand(this))
                        .arguments(GenericArguments.user(Text.of("playername")))
                        .permission(TicketPermissions.COMMAND_TICKET_BAN)
                        .build();

        // /ticket unban (username)
        CommandSpec ticketUnban =
                CommandSpec.builder()
                        .description(Text.of("Unban a player from being able to create new tickets"))
                        .executor(new TicketUnbanCommand(this))
                        .arguments(GenericArguments.user(Text.of("playername")))
                        .permission(TicketPermissions.COMMAND_TICKET_BAN)
                        .build();

        // /ticket reload
        CommandSpec ticketReload =
                CommandSpec.builder()
                        .description(Text.of("Reload ticket and player data."))
                        .executor(new TicketReloadCommand(this))
                        .permission(TicketPermissions.COMMAND_RELOAD)
                        .build();

        // /ticket claim (ticketID)
        CommandSpec ticketClaim =
                CommandSpec.builder()
                        .description(Text.of("Claim a ticket"))
                        .executor(new TicketClaimCommand(this))
                        .arguments(GenericArguments.integer(Text.of("ticketID")))
                        .permission(TicketPermissions.COMMAND_TICKET_CLAIM)
                        .build();

        // /ticket unclaim (ticketID)
        CommandSpec ticketUnclaim =
                CommandSpec.builder()
                        .description(Text.of("Unclaim a ticket"))
                        .executor(new TicketUnclaimCommand(this))
                        .arguments(GenericArguments.integer(Text.of("ticketID")))
                        .permission(TicketPermissions.COMMAND_TICKET_UNCLAIM)
                        .build();

        // /ticket reopen (ticketID)
        CommandSpec ticketReopen =
                CommandSpec.builder()
                        .description(Text.of("Reopen a ticket"))
                        .executor(new TicketReopenCommand(this))
                        .arguments(GenericArguments.integer(Text.of("ticketID")))
                        .permission(TicketPermissions.COMMAND_TICKET_REOPEN)
                        .build();

        // /ticket assign (ticketID) (player)
        CommandSpec ticketAssign =
                CommandSpec.builder()
                        .description(Text.of("Unclaim a ticket"))
                        .executor(new TicketAssignCommand(this))
                        .arguments(
                                GenericArguments.integer(Text.of("ticketID")),
                                GenericArguments.user(Text.of("player")))
                        .permission(TicketPermissions.COMMAND_TICKET_ASSIGN)
                        .build();

        // /ticket hold (ticketID)
        CommandSpec ticketHold =
                CommandSpec.builder()
                        .description(Text.of("Put a ticket on hold"))
                        .executor(new TicketHoldCommand(this))
                        .arguments(GenericArguments.integer(Text.of("ticketID")))
                        .permission(TicketPermissions.COMMAND_TICKET_HOLD)
                        .build();

        // /ticket comment (ticketID) (comment)
        CommandSpec ticketComment =
                CommandSpec.builder()
                        .description(Text.of("Open a ticket"))
                        .executor(new TicketCommentCommand(this))
                        .arguments(
                                GenericArguments.integer(Text.of("ticketID")),
                                GenericArguments.remainingJoinedStrings(Text.of("comment")))
                        .permission(TicketPermissions.COMMAND_TICKET_COMMENT)
                        .build();

        // /ticket teleport (ticketID)
        CommandSpec ticketTeleport =
                CommandSpec.builder()
                        .description(Text.of("Teleport to a ticket"))
                        .executor(new TicketTeleportCommand(this))
                        .arguments(GenericArguments.integer(Text.of("ticketID")))
                        .permission(TicketPermissions.COMMAND_TICKET_TELEPORT)
                        .build();

        // /ticket reject (ticketID)
        CommandSpec ticketReject =
                CommandSpec.builder()
                        .description(Text.of("Reject a ticket"))
                        .executor(new TicketRejectCommand(this))
                        .arguments(
                                GenericArguments.integer(Text.of("ticketID")),
                                GenericArguments.optional(
                                        GenericArguments.remainingJoinedStrings(Text.of("comment"))))
                        .permission(TicketPermissions.COMMAND_TICKET_CLOSE_ALL)
                        .build();

        // /ticket
        CommandSpec ticketBase =
                CommandSpec.builder()
                        .description(Text.of("Ticket base command, Displays help"))
                        .executor(new TicketCommand(this))
                        .child(ticketOpen, "open")
                        .child(ticketRead, "read", "check")
                        .child(ticketClose, "close", "complete")
                        .child(ticketBan, "ban")
                        .child(ticketUnban, "unban")
                        .child(ticketReload, "reload")
                        .child(ticketClaim, "claim")
                        .child(ticketUnclaim, "unclaim")
                        .child(ticketReopen, "reopen")
                        .child(ticketAssign, "assign")
                        .child(ticketHold, "hold")
                        .child(ticketComment, "comment")
                        .child(ticketTeleport, "teleport", "tp")
                        .child(ticketReject, "reject")
                        .build();

        cmdManager.register(this, ticketOpen, "modreq");
        cmdManager.register(this, ticketRead, "check");
        cmdManager.register(this, ticketBase, "ticket");
        cmdManager.register(this, staffList, "stafflist");
    }

    public Logger getLogger() {
        return logger;
    }

    public IDataStore getDataStore() {
        return dataStoreManager.getDataStore();
    }

    public void nagTimer() {
        if (TicketConfig.nagTimer > 0) {
            Sponge.getScheduler()
                    .createSyncExecutor(this)
                    .scheduleWithFixedDelay(
                            new Runnable() {
                                @Override
                                public void run() {
                                    final List<TicketData> tickets =
                                            new ArrayList<TicketData>(getDataStore().getTicketData());
                                    int openTickets = 0;
                                    int heldTickets = 0;
                                    for (TicketData ticket : tickets) {
                                        if (ticket.getStatus() == Open || ticket.getStatus() == Claimed) {
                                            openTickets++;
                                        }
                                        if (ticket.getStatus() == Held) {
                                            heldTickets++;
                                        }
                                    }
                                    if (TicketConfig.nagHeld) {
                                        if (heldTickets > 0) {
                                            if (openTickets > 0) {
                                                TicketUtil.notifyOnlineStaff(
                                                        TicketMessages.getTicketUnresolvedHeld(openTickets, heldTickets, "check"));
                                            }
                                        } else {
                                            if (openTickets > 0) {
                                                TicketUtil.notifyOnlineStaff(
                                                        TicketMessages.getTicketUnresolved(openTickets, "check"));
                                            }
                                        }
                                    } else {
                                        if (openTickets > 0) {
                                            TicketUtil.notifyOnlineStaff(
                                                    TicketMessages.getTicketUnresolved(openTickets, "check"));
                                        }
                                    }
                                }
                            },
                            TicketConfig.nagTimer,
                            TicketConfig.nagTimer,
                            TimeUnit.MINUTES);
        }
    }

    public ArrayList<String> getWaitTimer() {
        return this.waitTimer;
    }

    public Text fromLegacy(String legacy) {
        return TextSerializers.FORMATTING_CODE.deserializeUnchecked(legacy);
    }

    @Deprecated
    public List<TicketData> getTickets() {
        return getDataStore().getTicketData();
    }

    @Deprecated
    public TicketData getTicket(int ticketID) {
        if (getDataStore().getTicket(ticketID).isPresent()) {
            return getDataStore().getTicket(ticketID).get();
        }
        return null;
    }
}
