package org.dockbox.hartshorn.commands;

import org.dockbox.hartshorn.core.Key;
import org.dockbox.hartshorn.core.binding.Providers;
import org.dockbox.hartshorn.testsuite.ApplicationAwareTest;
import org.dockbox.hartshorn.testsuite.JUnitInjector;
import org.junit.jupiter.api.BeforeAll;

public class HartshornCommandTest extends ApplicationAwareTest {

    @BeforeAll
    public static void register() {
        JUnitInjector.register(Key.of(SystemSubject.class), Providers.of(JUnitSystemSubject.class));
    }
}
