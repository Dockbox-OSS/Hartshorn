package com.darwinreforged.servermodifications.commands.tickets;


import com.darwinreforged.servermodifications.objects.TicketData;
import com.darwinreforged.servermodifications.modules.TicketModule;
import com.darwinreforged.servermodifications.resources.Translations;
import com.darwinreforged.servermodifications.util.LocationUtils;
import com.darwinreforged.servermodifications.util.PlayerUtils;
import com.darwinreforged.servermodifications.util.todo.config.TicketConfig;
import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.List;

public class TicketTeleportCommand implements CommandExecutor {

    private final TicketModule plugin;

    public TicketTeleportCommand(TicketModule plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        final int ticketID = args.<Integer>getOne("ticketID").get();
        final List<TicketData> tickets = new ArrayList<TicketData>(plugin.getDataStore().getTicketData());

        if (!(src instanceof Player)) {
            throw new CommandException(Translations.UNKNOWN_ERROR.ft("Only players can use this command"));
        }
        Player player = (Player) src;

        if (tickets.isEmpty()) {
            throw new CommandException(Translations.UNKNOWN_ERROR.ft("Tickets list is empty."));
        } else {
            boolean ticketExist = false;
            for (TicketData ticket : tickets) {
                if (ticket.getTicketID() == ticketID) {
                    if (ticket.getServer().equalsIgnoreCase(TicketConfig.server)) {
                        ticketExist = true;
                        World world = LocationUtils.getWorld(ticket.getWorld()).get();
                        Location loc = new Location(world, ticket.getX(), ticket.getY(), ticket.getZ());
                        Vector3d vect = new Vector3d(ticket.getPitch(), ticket.getYaw(), 0);
                        player.setLocationAndRotation(loc, vect);
                        PlayerUtils.tell(player, Translations.TICKET_TELEPORT.ft(ticketID));
                    } else {
                        throw new CommandException(Translations.TICKET_ERROR_SERVER.ft(ticketID));
                    }
                }
            }
            if (!ticketExist) {
                throw new CommandException(Translations.TICKET_NOT_EXIST.ft(ticketID));
            }
            return CommandResult.success();
        }
    }
}
