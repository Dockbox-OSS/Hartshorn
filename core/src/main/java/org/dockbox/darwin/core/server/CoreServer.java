package org.dockbox.darwin.core.server;

import com.google.inject.Guice;
import com.google.inject.Injector;

import org.dockbox.darwin.core.util.exceptions.ExceptionHelper;
import org.dockbox.darwin.core.util.inject.AbstractExceptionInjector;
import org.dockbox.darwin.core.util.inject.AbstractModuleInjector;
import org.dockbox.darwin.core.util.inject.AbstractUtilInjector;
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

    private static CoreServer instance;

    private Injector injector;

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

        this.injector = Guice.createInjector();
        if (moduleInjector != null) this.injector = this.injector.createChildInjector(moduleInjector);
        if (exceptionInjector != null) this.injector = this.injector.createChildInjector(exceptionInjector);
        if (utilInjector != null) this.injector = this.injector.createChildInjector(utilInjector);

        CoreServer.instance = this;
    }

    public static <T> T getInstance(Class<T> type) {
        return instance.injector.getInstance(type);
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
            getInstance(ExceptionHelper.class).printFriendly(msg, throwable, stacktraces);
        }
    }

    public static Logger log() {
        return getServer().getLog();
    }

    public static Server getServer() {
        return instance;
    }
}
