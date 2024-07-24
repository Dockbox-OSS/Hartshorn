package org.dockbox.hartshorn.test.junit;

import org.dockbox.hartshorn.test.junit.cleanup.ClearMockitoCachesCallback;
import org.dockbox.hartshorn.test.junit.cleanup.HartshornCleanupCallback;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class HartshornJUnitCleanupCallback implements BeforeTestExecutionCallback, AfterAllCallback, AfterEachCallback {

    @Override
    public void beforeTestExecution(ExtensionContext context) throws Exception {
        HartshornJUnitNamespace.registerCleanupCallback(new ClearMockitoCachesCallback(), context);
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        this.afterLifecycle(context);
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        if (JUnitTestUtilities.isClassLifecycle(context)) {
            this.afterLifecycle(context);
        }
    }

    private void afterLifecycle(ExtensionContext context) throws Exception {
        for(HartshornCleanupCallback callback : HartshornJUnitNamespace.collectCleanupCallbacks(context)) {
            callback.closeAfterLifecycle(context);
        }
        // Always last, in case the application is used by the callbacks
        tryCloseApplication(context);
    }

    private void tryCloseApplication(ExtensionContext context) throws Exception {
        Option<AutoCloseable> closeableApplication = HartshornJUnitNamespace
                .applicationIfPresent(context)
                .ofType(AutoCloseable.class);

        if (closeableApplication.present()) {
            closeableApplication.get().close();
        }
    }
}
