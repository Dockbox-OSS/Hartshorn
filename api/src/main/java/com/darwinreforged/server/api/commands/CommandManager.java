package com.darwinreforged.server.api.commands;

import com.darwinreforged.server.api.commands.annotations.Command;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class CommandManager {

    public synchronized void registerCommandClass(Class clazz, Object plugin) {
        if (clazz.isAnnotationPresent(Command.class)) {
            Annotation annotation = clazz.getAnnotation(Command.class);

            if (annotation instanceof Command) {
                Command parent = (Command) annotation;
                String parentCommand = parent.command();
                if (parentCommand.equals("$none")) return;

                String parentPermission = parent.permission();
                String parentDescription = parent.description();
                AtomicReference<Method> parentExecutor = new AtomicReference<>();
                parentExecutor.set(null);

                Map<String, CommandSpec> children = new HashMap<>();

                Arrays.stream(clazz.getDeclaredMethods()).filter(method -> method.isAnnotationPresent(Command.class)).forEach(method -> {
                    method.setAccessible(true);
                    Command command = method.getAnnotation(Command.class);
                    if (command.command().equals("$none")) return;

                    List<CommandElement> arguments = new ArrayList<>();
                    Arrays.stream(command.arguments()).forEach(arg -> {
                        CommandElement element;
                        switch (arg.type()) {
                            case INTEGER:
                                element = GenericArguments.integer(Text.of(arg.value()));
                                break;
                            default:
                            case STRING:
                                element = GenericArguments.string(Text.of(arg.value()));
                                break;
                            case BIGDECIMAL:
                                element = GenericArguments.bigDecimal(Text.of(arg.value()));
                                break;
                            case BIGINTEGER:
                                element = GenericArguments.bigInteger(Text.of(arg.value()));
                                break;
                            case BOOLEAN:
                                element = GenericArguments.bool(Text.of(arg.value()));
                                break;
                            case PLAYER:
                                element = GenericArguments.user(Text.of(arg.value()));
                                break;
                            case JOIN:
                                element = GenericArguments.remainingJoinedStrings(Text.of(arg.value()));
                                break;
                        }

                        if (arg.optional()) element = GenericArguments.optional(element);
                        arguments.add(element);
                    });
                    if (command.isParent()) {
                        parentExecutor.set(method);
                    } else {
                        CommandSpec.Builder builder = CommandSpec.builder();
                        builder.description(Text.of(command.description()))
                                .arguments((CommandElement[]) arguments.toArray())
                                .executor(new InternalCommandExecutor(method));
                        if (!command.permission().equals("$none")) builder.permission(command.permission());

                        children.put(command.command(), builder.build());
                    }

                });

                CommandSpec.Builder builder = CommandSpec.builder();
                if (parentExecutor.get() != null) builder.executor(new InternalCommandExecutor(parentExecutor.get()));

                builder.permission(parentPermission);
                builder.description(Text.of(parentDescription));
                children.forEach((key, child) -> builder.child(child, key));
                CommandSpec command = builder.build();

                Sponge.getCommandManager().register(plugin, command, parentCommand);
            }
        }
    }

    private static class InternalCommandExecutor implements CommandExecutor {

        Method method;

        InternalCommandExecutor(Method method) {
            this.method = method;
        }

        @Override
        public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
            try {
                Object returnedVal = method.invoke(src, args);
                if (returnedVal instanceof CommandResult) return (CommandResult) returnedVal;
            } catch (IllegalAccessException | InvocationTargetException ex) {
        System.out.println(ex.getMessage());
            }
            return CommandResult.success();
        }
    }
}
