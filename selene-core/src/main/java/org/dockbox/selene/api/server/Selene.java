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

package org.dockbox.selene.api.server;

import com.google.inject.Injector;

import org.dockbox.selene.api.ExceptionHelper;
import org.dockbox.selene.api.MinecraftVersion;
import org.dockbox.selene.api.annotations.module.Module;
import org.dockbox.selene.api.module.ModuleManager;
import org.dockbox.selene.api.objects.Exceptional;
import org.dockbox.selene.api.objects.item.storage.MinecraftItems;
import org.dockbox.selene.api.server.bootstrap.SeleneBootstrap;
import org.dockbox.selene.api.server.config.ExceptionLevels;
import org.dockbox.selene.api.server.config.GlobalConfig;
import org.dockbox.selene.api.server.properties.InjectorProperty;
import org.dockbox.selene.api.tasks.CheckedRunnable;
import org.dockbox.selene.api.util.SeleneUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;

/** The global {@link Selene} instance used to grant access to various components. */
@SuppressWarnings("ClassWithTooManyMethods")
public final class Selene {

    private Selene() {}

    public static void handle(CheckedRunnable runnable) {
        try {
            runnable.run();
        }
        catch (Exception e) {
            handle(e);
        }
    }

    public static void handle(Throwable e) {
        handle(SeleneUtils.getFirstCauseMessage(e), e);
    }

    /**
     * Handles a given exception and message using the injected {@link ExceptionHelper} instance. Uses
     * the global preferences as defined in {@link GlobalConfig} to use either the {@link
     * ExceptionLevels#FRIENDLY} or {@link ExceptionLevels#MINIMAL} level. Additionally this also
     * performs the preference for whether or not to print stacktraces, using {@link
     * GlobalConfig#getStacktracesAllowed()}
     *
     * @param msg
     *         The message, usually provided by the developer causing the exception. Can be null.
     * @param e
     *         Zero or more exceptions (varargs)
     */
    public static void handle(@Nullable String msg, @Nullable Throwable... e) {
        ExceptionLevels level = null != getServer()
                ? getServer().getGlobalConfig().getExceptionLevel()
                : ExceptionLevels.NATIVE;
        boolean stacktraces = null == getServer() || getServer().getGlobalConfig().getStacktracesAllowed();
        for (Throwable throwable : e) level.handle(msg, throwable, stacktraces);
    }

    /**
     * Gets the instance of {@link Selene}.
     *
     * @return The {@link Selene} instance
     */
    public static SeleneBootstrap getServer() {
        return SeleneBootstrap.getInstance();
    }

    public static <T> T handle(Callable<T> callable) {
        try {
            return callable.call();
        }
        catch (Exception e) {
            handle(e);
            return null;
        }
    }

    /**
     * Gets a log instance representing the calling type.
     *
     * @return The {@link Logger}
     */
    public static Logger log() {
        StackTraceElement element = Thread.currentThread().getStackTrace()[2];
        String[] qualifiedClassName = element.getClassName().split("\\.");
        return LoggerFactory.getLogger(SeleneInformation.PROJECT_NAME + '/' + qualifiedClassName[qualifiedClassName.length - 1]);
    }

    /**
     * Provides quick access to {@link MinecraftVersion#getItems()} through {@link
     * SeleneBootstrap#getMinecraftVersion()}.
     *
     * @return The {@link MinecraftItems} instance for the active {@link MinecraftVersion}
     */
    public static MinecraftItems getItems() {
        return Selene.getServer().getMinecraftVersion().getItems();
    }

    /**
     * Attempts to look up a resource file. If the file exists it is wrapped in a {@link Exceptional}
     * and returned. If the file does not exist or is a directory, a empty {@link Exceptional} is
     * returned. If the requested file name is invalid, or {@code null}, a {@link Exceptional}
     * containing the appropriate exception is returned.
     *
     * @param name
     *         The name of the file to look up
     *
     * @return The resource file wrapped in a {@link Exceptional}, or a appropriate {@link
     *         Exceptional} (either empty or providing the appropriate exception).
     */
    public static Exceptional<Path> getResourceFile(String name) {
        try {
            InputStream in = Selene.class.getClassLoader().getResourceAsStream(name);
            byte[] buffer = new byte[in.available()];
            in.read(buffer);

            Path tempFile = Files.createTempFile(name, ".tmp");
            OutputStream outStream = new FileOutputStream(tempFile.toFile());
            outStream.write(buffer);

            return Exceptional.of(tempFile);
        }
        catch (NullPointerException | IOException e) {
            return Exceptional.of(e);
        }
    }

    /**
     * Gets an instance of a provided {@link Class} type. If the type is annotated with {@link Module}
     * it is ran through the {@link ModuleManager} instance to obtain the instance. If it is not
     * annotated as such, it is ran through the instance {@link Injector} to obtain the instance based
     * on implementation, or manually, provided mappings.
     *
     * @param <T>
     *         The type parameter for the instance to return
     * @param type
     *         The type of the instance
     * @param module
     *         The type of the module if module specific bindings are to be used
     * @param additionalProperties
     *         The properties to be passed into the type either during or after
     *         construction
     *
     * @return The instance, if present. Otherwise returns null
     */
    public static <T> T provide(Class<T> type, Class<?> module, InjectorProperty<?>... additionalProperties) {
        return Selene.getServer().getInstance(type, module, additionalProperties);
    }

    public static <T> T provide(Class<T> type, InjectorProperty<?>... additionalProperties) {
        return Selene.getServer().getInstance(type, additionalProperties);
    }

    public static <T> T provide(Class<T> type, Object module, InjectorProperty<?>... additionalProperties) {
        return Selene.getServer().getInstance(type, module, additionalProperties);
    }
}
