package com.darwinreforged.servermodifications.modules;

import com.darwinreforged.servermodifications.DarwinServer;
import com.darwinreforged.servermodifications.commands.tickets.TicketAssignCommand;
import com.darwinreforged.servermodifications.commands.tickets.TicketBanCommand;
import com.darwinreforged.servermodifications.commands.tickets.TicketClaimCommand;
import com.darwinreforged.servermodifications.commands.tickets.TicketCloseCommand;
import com.darwinreforged.servermodifications.commands.tickets.TicketCommand;
import com.darwinreforged.servermodifications.commands.tickets.TicketCommentCommand;
import com.darwinreforged.servermodifications.commands.tickets.TicketHoldCommand;
import com.darwinreforged.servermodifications.commands.tickets.TicketOpenCommand;
import com.darwinreforged.servermodifications.commands.tickets.TicketReadClosedCommand;
import com.darwinreforged.servermodifications.commands.tickets.TicketReadCommand;
import com.darwinreforged.servermodifications.commands.tickets.TicketReadHeldCommand;
import com.darwinreforged.servermodifications.commands.tickets.TicketReadSelfCommand;
import com.darwinreforged.servermodifications.commands.tickets.TicketRejectCommand;
import com.darwinreforged.servermodifications.commands.tickets.TicketReloadCommand;
import com.darwinreforged.servermodifications.commands.tickets.TicketReopenCommand;
import com.darwinreforged.servermodifications.commands.tickets.TicketTeleportCommand;
import com.darwinreforged.servermodifications.commands.tickets.TicketUnbanCommand;
import com.darwinreforged.servermodifications.commands.tickets.TicketUnclaimCommand;
import com.darwinreforged.servermodifications.listeners.TicketLoginAndDiscordListener;
import com.darwinreforged.servermodifications.modules.root.DisabledModule;
import com.darwinreforged.servermodifications.modules.root.ModuleInfo;
import com.darwinreforged.servermodifications.modules.root.PluginModule;
import com.darwinreforged.servermodifications.objects.TicketData;
import com.darwinreforged.servermodifications.objects.TicketPlayerData;
import com.darwinreforged.servermodifications.resources.Permissions;
import com.darwinreforged.servermodifications.resources.Translations;
import com.darwinreforged.servermodifications.util.PlayerUtils;
import com.darwinreforged.servermodifications.util.todo.config.TicketConfig;
import com.darwinreforged.servermodifications.util.todo.database.DataStoreManager;
import com.darwinreforged.servermodifications.util.todo.database.IDataStore;
import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameAboutToStartServerEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.text.Text;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;

import static com.darwinreforged.servermodifications.objects.TicketStatus.Claimed;
import static com.darwinreforged.servermodifications.objects.TicketStatus.Held;
import static com.darwinreforged.servermodifications.objects.TicketStatus.Open;


@ModuleInfo(
        id = "mmctickets",
        name = "MMCTickets",
        version = "2.0.7",
        description = "A real time ticket system")
@DisabledModule("Injection dependency")
public class TicketModule extends PluginModule {

    // TODO : Remove injection requirement
    @Inject
    @DefaultConfig(sharedRoot = false)
    public Path defaultConf;

    public TicketConfig config;

    private CommandManager cmdManager = Sponge.getCommandManager();

    private ArrayList<String> waitTimer;
    private DataStoreManager dataStoreManager;

