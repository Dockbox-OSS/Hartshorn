package org.dockbox.darwin.core.server;

import com.google.inject.Guice;
import com.google.inject.Injector;

import org.dockbox.darwin.core.util.exceptions.ExceptionHelper;
import org.dockbox.darwin.core.util.inject.AbstractExceptionInjector;
import org.dockbox.darwin.core.util.inject.AbstractModuleInjector;
import org.dockbox.darwin.core.util.inject.AbstractUtilInjector;
import org.dockbox.darwin.core.util.module.ModuleLoader;
import org.dockbox.darwin.core.util.module.ModuleScanner;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Properties;

public abstract class CoreServer implements Server {

    private final Logger log = LoggerFactory.getLogger(CoreServer.class);
    private final String version;
    private final Date lastUpdate;
    private final String[] authors = {"GuusLieben"};

    private static Server instance;

    private final ExceptionHelper exceptionHelper;
    private final ModuleLoader moduleLoader;
    private final ModuleScanner moduleScanner;

    public CoreServer(
            AbstractModuleInjector moduleInjector,
            AbstractExceptionInjector exceptionInjector,
            AbstractUtilInjector utilInjector
            ) {

        String tVer = "dev";
        Date tLU = Date.from(Instant.now());

        try {
            Properties properties = new Properties();
            properties.load(getClass().getResourceAsStream("/darwin.properties"));

            DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            tLU = format.parse(properties.getOrDefault("last_update", Instant.now().toString()).toString());
            tVer = properties.getOrDefault("version", "dev").toString();
        } catch (IOException | ParseException e) {
            except("Failed to convert resource file", e);
        }

        this.version = tVer;
        this.lastUpdate = tLU;

        Injector injector = Guice.createInjector();
        if (moduleInjector != null) injector = injector.createChildInjector(moduleInjector);
        if (exceptionInjector != null) injector = injector.createChildInjector(exceptionInjector);
        if (utilInjector != null) injector = injector.createChildInjector(utilInjector);

        this.exceptionHelper = injector.getInstance(ExceptionHelper.class);
        this.moduleLoader = injector.getInstance(ModuleLoader.class);
        this.moduleScanner = injector.getInstance(ModuleScanner.class);


        CoreServer.instance = this;
    }

    public static ExceptionHelper getExceptionHelper() {
        return ((CoreServer) getServer()).exceptionHelper;
    }

    public static ModuleLoader getModuleLoader() {
        return ((CoreServer) getServer()).moduleLoader;
    }

    public static ModuleScanner getModuleScanner() {
        return ((CoreServer) getServer()).moduleScanner;
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
    public Date getLastUpdate() {
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
            // if config allows stacktraces :
            boolean stacktraces = true;
            // if config allows friendly :
            this.exceptionHelper.printFriendly(msg, throwable, stacktraces);
        }
    }

    public static Logger log() {
        return getServer().getLog();
    }

    public static Server getServer() {
        return instance;
    }
}
