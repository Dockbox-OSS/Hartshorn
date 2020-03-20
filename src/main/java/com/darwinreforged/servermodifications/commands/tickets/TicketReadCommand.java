package com.darwinreforged.servermodifications.commands.tickets;

import com.darwinreforged.servermodifications.objects.TicketData;
import com.darwinreforged.servermodifications.permissions.TicketPermissions;
import com.darwinreforged.servermodifications.plugins.TicketPlugin;
import com.darwinreforged.servermodifications.translations.TicketMessages;
import com.darwinreforged.servermodifications.translations.Translations;
import com.darwinreforged.servermodifications.util.PlayerUtils;
import com.darwinreforged.servermodifications.util.plugins.TicketUtil;
import com.darwinreforged.servermodifications.util.todo.config.TicketConfig;
import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static com.darwinreforged.servermodifications.objects.TicketStatus.*;

public class TicketReadCommand implements CommandExecutor {

    private final TicketPlugin plugin;

    public TicketReadCommand(TicketPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        final Optional<Integer> ticketIDOp = args.<Integer>getOne("ticketID");

        final List<TicketData> tickets =
                new ArrayList<TicketData>(plugin.getDataStore().getTicketData());

        UUID uuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
        if (src instanceof Player) {
            Player player = (Player) src;
            uuid = player.getUniqueId();
        }

        if (tickets.isEmpty()) {
            throw new CommandException(Translations.UNKNOWN_ERROR.ft("Tickets list is empty."));
        } else {
            if (!ticketIDOp.isPresent()) {
                if (src.hasPermission(TicketPermissions.COMMAND_TICKET_READ_ALL)) {
                    PaginationService paginationService =
                            Sponge.getServiceManager().provide(PaginationService.class).get();
                    List<Text> contents = new ArrayList<>();
                    int totalTickets = 0;
                    for (TicketData ticket : tickets) {
                        if (TicketConfig.hideOffline) {
                            if (PlayerUtils.isUserOnline(
                                    TicketUtil.getPlayerNameFromData(plugin, ticket.getPlayerUUID()))) {
                                if (ticket.getStatus() == Claimed || ticket.getStatus() == Open) {
                                    String online = PlayerUtils.isUserOnline(ticket.getPlayerUUID()) ? "&b" : "&3";
                                    totalTickets++;
                                    Text.Builder send = Text.builder();
                                    String status = "";
                                    if (ticket.getStatus() == Claimed) status = "&bClaimed - ";
                                    send.append(
                                            plugin.fromLegacy(
                                                    status
                                                            + "&3#"
                                                            + ticket.getTicketID()
                                                            + " "
                                                            + TicketUtil.getTimeAgo(ticket.getTimestamp())
                                                            + " by "
                                                            + online
                                                            + TicketUtil.getPlayerNameFromData(plugin, ticket.getPlayerUUID())
                                                            + " &3on "
                                                            + TicketUtil.getServerFormatted(ticket.getServer())
                                                            + " &3- &7"
                                                            + Translations.shorten(ticket.getMessage())));
                                    send.onClick(TextActions.runCommand("/ticket read " + ticket.getTicketID()));
                                    send.onHover(
                                            TextActions.showText(
                                                    plugin.fromLegacy(
                                                            "Click here to get more details for ticket #"
                                                                    + ticket.getTicketID())));
                                    contents.add(send.build());
                                }
                            }
                        } else {
                            if (ticket.getStatus() == Claimed || ticket.getStatus() == Open) {
                                String online = PlayerUtils.isUserOnline(ticket.getPlayerUUID()) ? "&b" : "&3";
                                totalTickets++;
                                Text.Builder send = Text.builder();
                                String status = "";
                                if (ticket.getStatus() == Claimed) status = "&bClaimed - ";
                                send.append(
                                        plugin.fromLegacy(
                                                status
                                                        + "&3#"
                                                        + ticket.getTicketID()
                                                        + " "
                                                        + TicketUtil.getTimeAgo(ticket.getTimestamp())
                                                        + " by "
                                                        + online
                                                        + TicketUtil.getPlayerNameFromData(plugin, ticket.getPlayerUUID())
                                                        + " &3on "
                                                        + TicketUtil.getServerFormatted(ticket.getServer())
                                                        + " &3- &7"
                                                        + Translations.shorten(ticket.getMessage())));
                                send.onClick(TextActions.runCommand("/ticket read " + ticket.getTicketID()));
                                send.onHover(
                                        TextActions.showText(
                                                plugin.fromLegacy(
                                                        "Click here to get more details for ticket #" + ticket.getTicketID())));
                                contents.add(send.build());
                            }
                        }
                    }

                    if (contents.isEmpty()) {
                        contents.add(TicketMessages.getTicketReadNone());
                    }
                    int ticketsPer = 5;
                    if (TicketConfig.ticketsPerPage > 0) {
                        ticketsPer = TicketConfig.ticketsPerPage;
                    }
                    paginationService
                            .builder()
                            .title(plugin.fromLegacy("&3" + totalTickets + " Open Tickets"))
                            .contents(contents)
                            .padding(Text.of("-"))
                            .linesPerPage(ticketsPer + 2)
                            .sendTo(src);
                    return CommandResult.success();
                } else {
                    if (src.hasPermission(TicketPermissions.COMMAND_TICKET_READ_SELF)) {
                        throw new CommandException(Translations.TICKET_ERROR_INCORRECT_USAGE.t());
                    } else {
                        throw new CommandException(
                                Translations.TICKET_ERROR_PERMISSION.ft(TicketPermissions.COMMAND_TICKET_READ_ALL));
                    }
                }
            } else {
                if (src.hasPermission(TicketPermissions.COMMAND_TICKET_READ_ALL)
                        || (src.hasPermission(TicketPermissions.COMMAND_TICKET_READ_SELF))) {
                    PaginationService paginationService =
                            Sponge.getServiceManager().provide(PaginationService.class).get();
                    List<Text> contents = new ArrayList<>();
                    int ticketID = ticketIDOp.get();
                    String ticketStatus = "";
                    for (TicketData ticket : tickets) {
                        if (ticket.getTicketID() == ticketID) {
                            if (!ticket.getPlayerUUID().equals(uuid)
                                    && !src.hasPermission(TicketPermissions.COMMAND_TICKET_READ_ALL)) {
                                throw new CommandException(TicketMessages.getErrorTicketOwner());
                            }
                            ticketStatus = TicketUtil.getTicketStatusColour(ticket.getStatus());
                            String online = PlayerUtils.isUserOnline(ticket.getPlayerUUID()) ? "&b" : "&3";
                            Optional<World> worldOptional = Sponge.getServer().getWorld(ticket.getWorld());

                            Text.Builder action = Text.builder();

                            if (ticket.getStatus() == Open || ticket.getStatus() == Claimed) {
                                if (ticket.getStatus() == Open
                                        && src.hasPermission(TicketPermissions.COMMAND_TICKET_CLAIM)) {
                                    action.append(
                                            Text.builder()
                                                    .append(plugin.fromLegacy(TicketMessages.getClaimButton()))
                                                    .onHover(
                                                            TextActions.showText(
                                                                    plugin.fromLegacy(TicketMessages.getClaimButtonHover())))
                                                    .onClick(TextActions.runCommand("/ticket claim " + ticket.getTicketID()))
                                                    .build());
                                    action.append(plugin.fromLegacy(" "));
                                }
                                if (ticket.getStatus() == Claimed) {
                                    if (ticket.getStaffUUID().equals(uuid)
                                            && src.hasPermission(TicketPermissions.COMMAND_TICKET_UNCLAIM)) {
                                        action.append(
                                                Text.builder()
                                                        .append(plugin.fromLegacy(TicketMessages.getUnclaimButton()))
                                                        .onHover(
                                                                TextActions.showText(
                                                                        plugin.fromLegacy(TicketMessages.getUnclaimButtonHover())))
                                                        .onClick(
                                                                TextActions.runCommand("/ticket unclaim " + ticket.getTicketID()))
                                                        .build());
                                        action.append(plugin.fromLegacy(" "));
                                    }
                                }
                                if ((ticket.getStatus() == Open
                                        || ticket.getStatus() == Claimed && ticket.getStaffUUID().equals(uuid))
                                        && src.hasPermission(TicketPermissions.COMMAND_TICKET_HOLD)) {
                                    action.append(
                                            Text.builder()
                                                    .append(plugin.fromLegacy(TicketMessages.getHoldButton()))
                                                    .onHover(
                                                            TextActions.showText(
                                                                    plugin.fromLegacy(TicketMessages.getHoldButtonHover())))
                                                    .onClick(TextActions.runCommand("/ticket hold " + ticket.getTicketID()))
                                                    .build());
                                    action.append(plugin.fromLegacy(" "));
                                }
                            }
                            if (ticket.getStatus() == Held || ticket.getStatus() == Closed) {
                                if (src.hasPermission(TicketPermissions.COMMAND_TICKET_REOPEN)) {
                                    action.append(
                                            Text.builder()
                                                    .append(plugin.fromLegacy(TicketMessages.getReopenButton()))
                                                    .onHover(
                                                            TextActions.showText(
                                                                    plugin.fromLegacy(TicketMessages.getReopenButtonHover())))
                                                    .onClick(TextActions.runCommand("/ticket reopen " + ticket.getTicketID()))
                                                    .build());
                                    action.append(plugin.fromLegacy(" "));
                                }
                            }
                            if (ticket.getStatus() == Held
                                    || ticket.getStatus() == Claimed
                                    || ticket.getStatus() == Open) {
                                if ((ticket.getStatus() == Claimed && ticket.getStaffUUID().equals(uuid))
                                        || ticket.getStatus() == Open
                                        || ticket.getStatus() == Held) {
                                    if (src.hasPermission(TicketPermissions.COMMAND_TICKET_CLOSE_ALL)
                                            || src.hasPermission(TicketPermissions.COMMAND_TICKET_CLOSE_SELF)) {

                                        if (ticket.getWorld().equals("Plots1")) {
                                            action.append(
                                                    Text.builder()
                                                            .append(
                                                                    Text.of(
                                                                            TextColors.GRAY,
                                                                            "[",
                                                                            TextColors.GREEN,
                                                                            "Promote - Member",
                                                                            TextColors.GRAY,
                                                                            "]"))
                                                            .onHover(
                                                                    TextActions.showText(
                                                                            Text.of(
                                                                                    TextColors.AQUA, "Promote to Member and close ticket")))
                                                            .onClick(TextActions.runCommand("/multi ticket complete " + ticket.getTicketID() + "|promote " + TicketUtil.getPlayerNameFromData(plugin, ticket.getPlayerUUID()) + " Member"))
                                                            .build());
                                        } else if (ticket.getWorld().equals("Plots2")) {
                                            action.append(
                                                    Text.builder()
                                                            .append(
                                                                    Text.of(
                                                                            TextColors.GRAY,
                                                                            "[",
                                                                            TextColors.YELLOW,
                                                                            "Promote - Expert",
                                                                            TextColors.GRAY,
                                                                            "]"))
                                                            .onHover(
                                                                    TextActions.showText(
                                                                            Text.of(
                                                                                    TextColors.AQUA, "Promote to Expert and close ticket")))
                                                            .onClick(TextActions.runCommand("/multi ticket complete " + ticket.getTicketID() + "|promote " + TicketUtil.getPlayerNameFromData(plugin, ticket.getPlayerUUID()) + " Expert"))
                                                            .build());
                                        } else if (ticket.getWorld().equals("MasterPlots")) {
                                            action.append(Text.NEW_LINE);
                                            action.append(
                                                    Text.builder()
                                                            .append(
                                                                    Text.of(
                                                                            TextColors.GRAY,
                                                                            "[",
                                                                            TextColors.AQUA,
                                                                            "Promote - MS:Nature",
                                                                            TextColors.GRAY,
                                                                            "]"))
                                                            .onHover(
                                                                    TextActions.showText(
                                                                            Text.of(
                                                                                    TextColors.AQUA, "Promote to Mastered Skill Nature and close ticket")))
                                                            .onClick(TextActions.runCommand("/multi ticket complete " + ticket.getTicketID() + "|master " + TicketUtil.getPlayerNameFromData(plugin, ticket.getPlayerUUID()) + " nat"))
                                                            .build());
                                            action.append(plugin.fromLegacy(" "));
                                            action.append(
                                                    Text.builder()
                                                            .append(
                                                                    Text.of(
                                                                            TextColors.GRAY,
                                                                            "[",
                                                                            TextColors.AQUA,
                                                                            "Promote - MS:Architecture",
                                                                            TextColors.GRAY,
                                                                            "]"))
                                                            .onHover(
                                                                    TextActions.showText(
                                                                            Text.of(
                                                                                    TextColors.AQUA, "Promote to Mastered Skill Architecture and close ticket")))
                                                            .onClick(TextActions.runCommand("/multi ticket complete " + ticket.getTicketID() + "|master " + TicketUtil.getPlayerNameFromData(plugin, ticket.getPlayerUUID()) + " arch"))
                                                            .build());
                                            action.append(plugin.fromLegacy(" "));
                                            action.append(
                                                    Text.builder()
                                                            .append(
                                                                    Text.of(
                                                                            TextColors.GRAY,
                                                                            "[",
                                                                            TextColors.AQUA,
                                                                            "Promote - MS:Both",
                                                                            TextColors.GRAY,
                                                                            "]"))
                                                            .onHover(
                                                                    TextActions.showText(
                                                                            Text.of(
                                                                                    TextColors.AQUA, "Promote to both Mastered Skills and close ticket")))
                                                            .onClick(TextActions.runCommand("/multi ticket complete " + ticket.getTicketID() + "|master " + TicketUtil.getPlayerNameFromData(plugin, ticket.getPlayerUUID()) + " all"))
                                                            .build());
                                            action.append(Text.NEW_LINE);
                                        }
                                        action.append(plugin.fromLegacy(" "));
                                    }
                                }
                            }
                            if (ticket.getStatus() == Held
                                    || ticket.getStatus() == Claimed
                                    || ticket.getStatus() == Open) {
                                if (ticket.getStatus() == Held
                                        || ticket.getStatus() == Claimed
                                        || ticket.getStatus() == Open) {
                                    if ((ticket.getStatus() == Claimed && ticket.getStaffUUID().equals(uuid))
                                            || ticket.getStatus() == Open
                                            || ticket.getStatus() == Held) {
                                        if (src.hasPermission(TicketPermissions.COMMAND_TICKET_CLOSE_ALL)
                                                || src.hasPermission(TicketPermissions.COMMAND_TICKET_CLOSE_SELF)) {
                                            action.append(
                                                    Text.builder()
                                                            .append(plugin.fromLegacy(TicketMessages.getRejectButton()))
                                                            .onHover(
                                                                    TextActions.showText(
                                                                            plugin.fromLegacy(TicketMessages.getRejectButtonHover())))
                                                            .onClick(
                                                                    TextActions.runCommand("/ticket reject " + ticket.getTicketID()))
                                                            .build());
                                            action.append(plugin.fromLegacy(" "));
                                        }
                                    }
                                }
                            }
                            if (ticket.getComment().isEmpty()
                                    && src.hasPermission(TicketPermissions.COMMAND_TICKET_COMMENT)) {
                                if (ticket.getStatus() != Claimed
                                        || ticket.getStatus() == Claimed && ticket.getStaffUUID().equals(uuid)) {
                                    action.append(
                                            Text.builder()
                                                    .append(plugin.fromLegacy(TicketMessages.getCommentButton()))
                                                    .onHover(
                                                            TextActions.showText(
                                                                    plugin.fromLegacy(TicketMessages.getCommentButtonHover())))
                                                    .onClick(
                                                            TextActions.suggestCommand(
                                                                    "/ticket comment " + ticket.getTicketID() + " "))
                                                    .build());
                                }
                            }

                            Text.Builder send = Text.builder();
                            send.append(
                                    Text.of(
                                            TextColors.AQUA,
                                            "[",
                                            TextColors.DARK_AQUA,
                                            "Teleport",
                                            TextColors.AQUA,
                                            "]"));
                            if (src.hasPermission(TicketPermissions.COMMAND_TICKET_TELEPORT)
                                    && ticket.getServer().equalsIgnoreCase(TicketConfig.server)) {
                                send.onHover(TextActions.showText(TicketMessages.getTicketOnHoverTeleportTo()));
                                worldOptional.ifPresent(
                                        world ->
                                                send.onClick(
                                                        TextActions.executeCallback(
                                                                teleportTo(
                                                                        world,
                                                                        ticket.getX(),
                                                                        ticket.getY(),
                                                                        ticket.getZ(),
                                                                        ticket.getPitch(),
                                                                        ticket.getYaw(),
                                                                        ticketID))));
                            }

                            if (!action.build().isEmpty()) {
                                contents.add(action.build());
                            }
                            if (!ticket
                                    .getStaffUUID()
                                    .toString()
                                    .equals("00000000-0000-0000-0000-000000000000")) {
                                if (ticket.getStatus() == Claimed)
                                    contents.add(
                                            plugin.fromLegacy(
                                                    "&bClaimed by: &7"
                                                            + TicketUtil.getPlayerNameFromData(plugin, ticket.getStaffUUID())));
                                else if (ticket.getStatus() == Closed)
                                    contents.add(
                                            plugin.fromLegacy(
                                                    "&bHandled by: &7"
                                                            + TicketUtil.getPlayerNameFromData(plugin, ticket.getStaffUUID())));
                            }
                            if (!ticket.getComment().isEmpty()) {
                                contents.add(plugin.fromLegacy("&bComment: &7" + ticket.getComment()));
                            }

                            int ticketNum =
                                    (int)
                                            tickets.stream()
                                                    .filter(
                                                            t ->
                                                                    t.getPlayerUUID()
                                                                            .toString()
                                                                            .equals(ticket.getPlayerUUID().toString())
                                                                            && t.getMessage().equals(ticket.getMessage()))
                                                    .count();

                            contents.add(
                                    Text.of(
                                            plugin.fromLegacy(
                                                    "&bOpened by: "
                                                            + online
                                                            + TicketUtil.getPlayerNameFromData(plugin, ticket.getPlayerUUID())),
                                            TextColors.DARK_AQUA,
                                            " | Submission : #" + ticketNum));

                            contents.add(
                                    plugin.fromLegacy("&bWhen: " + TicketUtil.getTimeAgo(ticket.getTimestamp())));

                            contents.add(
                                    plugin.fromLegacy(
                                            "&bServer: " + TicketUtil.getServerFormatted(ticket.getServer())));
                            if (!ticket
                                    .getPlayerUUID()
                                    .toString()
                                    .equals("00000000-0000-0000-0000-000000000000")) {
                                contents.add(send.build());
                            }
                            contents.add(plugin.fromLegacy("&7" + ticket.getMessage()));
                        }
                    }

                    if (contents.isEmpty()) {
                        throw new CommandException(TicketMessages.getTicketNotExist(ticketID));
                    }

                    paginationService
                            .builder()
                            .title(plugin.fromLegacy("&3Request #" + ticketID + " &b- &3" + ticketStatus))
                            .contents(contents)
                            .padding(plugin.fromLegacy("&b-"))
                            .sendTo(src);
                    return CommandResult.success();
                } else {
                    throw new CommandException(
                            Translations.TICKET_ERROR_PERMISSION.ft(TicketPermissions.COMMAND_TICKET_READ_SELF));
                }
            }
        }
    }

    private Consumer<CommandSource> teleportTo(
            World world, int x, int y, int z, double pitch, double yaw, int ticketID) {
        return consumer -> {
            Player player = (Player) consumer;

            Location<World> location = new Location<>(world, x, y, z);
            Vector3i chunkPosition = location.getChunkPosition();

            if (!world.isLoaded()) world.loadChunk(chunkPosition, true);

            Vector3d playerRotation = new Vector3d(pitch, yaw, 0);
            player.setLocationAndRotation(location, playerRotation);
            player.sendMessage(TicketMessages.getTeleportToTicket(ticketID));
        };
    }
}
