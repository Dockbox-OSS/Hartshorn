package com.darwinreforged.servermodifications.listeners;

import com.boydti.fawe.Fawe;
import com.boydti.fawe.FaweAPI;
import com.darwinreforged.servermodifications.enums.brushtooltips.Brush;
import com.darwinreforged.servermodifications.enums.brushtooltips.Brushes;
import com.darwinreforged.servermodifications.resources.Translations;
import com.darwinreforged.servermodifications.util.PlayerUtils;
import com.darwinreforged.servermodifications.util.plugins.BrushTooltipUtil;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.action.InteractEvent;
import org.spongepowered.api.event.command.SendCommandEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.item.inventory.TargetInventoryEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Tuple;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class BrushTooltipListener {

    public static HashMap<String, String> brushAliases = new HashMap<>();
    private static HashMap<Player, LocalDateTime> lastMessage = new HashMap<>();
    private static HashMap<Player, List<ItemStack>> aStoredBrushes = new HashMap<>();
    private static List<String> commandAliases = Arrays.asList("brush", "br", "/brush", "/br");

    @Listener
    public void onInventoryInteract(TargetInventoryEvent event, @Root Player player) {
        updateInventoryTooltips(player);
    }

    public static void updateInventoryTooltips(Player player) {
        if (aStoredBrushes.get(player) != null)
            aStoredBrushes
                    .get(player)
                    .forEach(
                            stack -> {
                                ItemType itemType = stack.getType();
                                player
                                        .getInventory()
                                        .query(QueryOperationTypes.ITEM_TYPE.of(itemType))
                                        .forEach(slot -> slot.set(stack));
                            });
    }

    // What to do when the event is dispatched
    @Listener(order = Order.PRE)
    public void onCommand(SendCommandEvent event, @Root Player player) {
        // Make sure we are working with a CommandEvent, whether it's //brush handled later
        // Make sure it's a Brush command
        Optional<ItemStack> optionalInHand = player.getItemInHand(HandTypes.MAIN_HAND);
        if (commandAliases.contains(event.getCommand())) {

            String[] arguments = event.getArguments().split(" ");
            String brushType = arguments[0];

            if (brushAliases.get(brushType) != null) brushType = brushAliases.get(brushType);

            Brushes brushEnum = Brushes.valueOf(brushType.toUpperCase());

            // Get item in hand, this will be the brush tool to modify
            if (optionalInHand.isPresent() && brushEnum != null) {
                ItemStack itemInHand = optionalInHand.get();
                Brush brush = brushEnum.getBrush();

                Tuple<Text, String[]> flagsParsed = BrushTooltipUtil.parseFlags(arguments, brush);
                Text flagLore = flagsParsed.getFirst();
                String[] argumentsNoFlags = flagsParsed.getSecond();

                Tuple<List<Text>, Integer> argumentTuple =
                        BrushTooltipUtil.parseArguments(argumentsNoFlags, brush);
                List<Text> parsedArguments = argumentTuple.getFirst();
                int radius = argumentTuple.getSecond();

                String fullCommand = '/' + event.getCommand() + ' ' + event.getArguments();

                ItemStack newItemInHand =
                        BrushTooltipUtil.combineLore(
                                itemInHand, parsedArguments, radius, flagLore, brush, fullCommand);

                player
                        .getInventory()
                        .query(QueryOperationTypes.ITEM_TYPE.of(itemInHand.getType()))
                        .forEach(slot -> slot.set(newItemInHand));

                player.setItemInHand(HandTypes.MAIN_HAND, newItemInHand);

                aStoredBrushes.computeIfAbsent(player, k -> new ArrayList<>());
                aStoredBrushes.get(player).add(newItemInHand);
            }
        } else if ("repl".equals(event.getCommand()) || "/x".equals(event.getCommand())) {
            if (optionalInHand.isPresent()) {
                ItemStack itemInHand = optionalInHand.get();
                itemInHand.offer(
                        Keys.DISPLAY_NAME,
                        Translations.REPLACER_TOOL_DISPLAY_NAME.ft(event.getArguments().split(" ")[0]));
                List<Text> lore = new ArrayList<>();
                lore.add(Text.of(TextColors.GRAY, "//x " + event.getArguments().split(" ")[0]));
                itemInHand.offer(Keys.ITEM_LORE, lore);
                player.setItemInHand(HandTypes.MAIN_HAND, itemInHand);
            }
        }
    }

    @Listener(order = Order.PRE)
    public void onRightClick(InteractEvent event, @Root Player player) {
        try {
            if (player.getItemInHand(HandTypes.MAIN_HAND).isPresent()) {
                ItemStack itemInHand = player.getItemInHand(HandTypes.MAIN_HAND).get();
                if (itemInHand.get(Keys.ITEM_LORE).isPresent()) {
                    List<Text> lore = itemInHand.get(Keys.ITEM_LORE).get();
                    String command = lore.get(lore.size() - 1).toPlain();
                    if (command.startsWith("/brush") || command.startsWith("/br")) {
                        if (Fawe.get()
                                .getCachedPlayer(player.getUniqueId())
                                .getSession()
                                .getBrushTool(FaweAPI.wrapPlayer(player).getPlayer())
                                .getBrush()
                                == null) {

                            if (lastMessage.get(player) == null // Never sent
                                    || lastMessage
                                    .get(player)
                                    .plus(1, ChronoUnit.SECONDS) // Sent 5 seconds ago (or more)
                                    .isBefore(LocalDateTime.now())) {

                                Text.Builder commandBuilder = Text.builder();
                                commandBuilder.append(Text.of(command));
                                commandBuilder.onClick(TextActions.suggestCommand(command));

                                // After
                                PlayerUtils.tell(player, Text.of(Translations.HOLDING_UNSET_TOOL.s(), commandBuilder
                                        .build()));
                            }
                            lastMessage.put(player, LocalDateTime.now());
                        }
                    }
                }
            }
        } catch (Exception ex) {
            // Do nothing, if it errored there is either no brush or it doesn't want to be set
        }
    }
}
