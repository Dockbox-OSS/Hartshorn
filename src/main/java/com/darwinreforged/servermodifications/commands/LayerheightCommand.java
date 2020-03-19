package com.darwinreforged.servermodifications.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class LayerheightCommand  implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        // TODO Auto-generated method stub
        if (!(src instanceof Player)) {
            src.sendMessage(Text.of("Only a player can run this command"));
            return CommandResult.success();

        } else {
            Player player = (Player) src;
            if (player.getItemInHand(HandTypes.MAIN_HAND).isPresent()) {
                int num = (int) args.getOne("1 to 8").get();
                if (num > 8) {
                    src.sendMessage(
                            Text.of(TextColors.GRAY, "[] ", TextColors.AQUA, "Error: Cannot be above 8."));
                    return CommandResult.success();
                }
                if (num < 1) {
                    src.sendMessage(
                            Text.of(TextColors.GRAY, "[] ", TextColors.AQUA, "Error: Cannot be below 1."));
                    return CommandResult.success();
                }
                ItemStack item = player.getItemInHand(HandTypes.MAIN_HAND).get();

                if (item.getItem().getBlock().isPresent() && !item.getType().equals(ItemTypes.AIR)) {
                    item.offer(Keys.DISPLAY_NAME, Text.of("Layer Height Tool: " + num));
                    player.setItemInHand(HandTypes.MAIN_HAND, item);
                    player.sendMessage(
                            Text.of(
                                    TextColors.GRAY,
                                    "[] ",
                                    TextColors.AQUA,
                                    "Successfully set the layer height to ",
                                    num));

                } else {
                    player.sendMessage(
                            Text.of(
                                    TextColors.GRAY,
                                    "[] ",
                                    TextColors.AQUA,
                                    "Error: tool can only be bound to a block."));
                }
            }
        }
        return CommandResult.success();
    }
}
