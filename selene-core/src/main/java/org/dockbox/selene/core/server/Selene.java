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

package org.dockbox.selene.core.server;

import org.dockbox.selene.core.ExceptionHelper;
import org.dockbox.selene.core.MinecraftVersion;
import org.dockbox.selene.core.objects.Exceptional;
import org.dockbox.selene.core.objects.item.storage.MinecraftItems;
import org.dockbox.selene.core.server.config.ExceptionLevels;
import org.dockbox.selene.core.server.config.GlobalConfig;
import org.dockbox.selene.core.util.SeleneUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * The global {@link Selene} instance used to grant access to various components.
 */
@SuppressWarnings("ClassWithTooManyMethods")
public final class Selene {

    private Selene() {}

    /**
     * Gets the instance of {@link Selene}.
     *
     * @return The {@link Selene} instance
     */
    public static SeleneBootstrap getServer() {
        return SeleneBootstrap.getInstance();
    }

    /**
     * Handles a given exception and message using the injected {@link ExceptionHelper} instance. Uses the global
     * preferences as defined in {@link GlobalConfig} to use either the {@link ExceptionLevels#FRIENDLY} or
     * {@link ExceptionLevels#MINIMAL} level. Additionally this also performs the preference for whether or not to
     * print stacktraces, using {@link GlobalConfig#getStacktracesAllowed()}
     *
     * @param msg
     *         The message, usually provided by the developer causing the exception. Can be null.
     * @param e
     *         Zero or more exceptions (varargs)
     */
    public static void handle(@Nullable String msg, @Nullable Throwable... e) {
        if (null != getServer()) {
            for (Throwable throwable : e) {
                boolean stacktraces = getServer().getGlobalConfig().getStacktracesAllowed();
                ExceptionHelper eh = SeleneUtils.INJECT.getInstance(ExceptionHelper.class);

                if (ExceptionLevels.FRIENDLY == getServer().getGlobalConfig().getExceptionLevel()) {
                    eh.printFriendly(msg, throwable, stacktraces);
                } else {
                    eh.printMinimal(msg, throwable, stacktraces);
                }
            }
        } else {
            log().error("Selene has not been initialised! Logging natively");
            log().error(msg);
            for (Throwable throwable : e) log().error(Arrays.toString(throwable.getStackTrace()));
        }
    }

    public static void handle(Throwable e) {
        handle(e.getMessage(), e);
    }

    /**
     * Provides quick access to {@link SeleneBootstrap#log()}. Primarily added to avoid {@link SeleneBootstrap#log()}
     * from being used globally.
     *
     * @return The {@link Logger}
     */
    public static Logger log() {
        return SeleneBootstrap.log();
    }

    /**
     * Provides quick access to {@link MinecraftVersion#getItems()} through {@link SeleneBootstrap#getMinecraftVersion()}.
     *
     * @return The {@link MinecraftItems} instance for the active {@link MinecraftVersion}
     */
    public static MinecraftItems getItems() {
        return SeleneBootstrap.getInstance().getMinecraftVersion().getItems();
    }

    /**
     * Attempts to look up a resource file. If the file exists it is wrapped in a {@link Exceptional} and returned. If
     * the file does not exist or is a directory, a empty {@link Exceptional} is returned. If the requested file name is
     * invalid, or {@code null}, a {@link Exceptional} containing the appropriate exception is returned.
     *
     * @param name
     *         The name of the file to look up
     *
     * @return The resource file wrapped in a {@link Exceptional}, or a appropriate {@link Exceptional} (either empty or
     * providing the appropriate exception).
     */
    public static Exceptional<Path> getResourceFile(String name) {
        try {
            URL resourceUrl = Selene.class.getClassLoader().getResource(name);
            // Warning suppressed relates to potential NPE on `resourceUrl#toURI`. This is caught and handled directly
            // and can thus be suppressed safely.
            @SuppressWarnings("ConstantConditions") Path resourcePath = Paths.get(resourceUrl.toURI());
            File resourceFile = resourcePath.toFile();
            if (resourceFile.isFile()) {
                return Exceptional.of(resourcePath);
            }
        } catch (URISyntaxException | NullPointerException e) {
            return Exceptional.of(e);
        }
        return Exceptional.empty();
    }

}
