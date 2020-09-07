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

package org.dockbox.darwin.core.server;

import com.google.inject.AbstractModule;
import com.google.inject.ConfigurationException;
import com.google.inject.Guice;
import com.google.inject.Injector;

import org.dockbox.darwin.core.server.config.GlobalConfig;
import org.dockbox.darwin.core.server.config.GlobalConfig.ExceptionLevels;
import org.dockbox.darwin.core.util.exceptions.ExceptionHelper;
import org.dockbox.darwin.core.util.extension.Extension;
import org.dockbox.darwin.core.util.extension.ExtensionContext;
import org.dockbox.darwin.core.util.extension.ExtensionManager;
import org.dockbox.darwin.core.util.inject.AbstractCommonInjector;
import org.dockbox.darwin.core.util.inject.AbstractExceptionInjector;
import org.dockbox.darwin.core.util.inject.AbstractModuleInjector;
import org.dockbox.darwin.core.util.inject.AbstractUtilInjector;
import org.dockbox.darwin.core.util.library.LibraryArtifact;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;

@Extension(id = "darwinserver", name = "Darwin Server", description = "The global module used for configuration purposes", authors = "GuusLieben")
public abstract class Server implements KServer {

    private final Logger log = LoggerFactory.getLogger(Server.class);
    private String version;
    private LocalDate lastUpdate;
    protected static final String[] authors = {"GuusLieben"};

    private static Server instance;

    private Injector injector;

    protected Server(AbstractCommonInjector injector) {
        this.injector = Guice.createInjector(injector);
        this.construct();
    }

    protected Server(
            AbstractModuleInjector moduleInjector,
            AbstractExceptionInjector exceptionInjector,
            AbstractUtilInjector utilInjector
    ) {
        this.injector = Guice.createInjector();
        if (null != moduleInjector) this.injector = this.injector.createChildInjector(moduleInjector);
        if (null != exceptionInjector) this.injector = this.injector.createChildInjector(exceptionInjector);
        if (null != utilInjector) this.injector = this.injector.createChildInjector(utilInjector);

        this.verifyInjectorBindings();
        this.construct();
    }

    private void verifyInjectorBindings() {
        for (Class<?> bindingType : AbstractCommonInjector.Companion.getRequiredBindings()) {
            try {
                this.injector.getBinding(bindingType);
            } catch (ConfigurationException e) {
                log().error("Missing binding for " + bindingType.getCanonicalName() + "! While it is possible to inject it later, it is recommended to do so through the default platform injector!");
            }
        }
    }

    protected void construct() {
        String tVer = "dev";
        LocalDate tLU = LocalDate.from(Instant.now());

        try {
            Properties properties = new Properties();
            properties.load(this.getClass().getResourceAsStream("/darwin.properties"));

            DateTimeFormatter format = DateTimeFormatter.BASIC_ISO_DATE;
            tLU = LocalDate.parse(properties.getOrDefault("last_update", Instant.now().toString()).toString(), format);

            tVer = properties.getOrDefault("version", "dev").toString();
        } catch (IOException e) {
            this.except("Failed to convert resource file", e);
        }

        this.version = tVer;
        this.lastUpdate = tLU;

        Server.instance = this;
    }

    public static <T> T getInstance(Class<T> type) {
        if (type.isAnnotationPresent(Extension.class)) {
            return getInstance(ExtensionManager.class).getInstance(type).orElse(null);
        }
        return instance.injector.getInstance(type);
    }

    public static <T> void bindUtility(Class<T> contract, Class<? extends T> implementation) {
        AbstractModule localModule = new AbstractModule() {
            @Override
            protected void configure() {
                super.configure();
                this.bind(contract).to(implementation);
            }
        };
        instance.injector = instance.injector.createChildInjector(localModule);
    }

    public Injector getInjector() {
        return this.injector;
    }

    protected void initIntegratedExtensions(Consumer<ExtensionContext> consumer) {
        getInstance(ExtensionManager.class).collectIntegratedExtensions().forEach(consumer);
    }

    protected void initExternalExtensions(Consumer<ExtensionContext> consumer) {
        getInstance(ExtensionManager.class).getExternalExtensions().forEach(consumer);
    }

    @NotNull
    @Override
    public Logger getLog() {
        return this.log;
    }

    @NotNull
    @Override
    public String getVersion() {
        return this.version;
    }

    @NotNull
    @Override
    public LocalDate getLastUpdate() {
        return this.lastUpdate;
    }

    @NotNull
    @Override
    public String @NotNull [] getAuthors() {
        return authors;
    }

    @Override
    public void except(@Nullable String msg, @Nullable Throwable... e) {
        for (Throwable throwable : e) {
            boolean stacktraces = this.getGlobalConfig().getStacktracesAllowed();
            ExceptionHelper eh = getInstance(ExceptionHelper.class);

            if (ExceptionLevels.FRIENDLY == this.getGlobalConfig().getExceptionLevel()) {
                eh.printFriendly(msg, throwable, stacktraces);
            } else {
                eh.printMinimal(msg, throwable, stacktraces);
            }
        }
    }

    @NotNull
    @Override
    public GlobalConfig getGlobalConfig() {
        return getInstance(GlobalConfig.class);
    }

    public static Logger log() {
        return getServer().getLog();
    }

    public static KServer getServer() {
        return instance;
    }

    // TODO: Check if these are present at startup
    private LibraryArtifact[] getAllArtifacts() {
        List<LibraryArtifact> artifacts = new ArrayList<>(Arrays.asList(this.getArtifacts()));
        artifacts.add(new LibraryArtifact("org.reflections", "reflections", "0.9.11"));
        artifacts.add(new LibraryArtifact("com.fasterxml.jackson.core", "jackson-databind", "2.9.8"));
        artifacts.add(new LibraryArtifact("com.fasterxml.jackson.dataformat", "jackson-dataformat-yaml", "2.9.8"));
        artifacts.add(new LibraryArtifact("com.fasterxml.jackson.datatype", "jackson-datatype-jsr310", "2.9.8"));
        artifacts.add(new LibraryArtifact("org.apache.commons", "commons-collections4", "4.1"));
        return artifacts.toArray(new LibraryArtifact[0]);
    }

    protected abstract LibraryArtifact[] getArtifacts();

}
