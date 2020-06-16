package org.dockbox.darwin.sponge.util.inject;

import org.dockbox.darwin.core.util.events.EventBus;
import org.dockbox.darwin.core.util.events.SimpleEventBus;
import org.dockbox.darwin.core.util.files.ConfigManager;
import org.dockbox.darwin.core.util.files.DataManager;
import org.dockbox.darwin.core.util.files.FileUtils;
import org.dockbox.darwin.core.util.files.YamlConfigManager;
import org.dockbox.darwin.core.util.files.YamlSQLiteDataManager;
import org.dockbox.darwin.core.util.inject.AbstractUtilInjector;
import org.dockbox.darwin.sponge.util.files.SpongeFileUtils;

public class SpongeUtilInjector extends AbstractUtilInjector {

    @Override
    protected void configure() {
        bind(FileUtils.class).to(SpongeFileUtils.class);
        bind(ConfigManager.class).to(YamlConfigManager.class);
        bind(DataManager.class).to(YamlSQLiteDataManager.class);
        bind(EventBus.class).to(SimpleEventBus.class);
    }

}
