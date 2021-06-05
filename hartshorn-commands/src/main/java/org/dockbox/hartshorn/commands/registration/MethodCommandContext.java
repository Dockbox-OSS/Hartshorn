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

package org.dockbox.hartshorn.commands.registration;

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.domain.Identifiable;
import org.dockbox.hartshorn.api.exceptions.Except;
import org.dockbox.hartshorn.api.i18n.common.ResourceEntry;
import org.dockbox.hartshorn.commands.CommandInterface;
import org.dockbox.hartshorn.commands.CommandUser;
import org.dockbox.hartshorn.commands.annotations.Command;
import org.dockbox.hartshorn.commands.context.CommandContext;
import org.dockbox.hartshorn.commands.exceptions.IllegalSourceException;
import org.dockbox.hartshorn.commands.source.CommandSource;
import org.dockbox.hartshorn.util.Reflect;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.List;

import lombok.Getter;

public class MethodCommandContext extends AbstractRegistrationContext {

    @Getter
    private final Method method;

    public MethodCommandContext(Command command, Method method) {
        super(command, method.getDeclaringClass());
        this.method = method;
    }

    @Override
    public Exceptional<ResourceEntry> call(CommandSource source, CommandContext context) {
        try {
            List<Object> args = this.prepareArguments(source, context);
            Object instance = this.prepareInstance();
            Command command = this.method.getAnnotation(Command.class);

            if (0 < command.cooldownDuration() && source instanceof Identifiable) {
                String registrationId = AbstractRegistrationContext.getRegistrationId((Identifiable) source, context);
                HartshornUtils.cooldown(registrationId, command.cooldownDuration(), command.cooldownUnit());
            }

            this.method.invoke(instance, HartshornUtils.toArray(Object.class, args));
            return Exceptional.none();
        }
        catch (IllegalSourceException e) {
            return Exceptional.of(e);
        }
        catch (IllegalAccessException | InvocationTargetException e) {
            Except.handle("Failed to invoke command", e.getCause());
            return Exceptional.of(e);
        }
        catch (Throwable e) {
            Except.handle("Failed to invoke command", e);
            return Exceptional.of(e);
        }
    }

    @SuppressWarnings("UnnecessaryContinue")
    private List<Object> prepareArguments(CommandSource source, CommandContext context) {
        List<Object> finalArgs = HartshornUtils.emptyList();

        for (Parameter parameter : this.getMethod().getParameters()) {
            Class<?> parameterType = parameter.getType();

            if (Reflect.eitherAssignsFrom(CommandSource.class, parameterType))
                finalArgs.add(lookupCommandSource(parameterType, source));
            else if (Reflect.eitherAssignsFrom(CommandContext.class, parameterType))
                finalArgs.add(context);

            else if (MethodCommandContext.processFlagParameters(parameter, context, finalArgs)) continue;
            else if (MethodCommandContext.processArgumentParameters(parameter, context, finalArgs)) continue;

            else throw new IllegalStateException("Method requested parameter type '" + parameterType.getSimpleName() + "' which is not provided");
        }
        return finalArgs;
    }

    private Object prepareInstance() {
        Object instance;
        if (this.getDeclaringClass().equals(Hartshorn.class) || Reflect.assignableFrom(Hartshorn.class, this.getDeclaringClass())) {
            instance = Hartshorn.server();
        }
        else {
            instance = Hartshorn.context().get(this.getDeclaringClass());
        }
        return instance;
    }

    private static CommandSource lookupCommandSource(Class<?> parameterType, CommandSource source) {
        if (Reflect.assignableFrom(CommandUser.class, parameterType) && !(source instanceof CommandUser))
            throw new IllegalSourceException("Command can only be ran by players");
        else if (Reflect.assignableFrom(CommandInterface.class, parameterType) && !(source instanceof CommandInterface))
            throw new IllegalSourceException("Command can only be ran by the console");
        return source;
    }

    private static boolean processFlagParameters(Parameter parameter, CommandContext context, Collection<Object> finalArgs) {
        String flagName = parameter.getName();
        if (context.has(flagName) && context.flag(flagName).present()) {
            finalArgs.add(context.flag(flagName).get().getValue());
            return true;
        }
        else return false;
    }

    private static boolean processArgumentParameters(Parameter parameter, CommandContext context, Collection<Object> finalArgs) {
        String argumentName = parameter.getName();
        if (context.has(argumentName) && context.argument(argumentName).present()) {
            finalArgs.add(context.argument(argumentName).get().getValue());
            return true;
        }
        else return false;
    }

    public Class<?> getDeclaringClass() {
        return this.getMethod().getDeclaringClass();
    }

    private boolean isSenderInCooldown(CommandSource sender, CommandContext ctx) {
        Command command = this.getMethod().getAnnotation(Command.class);
        if (0 >= command.cooldownDuration()) return false;
        if (sender instanceof Identifiable) {
            String registrationId = AbstractRegistrationContext.getRegistrationId((Identifiable) sender, ctx);
            return HartshornUtils.isInCooldown(registrationId);
        }
        return false;
    }

    public String getLocation() {
        return this.getDeclaringClass().getCanonicalName() + "." + this.getMethod().getName();
    }
}
