package org.dockbox.hartshorn.test.junit.cleanup;

import org.junit.jupiter.api.extension.ExtensionContext;

public interface HartshornCleanupCallback {

    void closeAfterLifecycle(ExtensionContext context) throws Exception;
}
