package org.dockbox.darwin.sponge.util.inject;

import net.byteflux.libby.LibraryManager;
import net.byteflux.libby.SpongeLibraryManager;

import org.dockbox.darwin.core.util.discord.DiscordUtils;
import org.dockbox.darwin.core.util.events.EventBus;
import org.dockbox.darwin.core.util.events.SimpleEventBus;
import org.dockbox.darwin.core.util.exceptions.ExceptionHelper;
import org.dockbox.darwin.core.util.exceptions.SimpleExceptionHelper;
import org.dockbox.darwin.core.util.files.ConfigManager;
import org.dockbox.darwin.core.util.files.DataManager;
import org.dockbox.darwin.core.util.files.FileUtils;
import org.dockbox.darwin.core.util.files.YamlConfigManager;
import org.dockbox.darwin.core.util.files.YamlSQLiteDataManager;
import org.dockbox.darwin.core.util.inject.AbstractCommonInjector;
import org.dockbox.darwin.core.util.module.ModuleLoader;
import org.dockbox.darwin.core.util.module.ModuleScanner;
import org.dockbox.darwin.core.util.module.SimpleModuleLoader;
import org.dockbox.darwin.core.util.module.SimpleModuleScanner;
import org.dockbox.darwin.core.util.text.BroadcastService;
import org.dockbox.darwin.sponge.util.discord.SpongeDiscordUtils;
import org.dockbox.darwin.sponge.util.files.SpongeFileUtils;
import org.dockbox.darwin.core.util.text.SimpleBroadcastService;

public class SpongeCommonInjector extends AbstractCommonInjector {

    @Override
    protected void configureExceptionInject() {
        bind(ExceptionHelper.class).to(SimpleExceptionHelper.class);
    }

    @Override
    protected void configureModuleInject() {
        bind(ModuleLoader.class).to(SimpleModuleLoader.class);
        bind(ModuleScanner.class).to(SimpleModuleScanner.class);
    }

    @Override
    protected void configureUtilInject() {
        bind(FileUtils.class).to(SpongeFileUtils.class);
        bind(ConfigManager.class).to(YamlConfigManager.class);
        bind(DataManager.class).to(YamlSQLiteDataManager.class);
        bind(EventBus.class).to(SimpleEventBus.class);
        bind(DiscordUtils.class).to(SpongeDiscordUtils.class);
        bind(LibraryManager.class).to(SpongeLibraryManager.class);
        bind(BroadcastService.class).to(SimpleBroadcastService.class);
    }

}
