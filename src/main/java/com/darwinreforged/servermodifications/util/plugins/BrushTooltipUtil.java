package com.darwinreforged.servermodifications.util.plugins;

import com.darwinreforged.servermodifications.enums.brushtooltips.Argument;
import com.darwinreforged.servermodifications.enums.brushtooltips.Brush;
import com.darwinreforged.servermodifications.resources.Translations;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Tuple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BrushTooltipUtil {

    public static Tuple<Text, String[]> parseFlags(String[] arguments, Brush brush) {
        List<String> parsedArguments = new ArrayList<>();
        List<String> flags = new ArrayList<>();

        brush
                .getFlags()
                .forEach(
                        flag ->
                                Arrays.stream(arguments)
                                        .forEach(
                                                (String argument) -> {
                                                    if (argument.equals(flag.getFlag())) flags.add(flag.getDescription());
                                                    else parsedArguments.add(argument);
                                                }));

        if (flags.isEmpty()) return new Tuple<>(Text.EMPTY, arguments);
        else {
            Text flagLabel = Translations.BRUSH_TOOLTIP_LORE_FLAGS.ft(String.join(", ", flags));
            return new Tuple<>(flagLabel, parsedArguments.toArray(new String[0]));
        }
    }

    public static Tuple<List<Text>, Integer> parseArguments(String[] arguments, Brush brush) {
        List<Text> argumentLore = new ArrayList<>();
        int radius = -1;

        for (Argument argument : brush.getArguments()) {
            if (argument.getIndex() + 1 <= arguments.length) {
                String description = argument.getDescription();
                String value = arguments[argument.getIndex() + 1];

                if ("Radius".equals(argument.getDescription()))
                    radius = Integer.parseInt(arguments[argument.getIndex() + 1]);
                else
                    argumentLore.add(Translations.BRUSH_TOOLTIP_LORE_ARGUMENT.ft(description, value));
            }
        }

        return new Tuple<>(argumentLore, radius);
    }

    public static ItemStack combineLore(
            ItemStack itemStack,
            List<Text> arguments,
            int radius,
            Text flag,
            Brush brush,
            String command) {
        String brushTitle = brush.getDisplayName();
        Text itemDisplayName = Translations.BRUSH_TOOLTIP_DISPLAY_NAME.ft(brushTitle, radius);
        itemStack.offer(Keys.DISPLAY_NAME, itemDisplayName);

        List<Text> lore = new ArrayList<>();
        Text line = Translations.BRUSH_TOOLTIP_LORE_SEPARATOR.t();

        lore.add(line);
        lore.addAll(arguments);
        lore.add(line);
        lore.add(flag);
        lore.add(line);
        lore.add(Text.of(TextColors.GRAY, command));

        itemStack.offer(Keys.ITEM_LORE, lore);

        return itemStack;
    }
}
