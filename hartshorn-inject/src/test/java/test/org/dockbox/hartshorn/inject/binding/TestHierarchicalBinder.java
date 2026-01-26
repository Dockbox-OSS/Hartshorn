/*
 * Copyright 2019-2026 the original author or authors.
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

package test.org.dockbox.hartshorn.inject.binding;

import org.dockbox.hartshorn.context.SimpleSingleElementContext;
import org.dockbox.hartshorn.inject.ImmutableInjectorConfiguration;
import org.dockbox.hartshorn.inject.InjectorConfiguration;
import org.dockbox.hartshorn.inject.binding.DefaultBindingAliasNormalizer;
import org.dockbox.hartshorn.inject.binding.HierarchyCache;
import org.dockbox.hartshorn.inject.binding.SimpleHierarchicalBinder;
import org.dockbox.hartshorn.inject.provider.singleton.ConcurrentHashSingletonCache;
import org.dockbox.hartshorn.inject.scope.Scope;
import org.dockbox.hartshorn.inject.scope.ScopeAdapter;
import org.dockbox.hartshorn.inject.scope.ScopeModuleContext;
import org.dockbox.hartshorn.properties.MapPropertyRegistry;
import org.dockbox.hartshorn.util.option.Option;

import java.util.UUID;

public class TestHierarchicalBinder extends SimpleHierarchicalBinder {

    private final Scope scope = ScopeAdapter.of("test-" + UUID.randomUUID());
    private HierarchyCache hierarchyCache;

    public TestHierarchicalBinder() {
        super(
            null,
            new DefaultBindingAliasNormalizer(),
            new ConcurrentHashSingletonCache()
        );
    }

    @Override
    protected Scope applicationScope() {
        return this.scope;
    }

    @Override
    public HierarchyCache hierarchyCache() {
        if (this.hierarchyCache == null) {
            this.hierarchyCache = new HierarchyCache(
                this.configuration(),
                this,
                this
            );
        }
        return this.hierarchyCache;
    }

    @Override
    protected Option<ScopeModuleContext> resolveScopeModuleContext() {
        return Option.empty();
    }

    protected InjectorConfiguration configuration() {
        return ImmutableInjectorConfiguration.create(configuration -> {
            configuration.enableStrictMode()
                    .allowFallbackToSingleConstructor()
                    .requireByDefault()
                    .disableBanner()
                    .disableBatchMode()
                    .showStacktraces();
        }).initialize(SimpleSingleElementContext.create(new MapPropertyRegistry()));
    }
}
