/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.sponge.util.command;

import com.google.common.collect.Multimap;

import org.dockbox.selene.api.Selene;
import org.dockbox.selene.api.domain.Exceptional;
import org.dockbox.selene.api.exceptions.Except;
import org.dockbox.selene.commands.DefaultCommandBus;
import org.dockbox.selene.commands.context.SimpleCommandContext;
import org.dockbox.selene.commands.registration.AbstractRegistrationContext;
import org.dockbox.selene.commands.registration.CommandInheritanceContext;
import org.dockbox.selene.commands.source.CommandSource;
import org.dockbox.selene.commands.values.AbstractArgumentElement;
import org.dockbox.selene.commands.values.ArgumentValue;
import org.dockbox.selene.sponge.util.SpongeConversionUtil;
import org.dockbox.selene.sponge.util.command.values.SpongeArgumentElement;
import org.dockbox.selene.sponge.util.command.values.SpongeArgumentValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.command.spec.CommandSpec.Builder;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Singleton;

@Singleton
public class SpongeCommandBus extends DefaultCommandBus<Builder> {

    private final Field parsedArgsF;

    public SpongeCommandBus() {
        try {
            this.parsedArgsF = CommandContext.class.getDeclaredField("parsedArgs");
            if (!this.parsedArgsF.isAccessible()) this.parsedArgsF.setAccessible(true);
        }
        catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Could not access 'parsedArgs' field in CommandContext");
        }
    }

    private CommandExecutor buildExecutor(AbstractRegistrationContext registrationContext, String command) {
        return (src, args) -> {
            /*
            Command sources need to be convertable so that they can be identified by command implementations. While it
            is possible for modules to implement CommandSources, these should never be used to execute commands.
            Only the console, players, and Discord command sources can be converted natively.
            */
            CommandSource sender = SpongeConversionUtil.fromSponge(src)
                    .cause(() -> new IllegalArgumentException("Command sender is not a convertable source type, did a plugin call me?"));
            SimpleCommandContext ctx = this.createCommandContext(args, sender, command);
            callCommandContext(registrationContext, command, sender, ctx);
            return CommandResult.success();
        };
    }

    @SuppressWarnings("unchecked")
    private SimpleCommandContext createCommandContext(CommandContext ctx, @NotNull CommandSource sender, @Nullable String command) {
        try {
            /*
            Sponge's CommandContext does not expose parsed arguments by default, so Reflections are needed to access these
            so that they can be converted.
            */
            Multimap<String, Object> parsedArgs = (Multimap<String, Object>) this.parsedArgsF.get(ctx);
            return super.createCommandContext(command, sender, parsedArgs.asMap());
        }
        catch (IllegalAccessException | ClassCastException e) {
            Except.handle("Could not load parsed arguments from Sponge command context", e);
            return SimpleCommandContext.EMPTY;
        }
    }

    @Override
    protected ArgumentValue<?> getArgumentValue(String type, String permission, String key) {
        return new SpongeArgumentValue(type, permission, key);
    }

    @Override
    protected Object tryConvertObject(Object obj) {
        /*
        This converts non-native (JDK) types to Selene usable types. Converters can be applied later to further convert
        and/or modify these objects.
        */
        Exceptional<?> oo = SpongeConversionUtil.autoDetectFromSponge(obj);
        return oo.present() ? oo.get() : obj; // oo.or() cannot be cast due to generic ? type
    }

    @Override
    protected AbstractArgumentElement<?> wrapElements(List<AbstractArgumentElement<?>> elements) {
        return new SpongeArgumentElement(elements.stream()
                .filter(element -> element instanceof SpongeArgumentElement)
                .map(element -> (SpongeArgumentElement) element)
                .toArray(SpongeArgumentElement[]::new)
        );
    }

    protected CommandSpec.Builder buildInheritedContextExecutor(CommandInheritanceContext context, String alias) {
        CommandSpec.Builder builder = this.buildContextExecutor(context, alias);
        context.getInheritedCommands().forEach(inheritedContext ->
                inheritedContext.getAliases().forEach(inheritedAlias ->
                        builder.child(
                                this.buildContextExecutor(inheritedContext, inheritedAlias).build(),
                                inheritedAlias
                        )));
        return builder;
    }

    @Override
    protected void registerExecutor(Builder executor, String alias) {
        Sponge.getCommandManager().register(Selene.getServer(), executor.build(), alias);
    }

    protected CommandSpec.Builder buildContextExecutor(AbstractRegistrationContext context, String alias) {
        CommandSpec.Builder builder = CommandSpec.builder();

        List<AbstractArgumentElement<?>> elements = super.parseArgumentElements(context.getCommand().usage(), context.getCommand().permission());
        List<CommandElement> commandElements = elements.stream()
                .filter(SpongeArgumentElement.class::isInstance)
                .map(SpongeArgumentElement.class::cast)
                .map(AbstractArgumentElement::getReference)
                .collect(Collectors.toList());
        if (!elements.isEmpty()) builder.arguments(commandElements.toArray(new CommandElement[0]));

        builder.permission(context.getCommand().permission());
        builder.executor(this.buildExecutor(context, alias));

        return builder;
    }
}
