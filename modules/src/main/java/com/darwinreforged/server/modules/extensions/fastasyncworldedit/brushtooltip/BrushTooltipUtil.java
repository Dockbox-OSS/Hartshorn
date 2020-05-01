package com.darwinreforged.server.modules.extensions.fastasyncworldedit.brushtooltip;

import com.darwinreforged.server.core.entities.DarwinItem;
import com.darwinreforged.server.core.entities.Tuple;
import com.darwinreforged.server.core.resources.Translations;
import com.darwinreforged.server.modules.extensions.fastasyncworldedit.brushtooltip.enums.Argument;
import com.darwinreforged.server.modules.extensions.fastasyncworldedit.brushtooltip.enums.Brush;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BrushTooltipUtil {

    public static Tuple<String, String[]> parseFlags(String[] arguments, Brush brush) {
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

        if (flags.isEmpty()) return new Tuple<>("", arguments);
        else {
            String flagLabel = Translations.BRUSH_TOOLTIP_LORE_FLAGS.f(String.join(", ", flags));
            return new Tuple<>(flagLabel, parsedArguments.toArray(new String[0]));
        }
    }

    public static Tuple<List<String>, Integer> parseArguments(String[] arguments, Brush brush) {
        List<String> argumentLore = new ArrayList<>();
        int radius = -1;

        for (Argument argument : brush.getArguments()) {
            if (argument.getIndex() + 1 <= arguments.length) {
                String description = argument.getDescription();
                String value = arguments[argument.getIndex() + 1];

                if ("Radius".equals(argument.getDescription()))
                    radius = Integer.parseInt(arguments[argument.getIndex() + 1]);
                else
                    argumentLore.add(Translations.BRUSH_TOOLTIP_LORE_ARGUMENT.f(description, value));
            }
        }

        return new Tuple<>(argumentLore, radius);
    }

    public static DarwinItem<?> combineLore(
            DarwinItem<?> itemStack,
            List<String> arguments,
            int radius,
            String flag,
            Brush brush,
            String command) {
        String brushTitle = brush.getDisplayName();
        String itemDisplayName = Translations.BRUSH_TOOLTIP_DISPLAY_NAME.f(brushTitle, radius);
        itemStack.setDisplayName(itemDisplayName);

        List<String> lore = new ArrayList<>();
        String line = Translations.BRUSH_TOOLTIP_LORE_SEPARATOR.s();

        lore.add(line);
        lore.addAll(arguments);
        lore.add(line);
        lore.add(flag);
        lore.add(line);
        lore.add(String.format("&7%s", command));

        itemStack.setLore(lore.toArray(new String[] {}));

        return itemStack;
    }
}
