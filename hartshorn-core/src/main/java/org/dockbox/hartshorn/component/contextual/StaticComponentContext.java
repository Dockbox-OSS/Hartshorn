/*
 * Copyright 2019-2023 the original author or authors.
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

package org.dockbox.hartshorn.component.contextual;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.context.ContextKey;
import org.dockbox.hartshorn.context.InstallIfAbsent;
import org.dockbox.hartshorn.context.DefaultApplicationAwareContext;
import org.dockbox.hartshorn.reporting.DiagnosticsPropertyCollector;
import org.dockbox.hartshorn.reporting.Reportable;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import jakarta.inject.Inject;

@InstallIfAbsent
public class StaticComponentContext extends DefaultApplicationAwareContext implements StaticComponentCollector, Reportable {

    public static final ContextKey<StaticComponentContext> CONTEXT_KEY = ContextKey.builder(StaticComponentContext.class)
            .fallback(StaticComponentContext::new)
            .build();

    private final List<StaticComponentContainer<?>> staticComponentContainers = new CopyOnWriteArrayList<>();

    @Inject
    public StaticComponentContext(final ApplicationContext applicationContext) {
        super(applicationContext);
    }

    public StaticComponentProvider provider() {
        return new ContextStaticComponentProvider(this);
    }

    public List<StaticComponentContainer<?>> containers() {
        return this.staticComponentContainers;
    }

    @Override
    public <T> StaticComponentContainer<T> register(final T instance, final Class<T> type, final String id) {
        final TypeView<T> typeView = this.applicationContext().environment().introspector().introspect(type);
        final StaticComponentContainer<T> componentReference = new StaticComponentContainer<>(instance, typeView, id);
        this.staticComponentContainers.add(componentReference);
        return componentReference;
    }

    @Override
    public <T> Set<StaticComponentContainer<T>> register(final Class<T> type, final Collection<T> components, final String id) {
        return components.stream()
                .map(component -> this.register(component, type, id))
                .collect(Collectors.toSet());
    }

    @Override
    public void unregister(final StaticComponentContainer<?> componentReference) {
        this.staticComponentContainers.remove(componentReference);
    }

    @Override
    public void report(final DiagnosticsPropertyCollector collector) {
        final Reportable[] reporters = this.staticComponentContainers.stream()
                .map(StaticComponentContainerReportable::new)
                .toArray(Reportable[]::new);
        collector.property("beans").write(reporters);
    }
}
