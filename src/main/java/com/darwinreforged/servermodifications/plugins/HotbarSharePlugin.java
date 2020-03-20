package com.darwinreforged.servermodifications.plugins;

import com.darwinreforged.servermodifications.translations.Translations;
import com.darwinreforged.servermodifications.util.PlayerUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.item.EnchantmentData;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.entity.Hotbar;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Plugin(
        id = "hotbarshare",
        name = "Hotbar Sharing",
        version = "1.0.2",
        description = "Allows the user to share their hotbar with one or multiple players, storing all custom data of the items",
        authors = {"DiggyNevs"})
public class HotbarSharePlugin {

    private static final HashMap<Long, HotbarShare> sharedBars = new HashMap<>();

    private CommandSpec hblView =
            CommandSpec.builder()
                    .arguments(GenericArguments.longNum(Text.of("id")))
                    .executor(new ViewCommand())
                    .build();

    private CommandSpec hblLoad =
            CommandSpec.builder()
                    .arguments(GenericArguments.longNum(Text.of("id")), GenericArguments.optional(GenericArguments.integer(Text.of("SlotIndex"))))
                    .executor(new LoadCommand())
                    .build();

    private CommandSpec hbl =
            CommandSpec.builder()
                    .description(Text.of("Loads hotbar from #hotbar action"))
                    .permission("hb.load")
                    .child(hblView, "view")
                    .child(hblLoad, "load")
                    .build();

    private CommandSpec hbshare =
            CommandSpec.builder()
                    .description(Text.of("Shared hotbar"))
                    .permission("hb.share")
                    .arguments(GenericArguments.optional(GenericArguments.player(Text.of("PlayerName"))))
                    .executor(new ShareCommand())
                    .build();

    public HotbarSharePlugin() {
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        Sponge.getCommandManager().register(this, hbl, "hbload");
        Sponge.getCommandManager().register(this, hbshare, "shareinv");
    }

    public static class ShareCommand implements CommandExecutor {

        @Override
        public CommandResult execute(CommandSource src, CommandContext args) {
            if (src instanceof Player) {
                Player player = (Player) src;
                Player shareWith = null;
                if (args.getOne("PlayerName").isPresent())
                    shareWith = (Player) args.getOne("PlayerName").get();

                Inventory inventory = ((Player) player.createSnapshot().restore().get()).getInventory();
                ;
                Hotbar hotbar = inventory.query(Hotbar.class);
                List<ItemStack> itemStackList = new ArrayList<>();
                hotbar.slots().forEach(inventoryItem -> {
                    if (inventoryItem instanceof Slot) {
                        Slot slot = (Slot) inventoryItem;
                        if (slot.peek().isPresent()) itemStackList.add(slot.peek().get().copy());
                    }
                });

                Long id = System.currentTimeMillis();
                HotbarSharePlugin.sharedBars.put(id, new HotbarShare(itemStackList, player.getName()));

                Text buttonView =
                        Text.builder()
                                .append(
                                        Text.of(
                                                TextColors.DARK_AQUA,
                                                " [",
                                                TextColors.AQUA,
                                                "View",
                                                TextColors.DARK_AQUA,
                                                "]"))
                                .onClick(TextActions.runCommand("/hbload view " + id))
                                .onHover(TextActions.showText(Text.of("View hotbar")))
                                .build();

                Text buttonLoad =
                        Text.builder()
                                .append(
                                        Text.of(
                                                TextColors.DARK_AQUA,
                                                "[",
                                                TextColors.AQUA,
                                                "Load",
                                                TextColors.DARK_AQUA,
                                                "]"))
                                .onClick(TextActions.runCommand("/hbload load " + id))
                                .onHover(TextActions.showText(Text.of("Load hotbar")))
                                .build();

                PlayerUtils.tell(player, Translations.SHARED_HOTBAR_WITH.f(shareWith == null ? "everyone" : shareWith.getName()));
                if (shareWith != null)
                    PlayerUtils.tell(shareWith, Text.of(Translations.PLAYER_SHARED_HOTBAR.f(player.getName()), buttonView, buttonLoad));
                else
                    PlayerUtils.broadcast(Text.of(Translations.PLAYER_SHARED_HOTBAR.f(player.getName()), buttonView, buttonLoad));
            }
            return CommandResult.success();
        }
    }

    public static class LoadCommand implements CommandExecutor {