    public TicketModule() {
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void onServerFinishLoad(GameInitializationEvent event) {
        DarwinServer.registerListener(new TicketLoginAndDiscordListener(this));

        TypeSerializers.getDefaultSerializers()
                .registerType(TypeToken.of(TicketData.class), new TicketData.TicketSerializer());
        TypeSerializers.getDefaultSerializers()
                .registerType(TypeToken
                        .of(TicketPlayerData.class), new TicketPlayerData.TicketPlayerDataSerializer());

        try {
            config = new TicketConfig(this);
            loadCommands();
        } catch (IOException | ObjectMappingException e) {
            e.printStackTrace();
        }
    }

    @Listener
    public void onServerAboutStart(GameAboutToStartServerEvent event) {
        dataStoreManager = new DataStoreManager();
        if (dataStoreManager.load()) {
            DarwinServer.getLogger().info("MMCTickets datastore Loaded");
        } else {
            DarwinServer.getLogger().error("Unable to load a datastore please check your Console/Config!");
        }
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        DarwinServer.getLogger().info("MMCTickets Loaded");
        DarwinServer.getLogger().info("Tickets loaded: " + getDataStore().getTicketData().size());
        DarwinServer.getLogger().info("Notifications loaded: " + getDataStore().getNotifications().size());
        DarwinServer.getLogger().info("PlayerData loaded: " + getDataStore().getPlayerData().size());

        this.waitTimer = new ArrayList<String>();

        // start ticket nag timer
        nagTimer();
    }

    @Listener
    public void onPluginReload(GameReloadEvent event) throws IOException, ObjectMappingException {
        this.config = new TicketConfig(this);
        dataStoreManager = new DataStoreManager();
        loadDataStore();
    }

    public void loadDataStore() {
        if (dataStoreManager.load()) {
            DarwinServer.getLogger().info("MMCTickets datastore Loaded");
        } else {
            DarwinServer.getLogger().error("Unable to load a datastore please check your Console/Config!");
        }
    }

    public void setDataStoreManager(DataStoreManager dataStoreManager) {
        this.dataStoreManager = dataStoreManager;
    }

    private void loadCommands() {
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
                        .permission(Permissions.COMMAND_TICKET_READ_ALL.p())
                        .build();

        // /ticket read held
        CommandSpec readHeld =
                CommandSpec.builder()
                        .description(Text.of("Display a list of all held tickets"))
                        .executor(new TicketReadHeldCommand(this))
                        .permission(Permissions.COMMAND_TICKET_READ_ALL.p())
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
                        .permission(Permissions.COMMAND_TICKET_OPEN.p())
                        .build();

        // /ticket ban (username)
        CommandSpec ticketBan =
                CommandSpec.builder()
                        .description(Text.of("Ban a player from being able to create new tickets"))
                        .executor(new TicketBanCommand(this))
                        .arguments(GenericArguments.user(Text.of("playername")))
                        .permission(Permissions.COMMAND_TICKET_BAN.p())
                        .build();

        // /ticket unban (username)
        CommandSpec ticketUnban =
                CommandSpec.builder()
                        .description(Text.of("Unban a player from being able to create new tickets"))
                        .executor(new TicketUnbanCommand(this))
                        .arguments(GenericArguments.user(Text.of("playername")))
                        .permission(Permissions.COMMAND_TICKET_BAN.p())
                        .build();

        // /ticket reload
        CommandSpec ticketReload =
                CommandSpec.builder()
                        .description(Text.of("Reload ticket and player data."))
                        .executor(new TicketReloadCommand(this))
                        .permission(Permissions.COMMAND_TICKET_RELOAD.p())
                        .build();

        // /ticket claim (ticketID)
        CommandSpec ticketClaim =
                CommandSpec.builder()
                        .description(Text.of("Claim a ticket"))
                        .executor(new TicketClaimCommand(this))
                        .arguments(GenericArguments.integer(Text.of("ticketID")))
                        .permission(Permissions.COMMAND_TICKET_CLAIM.p())
                        .build();

        // /ticket unclaim (ticketID)
        CommandSpec ticketUnclaim =
                CommandSpec.builder()
                        .description(Text.of("Unclaim a ticket"))
                        .executor(new TicketUnclaimCommand(this))
                        .arguments(GenericArguments.integer(Text.of("ticketID")))
                        .permission(Permissions.COMMAND_TICKET_UNCLAIM.p())
                        .build();

        // /ticket reopen (ticketID)
        CommandSpec ticketReopen =
                CommandSpec.builder()
                        .description(Text.of("Reopen a ticket"))
                        .executor(new TicketReopenCommand(this))
                        .arguments(GenericArguments.integer(Text.of("ticketID")))
                        .permission(Permissions.COMMAND_TICKET_REOPEN.p())
                        .build();

        // /ticket assign (ticketID) (player)
        CommandSpec ticketAssign =
                CommandSpec.builder()
                        .description(Text.of("Unclaim a ticket"))
                        .executor(new TicketAssignCommand(this))
                        .arguments(
                                GenericArguments.integer(Text.of("ticketID")),
                                GenericArguments.user(Text.of("player")))
                        .permission(Permissions.COMMAND_TICKET_ASSIGN.p())
                        .build();

        // /ticket hold (ticketID)
        CommandSpec ticketHold =
                CommandSpec.builder()
                        .description(Text.of("Put a ticket on hold"))
                        .executor(new TicketHoldCommand(this))
                        .arguments(GenericArguments.integer(Text.of("ticketID")))
                        .permission(Permissions.COMMAND_TICKET_HOLD.p())
                        .build();

        // /ticket comment (ticketID) (comment)
        CommandSpec ticketComment =
                CommandSpec.builder()
                        .description(Text.of("Open a ticket"))
                        .executor(new TicketCommentCommand(this))
                        .arguments(
                                GenericArguments.integer(Text.of("ticketID")),
                                GenericArguments.remainingJoinedStrings(Text.of("comment")))
                        .permission(Permissions.COMMAND_TICKET_COMMENT.p())
                        .build();

        // /ticket teleport (ticketID)
        CommandSpec ticketTeleport =
                CommandSpec.builder()
                        .description(Text.of("Teleport to a ticket"))
                        .executor(new TicketTeleportCommand(this))
                        .arguments(GenericArguments.integer(Text.of("ticketID")))
                        .permission(Permissions.COMMAND_TICKET_TELEPORT.p())
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
                        .permission(Permissions.COMMAND_TICKET_CLOSE_ALL.p())
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
    }

    public IDataStore getDataStore() {
        return dataStoreManager.getDataStore();
    }

    public void nagTimer() {
        if (TicketConfig.nagTimer > 0) {
            Sponge.getScheduler()
                    .createSyncExecutor(this)
                    .scheduleWithFixedDelay(
                            () -> {
                                final List<TicketData> tickets =
                                        new ArrayList<>(getDataStore().getTicketData());
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
                                            PlayerUtils.broadcastForPermission(Translations.TICKET_UNRESOLVED_HELD.ft(openTickets, heldTickets, "check"), Permissions.TICKET_STAFF.p());
                                        }
                                    } else {
                                        if (openTickets > 0) {
                                            PlayerUtils.broadcastForPermission(Translations.TICKET_UNRESOLVED.ft(openTickets, "check"), Permissions.TICKET_STAFF.p());
                                        }
                                    }
                                } else {
                                    if (openTickets > 0) {
                                        PlayerUtils.broadcastForPermission(Translations.TICKET_UNRESOLVED.ft(openTickets, "check"), Permissions.TICKET_STAFF.p());
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
