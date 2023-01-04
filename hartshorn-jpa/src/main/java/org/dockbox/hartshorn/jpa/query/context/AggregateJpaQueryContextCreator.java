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

package org.dockbox.hartshorn.jpa.query.context;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.proxy.MethodInterceptorContext;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

public class AggregateJpaQueryContextCreator implements JpaQueryContextCreator {

    private final Set<PriorityEntry> factories = new ConcurrentSkipListSet<>();

    @Override
    public <T> Option<JpaQueryContext> create(final ApplicationContext applicationContext,
                                              final MethodInterceptorContext<T, ?> interceptorContext,
                                              final TypeView<?> entityType, final Object persistenceCapable) {

        for (final PriorityEntry entry : this.factories) {
            final Option<JpaQueryContext> context = entry.factory().create(applicationContext, interceptorContext, entityType, persistenceCapable);
            if (context.present()) return context;
        }
        return Option.empty();
    }

    @Override
    public <T> boolean supports(final ComponentProcessingContext<T> processingContext, final MethodView<T, ?> method) {
        return this.factories.stream().anyMatch(entry -> entry.factory().supports(processingContext, method));
    }

    public void register(final int priority, final JpaQueryContextCreator factory) {
        this.factories.add(PriorityEntry.of(priority, factory));
    }

    private static class PriorityEntry implements Comparable<PriorityEntry> {
        private final int priority;
        private final JpaQueryContextCreator factory;

        private PriorityEntry(final int priority, final JpaQueryContextCreator factory) {
            this.priority = priority;
            this.factory = factory;
        }

        public int priority() {
            return this.priority;
        }

        public JpaQueryContextCreator factory() {
            return this.factory;
        }

        public static PriorityEntry of(final int priority, final JpaQueryContextCreator factory) {
            return new PriorityEntry(priority, factory);
        }

        @Override
        public int compareTo(@NonNull final PriorityEntry o) {
            return Integer.compare(this.priority, o.priority);
        }
    }
}
