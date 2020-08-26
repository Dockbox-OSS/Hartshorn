package org.dockbox.darwin.core.util.inject

import com.google.inject.AbstractModule
import net.byteflux.libby.LibraryManager
import org.dockbox.darwin.core.i18n.I18nService
import org.dockbox.darwin.core.server.config.GlobalConfig
import org.dockbox.darwin.core.util.discord.DiscordUtils
import org.dockbox.darwin.core.util.events.EventBus
import org.dockbox.darwin.core.util.exceptions.ExceptionHelper
import org.dockbox.darwin.core.util.files.ConfigManager
import org.dockbox.darwin.core.util.files.DataManager
import org.dockbox.darwin.core.util.files.FileUtils
import org.dockbox.darwin.core.util.module.ModuleLoader
import org.dockbox.darwin.core.util.module.ModuleScanner
import org.dockbox.darwin.core.util.player.PlayerStorageService
import org.dockbox.darwin.core.util.text.BroadcastService

abstract class AbstractCommonInjector : AbstractModule() {

    override fun configure() {
        super.configure()
        this.configureExceptionInject()
        this.configureModuleInject()
        this.configureUtilInject()
    }

    protected abstract fun configureExceptionInject()
    protected abstract fun configureModuleInject()
    protected abstract fun configureUtilInject()

    companion object {
        val requiredBindings: Array<Class<*>> = arrayOf(
                ExceptionHelper::class.java,
                ModuleLoader::class.java,
                ModuleScanner::class.java,
                FileUtils::class.java,
                ConfigManager::class.java,
                DataManager::class.java,
                EventBus::class.java,
                DiscordUtils::class.java,
                LibraryManager::class.java,
                BroadcastService::class.java,
                PlayerStorageService::class.java,
                I18nService::class.java,
                GlobalConfig::class.java
        )
    }
}
