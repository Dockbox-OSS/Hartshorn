package com.darwinreforged.servermodifications.commands.tickets;

import com.darwinreforged.servermodifications.objects.TicketData;
import com.darwinreforged.servermodifications.permissions.TicketPermissions;
import com.darwinreforged.servermodifications.plugins.TicketPlugin;
import com.darwinreforged.servermodifications.resources.Translations;
import com.darwinreforged.servermodifications.util.LocationUtils;
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
                    for (TicketData ticket : tickets) {
                        if (TicketConfig.hideOffline) {
                            if (PlayerUtils.isUserOnline(
                                    PlayerUtils.getSafely(PlayerUtils.getNameFromUUID(ticket.getPlayerUUID()))))
                                contents = getContentsForClaimedTicket(contents, ticket);
                        } else {
                            contents = getContentsForClaimedTicket(contents, ticket);
                        }
                    }

                    if (contents.isEmpty()) {
                        contents.add(Translations.TICKET_READ_NONE_OPEN.t());
                    }
                    int ticketsPer = 5;
                    if (TicketConfig.ticketsPerPage > 0) {
                        ticketsPer = TicketConfig.ticketsPerPage;
                    }
                    paginationService
                            .builder()
                            .title(Translations.TICKET_OPEN_TITLE.ft(tickets.size()))
                            .contents(contents)
                            .padding(Translations.DEFAULT_PADDING.t())
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
                                throw new CommandException(Translations.TICKET_ERROR_OWNER.t());
                            }
                            ticketStatus = TicketUtil.getTicketStatusColour(ticket.getStatus());
                            String online = PlayerUtils.isUserOnline(ticket.getPlayerUUID()) ? "&b" : "&3";
                            Optional<World> worldOptional = LocationUtils.getWorld(ticket.getWorld());

                            Text.Builder action = Text.builder();

                            if (ticket.getStatus() == Open || ticket.getStatus() == Claimed) {
                                if (ticket.getStatus() == Open
                                        && src.hasPermission(TicketPermissions.COMMAND_TICKET_CLAIM)) {
                                    action.append(
                                            Text.builder()
                                                    .append(Translations.TICKET_CLAIM_BUTTON.t())
                                                    .onHover(
                                                            TextActions.showText(Translations.TICKET_CLAIM_BUTTON_HOVER.t()))
                                                    .onClick(TextActions.runCommand("/ticket claim " + ticket.getTicketID()))
                                                    .build());
                                    action.append(Text.of(" "));
                                }
                                if (ticket.getStatus() == Claimed) {
                                    if (ticket.getStaffUUID().equals(uuid)
                                            && src.hasPermission(TicketPermissions.COMMAND_TICKET_UNCLAIM)) {
                                        action.append(
                                                Text.builder()
                                                        .append(Translations.TICKET_UNCLAIM_BUTTON.t())
                                                        .onHover(
                                                                TextActions.showText(Translations.TICKET_UNCLAIM_BUTTON_HOVER.t()))
                                                        .onClick(
                                                                TextActions.runCommand("/ticket unclaim " + ticket.getTicketID()))
                                                        .build());
                                        action.append(Text.of(" "));
                                    }
                                }
                                if ((ticket.getStatus() == Open
                                        || ticket.getStatus() == Claimed && ticket.getStaffUUID().equals(uuid))
                                        && src.hasPermission(TicketPermissions.COMMAND_TICKET_HOLD)) {
                                    action.append(
                                            Text.builder()
                                                    .append(Translations.TICKET_HOLD_BUTTON.t())
                                                    .onHover(
                                                            TextActions.showText(Translations.TICKET_HOLD_BUTTON_HOVER.t()))
                                                    .onClick(TextActions.runCommand("/ticket hold " + ticket.getTicketID()))
                                                    .build());
                                    action.append(Text.of(" "));
                                }
                            }
                            if (ticket.getStatus() == Held || ticket.getStatus() == Closed) {
                                if (src.hasPermission(TicketPermissions.COMMAND_TICKET_REOPEN)) {
                                    action.append(
                                            Text.builder()
                                                    .append(Translations.TICKET_REOPEN_BUTTON.t())
                                                    .onHover(
                                                            TextActions.showText(Translations.TICKET_REOPEN_BUTTON_HOVER.t()))
                                                    .onClick(TextActions.runCommand("/ticket reopen " + ticket.getTicketID()))
                                                    .build());
                                    action.append(Text.of(" "));
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

                                        if (ticket.getWorld().equals(Translations.PLOTS1_NAME.s())) {
                                            action.append(
                                                    Text.builder()
                                                            .append(Translations.PROMOTE_MEMBER_BUTTON.t())
                                                            .onHover(TextActions.showText(Translations.PROMOTE_BUTTON_HOVER.ft(Translations.MEMBER_RANK_DISPLAY.s())))
                                                            .onClick(TextActions.runCommand("/multi ticket complete " + ticket.getTicketID() + "|promote " + PlayerUtils.getSafely(PlayerUtils.getNameFromUUID(ticket.getPlayerUUID())) + " Member"))
                                                            .build());
                                        } else if (ticket.getWorld().equals(Translations.PLOTS2_NAME.s())) {
                                            action.append(
                                                    Text.builder()
                                                            .append(Translations.PROMOTE_EXPERT_BUTTON.t())
                                                            .onHover(TextActions.showText(Translations.PROMOTE_BUTTON_HOVER.ft(Translations.EXPERT_RANK_DISPLAY.s())))
                                                            .onClick(TextActions.runCommand("/multi ticket complete " + ticket.getTicketID() + "|promote " + PlayerUtils.getSafely(PlayerUtils.getNameFromUUID(ticket.getPlayerUUID())) + " Expert"))
                                                            .build());
                                        } else if (ticket.getWorld().equals(Translations.MASTERPLOTS_NAME.s())) {
                                            action.append(Text.NEW_LINE);
                                            action.append(
                                                    Text.builder()
                                                            .append(Translations.PROMOTE_MASTER_NATURE_BUTTON.t())
                                                            .onHover(TextActions.showText(Translations.PROMOTE_BUTTON_HOVER.ft(Translations.MASTER_NATURE_DISPLAY.s())))
                                                            .onClick(TextActions.runCommand("/multi ticket complete " + ticket.getTicketID() + "|master " + PlayerUtils.getSafely(PlayerUtils.getNameFromUUID(ticket.getPlayerUUID())) + " nat"))
                                                            .build());
                                            action.append(Text.of(" "));
                                            action.append(
                                                    Text.builder()
                                                            .append(Translations.PROMOTE_MASTER_ARCHITECTURE_BUTTON.t())
                                                            .onHover(TextActions.showText(Translations.PROMOTE_BUTTON_HOVER.ft(Translations.MASTER_ARCHITECTURE_DISPLAY.s())))
                                                            .onClick(TextActions.runCommand("/multi ticket complete " + ticket.getTicketID() + "|master " + PlayerUtils.getSafely(PlayerUtils.getNameFromUUID(ticket.getPlayerUUID())) + " arch"))
                                                            .build());
                                            action.append(Text.of(" "));
                                            action.append(
                                                    Text.builder()
                                                            .append(Translations.PROMOTE_MASTER_BOTH_BUTTON.t())
                                                            .onHover(TextActions.showText(Translations.PROMOTE_BUTTON_HOVER.ft(Translations.MASTER_BOTH_DISPLAY.s())))
                                                            .onClick(TextActions.runCommand("/multi ticket complete " + ticket.getTicketID() + "|master " + PlayerUtils.getSafely(PlayerUtils.getNameFromUUID(ticket.getPlayerUUID())) + " all"))
                                                            .build());
                                            action.append(Text.NEW_LINE);
                                        }
                                        action.append(Text.of(" "));
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
                                                            .append(Translations.TICKET_REJECT_BUTTON.t())
                                                            .onHover(
                                                                    TextActions.showText(Translations.TICKET_REJECT_BUTTON_HOVER.t()))
                                                            .onClick(
                                                                    TextActions.runCommand("/ticket reject " + ticket.getTicketID()))
                                                            .build());
                                            action.append(Text.of(" "));
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
                                                    .append(Translations.TICKET_COMMENT_BUTTON.t())
                                                    .onHover(TextActions.showText(Translations.TICKET_COMMENT_BUTTON_HOVER.t()))
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
                                send.onHover(TextActions.showText(Translations.TICKET_TELEPORT_HOVER.t()));
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
                                            Translations.TICKET_CLAIMED_BY.ft(PlayerUtils.getSafely(PlayerUtils.getNameFromUUID(ticket.getStaffUUID()))));
                                else if (ticket.getStatus() == Closed)
                                    contents.add(
                                            Translations.TICKET_HANDLED_BY.ft(PlayerUtils.getSafely(PlayerUtils.getNameFromUUID(ticket.getStaffUUID()))));
                            }
                            if (!ticket.getComment().isEmpty()) {
                                contents.add(Translations.TICKET_COMMENT_CONTENT.ft(ticket.getComment()));
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

                            contents.add(Translations.TICKET_OPENED_BY.ft(PlayerUtils.getPlayerOnlineDisplay(ticket.getPlayerUUID()), ticketNum));
                            contents.add(Translations.TICKET_OPENED_WHEN.ft(TicketUtil.getTimeAgo(ticket.getTimestamp())));
                            contents.add(Translations.TICKET_OPENED_SERVER.ft(TicketUtil.getServerFormatted(ticket.getServer())));

                            if (!ticket
                                    .getPlayerUUID()
                                    .toString()
                                    .equals("00000000-0000-0000-0000-000000000000")) {
                                contents.add(send.build());
                            }
                            contents.add(Translations.TICKET_MESSAGE_LONG.ft(ticket.getMessage()));
                        }
                    }

                    if (contents.isEmpty()) {
                        throw new CommandException(Translations.TICKET_NOT_EXIST.ft(ticketID));
                    }

                    paginationService
                            .builder()
                            .title(Translations.TICKET_SINGLE_TITLE.ft(ticketID, ticketStatus))
                            .contents(contents)
                            .padding(Translations.DEFAULT_PADDING.t())
                            .sendTo(src);
                    return CommandResult.success();
                } else {
                    throw new CommandException(
                            Translations.TICKET_ERROR_PERMISSION.ft(TicketPermissions.COMMAND_TICKET_READ_SELF));
                }
            }
        }
    }

    public static List<Text> getContentsForClaimedTicket(List<Text> contents, TicketData ticket) {
        if (ticket.getStatus() == Claimed || ticket.getStatus() == Open) {
            Text.Builder send = Text.builder();
            String status = "";
            if (ticket.getStatus() == Claimed) status = Translations.TICKET_CLAIMED_PREFIX.s();
            send.append(Translations.TICKET_ROW_SINGLE.ft(ticket.getTicketID(), TicketUtil.getTimeAgo(ticket.getTimestamp()), PlayerUtils.getPlayerOnlineDisplay(ticket.getPlayerUUID()), TicketUtil.getServerFormatted(ticket.getServer()), Translations.shorten(ticket.getMessage()), status));
            send.onClick(TextActions.runCommand("/ticket read " + ticket.getTicketID()));
            send.onHover(
                    TextActions.showText(Translations.TICKET_MORE_INFO.ft(ticket.getTicketID())));
            contents.add(send.build());
        }
        return contents;
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
            PlayerUtils.tell(player, Translations.TICKET_TELEPORT.ft(ticketID));
        };
    }
}
