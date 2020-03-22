package com.darwinreforged.servermodifications.commands.tickets;

import com.darwinreforged.servermodifications.objects.TicketData;
import com.darwinreforged.servermodifications.plugins.TicketPlugin;
import com.darwinreforged.servermodifications.resources.Translations;
import com.darwinreforged.servermodifications.util.PlayerUtils;
import io.github.nucleuspowered.nucleus.Nucleus;
import io.github.nucleuspowered.nucleus.api.service.NucleusMailService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TicketRejectCommand implements CommandExecutor {

    private final TicketPlugin plugin;

    public TicketRejectCommand(TicketPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        args.putArg("rejected", true);
        new TicketCloseCommand(plugin).execute(src, args);
        Optional<NucleusMailService> mailServiceOptional =
                Nucleus.getNucleus().getInternalServiceManager().getService(NucleusMailService.class);
        if (mailServiceOptional.isPresent()) {
            final List<TicketData> tickets =
                    new ArrayList<TicketData>(plugin.getDataStore().getTicketData());
            final Optional<Integer> ticketIDOp = args.getOne("ticketID");
            if (ticketIDOp.isPresent()) {
                Optional<TicketData> ticketOpt =
                        tickets.stream().filter(t -> t.getTicketID() == ticketIDOp.get()).findFirst();
                if (ticketOpt.isPresent()) {
                    TicketData ticket = ticketOpt.get();
                    String playerName = PlayerUtils.getSafely(PlayerUtils.getNameFromUUID(ticket.getPlayerUUID()));
                    Sponge.getCommandManager()
                            .process(
                                    Sponge.getServer().getConsole(),
                                    "cu execute whenonline "
                                            + playerName
                                            + " *plaintell "
                                            + playerName
                                            + Translations.REJECTED_TICKET_TARGET.s());

                    PlayerUtils.tell(src, Translations.REJECTED_TICKET_SOURCE.f(ticket.getTicketID()));
                }
            }
        }
        return CommandResult.success();
    }
}
