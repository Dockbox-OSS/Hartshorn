package com.darwinreforged.servermodifications.commands.tickets;

import com.darwinreforged.servermodifications.permissions.TicketPermissions;
import com.darwinreforged.servermodifications.plugins.TicketPlugin;
import com.darwinreforged.servermodifications.translations.TicketMessages;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.List;

public class TicketStaffCommand implements CommandExecutor {

    private final TicketPlugin plugin;

    public TicketStaffCommand(TicketPlugin instance) {
        plugin = instance;
    }

    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        PaginationService paginationService = Sponge.getServiceManager().provide(PaginationService.class).get();
        List<Text> staffList = new ArrayList<>();
        StringBuilder staff = new StringBuilder();
        String separator = TicketMessages.getStaffListSeperator();

        for (Player player : Sponge.getServer().getOnlinePlayers()) {
            if (player.hasPermission(TicketPermissions.STAFF) && (!player.get(Keys.VANISH).filter(value -> value).isPresent())) {
                staff.append("&b" + player.getName());
                staff.append(separator);
            }
        }

        if (staff.length() > 0) {
            String staffString = staff.substring(0, staff.length() - separator.length());
            staffList.add(plugin.fromLegacy(staffString));
        }

        if (staffList.isEmpty()) {
            staffList.add(TicketMessages.getStaffListEmpty());
        }

        paginationService.builder()
                .title(TicketMessages.getStaffListTitle())
                .contents(staffList)
                .padding(TicketMessages.getStaffListPadding())
                .sendTo(src);
        return CommandResult.success();
    }
}
