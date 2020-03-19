package com.darwinreforged.servermodifications.commands;

import com.darwinreforged.servermodifications.plugins.MultiCommandPlugin;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.*;
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
                            src.sendMessage(Text.of(TextColors.GRAY, "[] ", TextColors.DARK_AQUA, "Performing : ", TextColors.AQUA, "/" + finalCommand));
                            Sponge.getCommandManager().process(src, finalCommand);
                        }).name("MultiCommand").submit(MultiCommandPlugin.getInstance());
                    }
                }
            }
        }

        return CommandResult.success();
    }

}
