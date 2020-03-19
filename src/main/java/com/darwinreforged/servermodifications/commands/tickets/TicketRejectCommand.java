package com.darwinreforged.servermodifications.commands.tickets;

import com.darwinreforged.servermodifications.objects.TicketData;
import com.darwinreforged.servermodifications.plugins.TicketPlugin;
import com.darwinreforged.servermodifications.util.TicketUtil;
import io.github.nucleuspowered.nucleus.Nucleus;
import io.github.nucleuspowered.nucleus.api.service.NucleusMailService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

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
                    String playerName = TicketUtil.getPlayerNameFromData(plugin, ticket.getPlayerUUID());
                    Sponge.getCommandManager()
                            .process(
                                    Sponge.getServer().getConsole(),
                                    "cu execute whenonline "
                                            + playerName
                                            + " *plaintell "
                                            + playerName
                                            + " &8[] &cYour application was reviewed but was not yet approved, make sure to read the given feedback on your plot, and apply again once ready!");
                    src.sendMessage(
                            Text.of(
                                    TextColors.GRAY,
                                    "[] ",
                                    TextColors.AQUA,
                                    "Rejected and closed ticket #" + ticket.getTicketID()));
                }
            }
        }
        return CommandResult.success();
    }
}
