/*
 * Copyright 2019-2022 the original author or authors.
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

package org.dockbox.hartshorn.beans;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.context.AutoCreating;
import org.dockbox.hartshorn.context.DefaultApplicationAwareContext;
import org.dockbox.hartshorn.reporting.DiagnosticsPropertyCollector;
import org.dockbox.hartshorn.reporting.DiagnosticsReporter;
import org.dockbox.hartshorn.reporting.Reportable;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import jakarta.inject.Inject;

@AutoCreating
public class BeanContext extends DefaultApplicationAwareContext implements BeanCollector, Reportable {

    private final List<BeanReference<?>> beans = new CopyOnWriteArrayList<>();

    @Inject
    public BeanContext(final ApplicationContext applicationContext) {
        super(applicationContext);
    }

    public BeanProvider provider() {
        return new ContextBeanProvider(this);
    }

    public List<BeanReference<?>> beans() {
        return this.beans;
    }

    @Override
    public <T> BeanReference<T> register(final T bean, final Class<T> type, final String id) {
        final TypeView<T> typeView = this.applicationContext().environment().introspect(type);
        final BeanReference<T> beanReference = new BeanReference<>(bean, typeView, id);
        this.beans.add(beanReference);
        return beanReference;
    }

    @Override
    public <T> Set<BeanReference<T>> register(final Class<T> type, final Collection<T> beans, final String id) {
        return beans.stream()
                .map(b -> this.register(b, type, id))
                .collect(Collectors.toSet());
    }

    @Override
    public void unregister(final BeanReference<?> beanReference) {
        this.beans.remove(beanReference);
    }

    @Override
    public void report(final DiagnosticsPropertyCollector collector) {
        final DiagnosticsReporter[] reporters = this.beans.stream().map(beanReference -> (DiagnosticsReporter) c -> {
            c.property("type").write(beanReference.type().qualifiedName());
            c.property("id").write(beanReference.id());
            c.property("instance").write(beanReference.bean().toString());
        }).toArray(DiagnosticsReporter[]::new);
        collector.property("beans").write(reporters);
    }
}
