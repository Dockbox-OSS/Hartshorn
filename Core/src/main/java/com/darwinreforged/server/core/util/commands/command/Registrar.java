package com.darwinreforged.server.core.util.commands.command;

import com.darwinreforged.server.core.util.CommandUtils;
import com.darwinreforged.server.core.util.commands.annotation.processor.Processor;
import com.darwinreforged.server.core.util.commands.element.ElementFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


public class Registrar<T extends Command> {

    private final CommandUtils manager;
    private final Processor processor = new Processor(this);
    private final Map<String, Entry> builders = new HashMap<>();

    private final ElementFactory elementFactory;
    private final CommandFactory<T> commandFactory;

    public Registrar(CommandUtils manager, ElementFactory elementFactory, CommandFactory<T> commandFactory) {
        this.manager = manager;
        this.elementFactory = elementFactory;
        this.commandFactory = commandFactory;
    }

    public CommandUtils getManager() {
        return manager;
    }

    public CommandFactory<T> getCommandFactory() {
        return commandFactory;
    }

    public ElementFactory getElementFactory() {
        return elementFactory;
    }

    public int register(Object source) {
        return processor.process(source);
    }

    public void register(Collection<String> aliases, CommandExecutor executor) {
        Entry builder = null;
        for (String alias : aliases) {
            builder = builders.get(alias);
            if (builder != null) {
                break;
            }
        }

        if (builder == null) {
            builder = new Entry();
        }

        for (String alias : aliases) {
            builders.put(alias, builder);
        }

        builder.aliases.addAll(aliases);
        builder.executors.add(executor);
    }

    public Collection<T> build() {
        return builders.values().stream().distinct()
                .peek(builder -> Collections.sort(builder.executors, CommandExecutor.EXECUTION_ORDER))
                .map(builder -> getCommandFactory().create(builder.aliases, builder.executors))
                .collect(Collectors.toList());
    }

    public static <T extends Command> Registrar<T> of(CommandUtils<T> manager, ElementFactory elementFactory, CommandFactory<T> commandFactory) {
        return new Registrar<>(manager, elementFactory, commandFactory);
    }

    private static class Entry {

        private final List<CommandExecutor> executors = new ArrayList<>();
        private final Set<String> aliases = new LinkedHashSet<>();
    }
}