        @Override
        public CommandResult execute(CommandSource src, CommandContext args) {
            if (src instanceof Player) {
                Long id = (Long) args.getOne("id").get();
                List<ItemStack> hotbar = HotbarSharePlugin.sharedBars.get(id).getHotbar();
                if (args.getOne("SlotIndex").isPresent()) {
                    // Only set one slot, offers to hotbar
                    Integer slotIndex = (Integer) args.getOne("SlotIndex").get();
                    ItemStack itemStack = hotbar.get(slotIndex);
                    this.fillIndex((Player) src, itemStack);
                } else {
                    ((Player) src).getInventory().query(Hotbar.class).clear();
                    for (int i = 0; i < hotbar.size(); i++) {
                        ItemStack itemStack = hotbar.get(i);
                        this.fillIndex((Player) src, itemStack);
                    }
                }
            }
            return CommandResult.success();
        }

        private void fillIndex(Player player, ItemStack itemStack) {
            ItemStack offerable = itemStack.copy();
            if (player.getInventory().query(Hotbar.class).canFit(offerable))
                player.getInventory().query(Hotbar.class).offer(offerable);
            else
                PlayerUtils.tell(player, Translations.FULL_HOTBAR.s());
        }
    }


    public static class ViewCommand implements CommandExecutor {

        @Override
        public CommandResult execute(CommandSource src, CommandContext args) {
            Long id = (Long) args.getOne("id").get();
            List<ItemStack> hotbar = HotbarSharePlugin.sharedBars.get(id).getHotbar();
            PlayerUtils.tell(src, Translations.HOTBAR_SHARE_HEADER.f(HotbarSharePlugin.sharedBars.get(id).getSharedBy()));

            for (int i = 0; i < hotbar.size(); i++) {
                ItemStack stack = hotbar.get(i);
                Text displayName =
                        stack.get(Keys.DISPLAY_NAME).orElse(Text.of(stack.getTranslation().get()));

                Optional<EnchantmentData> enchantmentData = stack.get(EnchantmentData.class);
                List<String> enchantmentDisplays = new ArrayList<>();
                enchantmentData.ifPresent(data -> data.enchantments().forEach(enchantment ->
                        enchantmentDisplays.add(String.format("%s %d", enchantment.getType().getTranslation().get(), enchantment.getLevel()))
                ));

                Optional<List<Text>> optionalLore = stack.get(Keys.ITEM_LORE);
                List<String> lore = new ArrayList<>();
                optionalLore.ifPresent(data -> data.forEach(line -> lore.add(String.format("   %s", line.toPlain()))));

                Text itemRow = Text.of(Translations.HOTBAR_SHARE_INDEX.f(i + 1, displayName));

                Text loadButton =
                        Text.builder()
                                .append(Text.of(TextColors.GRAY, "[", TextColors.AQUA, "Get", TextColors.GRAY, "]"))
                                .onClick(TextActions.runCommand("/hbload load " + id + " " + i))
                                .onHover(TextActions.showText(Text.of(TextColors.AQUA, "Get '", displayName, "'")))
                                .build();

                PlayerUtils.tell(src, Text.of(itemRow, " ", loadButton,

                        enchantmentData.isPresent() ? Text.of(
                                Translations.HOTBAR_SHARE_ENCHANTED.s(),
                                (enchantmentDisplays.size() > 1 ? "\n" : ""),
                                TextColors.AQUA,
                                String.join("\n ", enchantmentDisplays),
                                TextColors.DARK_AQUA)
                                : "",

                        optionalLore.isPresent() ? Text.of(
                                Translations.HOTBAR_SHARE_LORE.s(),
                                TextColors.AQUA,
                                (lore.size() > 1 ? "\n" : ""),
                                String.join("\n", lore))
                                : ""
                ), false);
            }
            PlayerUtils.tell(src, Translations.HOTBAR_SHARE_HEADER.f(HotbarSharePlugin.sharedBars.get(id).getSharedBy()));
            return CommandResult.success();
        }

    }

    static class HotbarShare {
        private List<ItemStack> hotbar;
        private String sharedBy;

        public HotbarShare(List<ItemStack> hotbar, String sharedBy) {
            this.hotbar = hotbar;
            this.sharedBy = sharedBy;
        }

        public List<ItemStack> getHotbar() {
            return hotbar;
        }

        public String getSharedBy() {
            return sharedBy;
        }
    }
}
