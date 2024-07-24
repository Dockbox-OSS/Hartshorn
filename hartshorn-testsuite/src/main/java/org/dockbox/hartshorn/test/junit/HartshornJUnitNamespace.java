package org.dockbox.hartshorn.test.junit;

import java.util.ArrayList;
import java.util.List;

import org.dockbox.hartshorn.inject.InjectionCapableApplication;
import org.dockbox.hartshorn.test.junit.cleanup.HartshornCleanupCallback;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;

class HartshornJUnitNamespace {

    static final Namespace NAMESPACE = Namespace.create(HartshornJUnitNamespace.class);

    private static final String APPLICATION = "application";

    static Store store(ExtensionContext context) {
        return context.getStore(NAMESPACE);
    }
    static InjectionCapableApplication application(ExtensionContext context) {
        InjectionCapableApplication application = store(context).get(APPLICATION, InjectionCapableApplication.class);
        if (application == null) {
            throw new IllegalStateException("No application found in the extension context");
        }
        return application;
    }

    static Option<InjectionCapableApplication> applicationIfPresent(ExtensionContext context) {
        InjectionCapableApplication application = store(context).get(APPLICATION, InjectionCapableApplication.class);
        return Option.of(application);
    }

    static void application(ExtensionContext context, InjectionCapableApplication application) {
        store(context).put(APPLICATION, application);
    }

    static List<HartshornCleanupCallback> collectCleanupCallbacks(ExtensionContext context) {
        List<?> callbacks = store(context).getOrComputeIfAbsent(
                HartshornCleanupCallback.class,
                key -> new ArrayList<>(),
                List.class);

        return callbacks.stream()
                .filter(HartshornCleanupCallback.class::isInstance)
                .map(HartshornCleanupCallback.class::cast)
                .toList();
    }

    static void registerCleanupCallback(HartshornCleanupCallback callback, ExtensionContext context) throws Exception {
        List<HartshornCleanupCallback> callbacks = store(context).getOrComputeIfAbsent(
                HartshornCleanupCallback.class,
                key -> new ArrayList<>(),
                List.class);

        if (!callbacks.contains(callback)) {
            callbacks.add(callback);
        }
    }
}
