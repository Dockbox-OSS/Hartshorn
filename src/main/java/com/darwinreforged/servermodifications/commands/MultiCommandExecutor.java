package com.darwinreforged.servermodifications.commands;

import com.darwinreforged.servermodifications.DarwinServer;
import com.darwinreforged.servermodifications.resources.Translations;
import com.darwinreforged.servermodifications.util.PlayerUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MultiCommandExecutor implements CommandExecutor {


    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Optional<Object> cmdOpt = args.getOne("commands");

        if (src instanceof Player) {
            if (cmdOpt.isPresent()) {
                String commandsJoined = cmdOpt.get().toString();
                UUID uuid = ((Player) src).getUniqueId();

                String[] commands = commandsJoined.split("\\|");
                for (int i = 0; i < commands.length; i++) {
                    String command = commands[i];
                    while (command.startsWith(" ")) command = command.substring(1);

                    if (!command.equals("")) {
                        String finalCommand = command;
                        // As this is all scheduled directly, make sure we schedule the next step 500ms (0.5s) later than the previous one.
                        Task.builder().delay((i + 1) * 500, TimeUnit.MILLISECONDS).execute(() -> {
                            PlayerUtils.tell(src, Translations.MULTI_CMD_PERFORMING.f(finalCommand));
                            Sponge.getCommandManager().process(src, finalCommand);
                        }).name("MultiCommand").submit(DarwinServer.getServer());
                    }
                }
            }
        }

        return CommandResult.success();
    }

}
