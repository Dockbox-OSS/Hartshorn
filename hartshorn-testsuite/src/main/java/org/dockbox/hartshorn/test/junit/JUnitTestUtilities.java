package org.dockbox.hartshorn.test.junit;

import java.util.Optional;

import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtensionContext;

class JUnitTestUtilities {

    static boolean isClassLifecycle(ExtensionContext context) {
        Optional<Lifecycle> lifecycle = context.getTestInstanceLifecycle();
        return lifecycle.isPresent() && Lifecycle.PER_CLASS == lifecycle.get();
    }
}
