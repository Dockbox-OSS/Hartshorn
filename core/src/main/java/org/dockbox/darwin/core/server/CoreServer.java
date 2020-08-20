package org.dockbox.darwin.core.server;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import org.dockbox.darwin.core.annotations.Module;
import org.dockbox.darwin.core.util.exceptions.ExceptionHelper;
import org.dockbox.darwin.core.util.inject.AbstractCommonInjector;
import org.dockbox.darwin.core.util.inject.AbstractExceptionInjector;
import org.dockbox.darwin.core.util.inject.AbstractModuleInjector;
import org.dockbox.darwin.core.util.inject.AbstractUtilInjector;
import org.dockbox.darwin.core.util.library.LibraryArtifact;
import org.dockbox.darwin.core.util.library.LibraryLoader;
import org.dockbox.darwin.core.util.module.ModuleLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public abstract class CoreServer<L> implements Server {

    private final Logger log = LoggerFactory.getLogger(CoreServer.class);
    private String version;
    private Date lastUpdate;
    private final String[] authors = {"GuusLieben"};

    private static CoreServer<?> instance;

    private Injector injector;

    public CoreServer(AbstractCommonInjector injector) {
        this.injector = Guice.createInjector(injector);
        construct();
    }

    public CoreServer(
            AbstractModuleInjector moduleInjector,
            AbstractExceptionInjector exceptionInjector,
            AbstractUtilInjector utilInjector
    ) {
        this.injector = Guice.createInjector();
        if (moduleInjector != null) this.injector = this.injector.createChildInjector(moduleInjector);
        if (exceptionInjector != null) this.injector = this.injector.createChildInjector(exceptionInjector);
        if (utilInjector != null) this.injector = this.injector.createChildInjector(utilInjector);

        construct();
    }

    protected void construct() {
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

        //noinspection unchecked
        this.injector.getInstance(LibraryLoader.class).configure(getLoader(), getAllArtifacts());

        CoreServer.instance = this;
    }

    protected abstract L getLoader();

    public static <T> T getInstance(Class<T> type) {
        if (type.isAnnotationPresent(Module.class)) {
            return getInstance(ModuleLoader.class).getModuleInstance(type).orElse(null);
        }
        return instance.injector.getInstance(type);
    }

    public static <T> void bindUtility(Class<T> contract, Class<? extends T> implementation) {
        AbstractModule localModule = new AbstractModule() {
            @Override
            protected void configure() {
                super.configure();
                bind(contract).to(implementation);
            }
        };
        instance.injector = instance.injector.createChildInjector(localModule);
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

    private LibraryArtifact[] getAllArtifacts() {
        List<LibraryArtifact> artifacts = new ArrayList<>(Arrays.asList(getArtifacts()));
        artifacts.add(new LibraryArtifact("org.reflections", "reflections", "0.9.11"));
        artifacts.add(new LibraryArtifact("com.fasterxml.jackson.core", "jackson-databind", "2.9.8"));
        artifacts.add(new LibraryArtifact("com.fasterxml.jackson.dataformat", "jackson-dataformat-yaml", "2.9.8"));
        artifacts.add(new LibraryArtifact("com.fasterxml.jackson.datatype", "jackson-datatype-jsr310", "2.9.8"));
        artifacts.add(new LibraryArtifact("org.apache.commons", "commons-collections4", "4.1"));
        return artifacts.toArray(new LibraryArtifact[0]);
    }

    protected abstract LibraryArtifact[] getArtifacts();

}
