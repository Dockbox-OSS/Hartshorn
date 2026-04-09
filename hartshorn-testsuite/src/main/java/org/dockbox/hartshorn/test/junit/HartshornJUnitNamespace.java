/*
 * Copyright 2019-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dockbox.hartshorn.test.junit;

import org.dockbox.hartshorn.inject.InjectionCapableApplication;
import org.dockbox.hartshorn.test.junit.cleanup.HartshornCleanupCallback;
import org.dockbox.hartshorn.util.option.Option;
import org.dockbox.hartshorn.util.stream.StreamGatherers;
import org.dockbox.hartshorn.util.types.TypeUtils;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;

import java.util.ArrayList;
import java.util.List;

class HartshornJUnitNamespace {

    static final Namespace NAMESPACE = Namespace.create(HartshornJUnitNamespace.class);

    private static final String APPLICATION = "application";

    static Store store(ExtensionContext context) {
        return context.getStore(NAMESPACE);
    }

    static InjectionCapableApplication application(ExtensionContext context) {
        InjectionCapableApplication application =
            store(context).get(APPLICATION, InjectionCapableApplication.class);
        if (application == null) {
            throw new IllegalStateException("No application found in the extension context");
        }
        return application;
    }

    static Option<InjectionCapableApplication> applicationIfPresent(ExtensionContext context) {
        InjectionCapableApplication application =
            store(context).get(APPLICATION, InjectionCapableApplication.class);
        return Option.of(application);
    }

    static void application(ExtensionContext context, InjectionCapableApplication application) {
        store(context).put(APPLICATION, application);
    }

    static List<HartshornCleanupCallback> collectCleanupCallbacks(ExtensionContext context) {
        List<?> callbacks = store(context).computeIfAbsent(
            HartshornCleanupCallback.class,
            _ -> new ArrayList<>(),
            List.class);

        return callbacks.stream()
            .gather(StreamGatherers.filterByType(HartshornCleanupCallback.class))
            .toList();
    }

    static void registerCleanupCallback(
        HartshornCleanupCallback callback,
        ExtensionContext context
    ) {
        List<HartshornCleanupCallback> callbacks = store(context).computeIfAbsent(
            HartshornCleanupCallback.class,
            _ -> new ArrayList<>(),
            TypeUtils.unchecked(List.class, Class.class));

        if (!callbacks.contains(callback)) {
            callbacks.add(callback);
        }
    }
}
