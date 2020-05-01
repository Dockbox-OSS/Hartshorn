package com.darwinreforged.server.core.util.commands;

import com.darwinreforged.server.core.util.commands.command.Command;
import com.darwinreforged.server.core.util.commands.command.CommandFactory;
import com.darwinreforged.server.core.util.commands.command.Registrar;
import com.darwinreforged.server.core.util.commands.element.ElementFactory;
import com.google.common.reflect.ClassPath;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;


public abstract class CommandManager<T extends Command> {

    private final Object owner;
    private Registrar<T> registrar;
    private CommandManager<T> bus;

    public CommandManager(Builder<T> builder) {
        this.owner = builder.owner;
        this.registrar = Registrar.of(this, builder.elementFactory, builder.commandFactory);
    }

    public CommandManager<T> getBus() {
        return bus;
    }

    public Object getOwner() {
        checkAccess();
        return owner;
    }

    public String getOwnerId() {
        return "command";
    }

    public CommandManager<T> registerPackage(Class<?> child) {
        checkAccess();
        return registerPackage(true, child);
    }

    public CommandManager<T> registerPackage(boolean recursive, Class<?> child) {
        checkAccess();
        return registerPackage(recursive, child.getPackage().getName());
    }

    public CommandManager<T> registerPackage(String path) {
        checkAccess();
        return registerPackage(true, path);
    }

    public CommandManager<T> registerPackage(boolean recurse, String path) {
        checkAccess();
        info("Scanning package %s for commands...", path);
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Collection<ClassPath.ClassInfo> classes;
            if (recurse) {
                classes = ClassPath.from(classLoader).getTopLevelClassesRecursive(path);
            } else {
                classes = ClassPath.from(classLoader).getTopLevelClasses(path);
            }
            for (ClassPath.ClassInfo info : classes) {
                try {
                    Class<?> c = Class.forName(info.getName());
                    register(c);
                } catch (ClassNotFoundException e) {
                    warn("%s", e);
                }
            }
        } catch (IOException e) {
            warn("%s", e);
        }
        return this;
    }

    public CommandManager<T> register(Class<?> c) {
        checkAccess();
        if (isValidExecutor(c)) {
            try {
                Object o = c.newInstance();
                int count = registrar.register(o);
                info("Registered %s command(s) from %s", count, o.getClass());
            } catch (IllegalAccessException | InstantiationException e) {
                warn("%s", e);
            }
        }
        return this;
    }

    public CommandManager<T> register(Object o) {
        checkAccess();
        if (isValidExecutor(o)) {
            int count = registrar.register(o);
            info("Registered %s command(s) from %s", count, o.getClass());
        }
        return this;
    }

    public void submit() {
        checkAccess();
        Collection<T> commands = registrar.build();
        for (T t : commands) {
            submit(owner, t);
        }
    }

    protected abstract void submit(Object owner, T command);

    private void checkAccess() {
        if (registrar == null) {
            throw new IllegalStateException("Attempted to access Registrar after it has been disposed!");
        }
    }

    protected void info(String message, Object... args) {
        Logger.getLogger("CommandBus").info(String.format(message, args));
    }

    protected void warn(String message, Object... args) {
        Logger.getLogger("CommandBus").log(Level.WARNING, String.format(message, args));
    }

    private static boolean isValidExecutor(Object o) {
        Class<?> c;

        if (o instanceof Class) {
            c = (Class) o;
            if (!hasDefaultConstructor(c)) {
                return false;
            }
        } else {
            c = o.getClass();
        }

        return hasAnnotatedMethods(c);
    }

    private static boolean hasDefaultConstructor(Class<?> c) {
        try {
            Constructor con = c.getConstructor();
            return Modifier.isPublic(con.getModifiers());
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    private static boolean hasAnnotatedMethods(Class<?> c) {
        while (c != Object.class) {
            for (Method method : c.getMethods()) {
                if (method.isAnnotationPresent(com.darwinreforged.server.core.util.commands.annotation.Command.class)) {
                    return true;
                }
            }
            c = c.getSuperclass();
        }
        return false;
    }

    public static <T extends Command> Builder<T> builder() {
        return new Builder<>();
    }

    public static class Builder<T extends Command> {

        private Object owner;
        private ElementFactory elementFactory = ElementFactory.create();
        private CommandFactory<T> commandFactory;

        public Builder<T> owner(Object owner) {
            this.owner = owner;
            return this;
        }

        public Builder<T> elements(ElementFactory factory) {
            this.elementFactory = factory;
            return this;
        }

        public Builder<T> commands(CommandFactory<T> factory) {
            this.commandFactory = factory;
            return this;
        }

        public <V extends CommandManager<T>> V build(Function<Builder<T>, V> function) {
            return function.apply(this);
        }
    }
}
