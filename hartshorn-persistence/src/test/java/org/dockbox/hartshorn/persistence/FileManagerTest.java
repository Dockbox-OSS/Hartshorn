package org.dockbox.hartshorn.persistence;

import org.dockbox.hartshorn.core.Key;
import org.dockbox.hartshorn.core.binding.Providers;
import org.dockbox.hartshorn.testsuite.ApplicationAwareTest;
import org.dockbox.hartshorn.testsuite.JUnitInjector;
import org.junit.jupiter.api.BeforeAll;

public class FileManagerTest extends ApplicationAwareTest {

    @BeforeAll
    public static void register() {
        JUnitInjector.register(Key.of(FileManager.class), Providers.of(JUnitFileManager.class));
    }
}
