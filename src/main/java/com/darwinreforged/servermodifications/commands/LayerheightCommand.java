package com.darwinreforged.servermodifications.commands;

import com.darwinreforged.servermodifications.translations.Translations;
import com.darwinreforged.servermodifications.util.PlayerUtils;
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

public class LayerheightCommand  implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        // TODO Auto-generated method stub
        if (!(src instanceof Player)) {
            PlayerUtils.tell(src, Translations.PLAYER_ONLY_COMMAND.s());
            return CommandResult.success();

        } else {
            Player player = (Player) src;
            if (player.getItemInHand(HandTypes.MAIN_HAND).isPresent()) {
                int num = (int) args.getOne("1 to 8").get();
                if (num > 8) {
                    PlayerUtils.tell(src, Translations.HEIGHT_TOO_HIGH.s());
                    return CommandResult.success();

                }
                if (num < 1) {
                    PlayerUtils.tell(src, Translations.HEIGHT_TOO_LOW.s());
                    return CommandResult.success();
                }
                ItemStack item = player.getItemInHand(HandTypes.MAIN_HAND).get();

                if (item.getItem().getBlock().isPresent() && !item.getType().equals(ItemTypes.AIR)) {
                    item.offer(Keys.DISPLAY_NAME, Text.of(Translations.HEIGHTTOOL_NAME.f(num)));
                    player.setItemInHand(HandTypes.MAIN_HAND, item);
                    PlayerUtils.tell(player, Translations.HEIGHTTOOL_SET.f(num));

                } else {
                    PlayerUtils.tell(player, Translations.HEIGHTTOOL_FAILED_BIND.s());
                }
            }
        }
        return CommandResult.success();
    }
}
