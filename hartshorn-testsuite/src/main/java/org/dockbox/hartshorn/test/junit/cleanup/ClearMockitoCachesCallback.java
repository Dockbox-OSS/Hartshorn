package org.dockbox.hartshorn.test.junit.cleanup;

import java.lang.reflect.Method;

import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.extension.ExtensionContext;

public class ClearMockitoCachesCallback implements HartshornCleanupCallback {

    @Override
    public void closeAfterLifecycle(ExtensionContext context) throws Exception {
        Option<Class<?>> mockito = TypeUtils.forName("org.mockito.Mockito");
        if (mockito.present()) {
            Method clearAllCaches = mockito.get().getMethod("clearAllCaches");
            clearAllCaches.invoke(null);
        }
    }
}
