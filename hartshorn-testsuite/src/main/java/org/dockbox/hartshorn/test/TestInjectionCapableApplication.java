/*
 * Copyright 2019-2024 the original author or authors.
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

package org.dockbox.hartshorn.test;

import java.util.Properties;

import org.dockbox.hartshorn.context.DefaultContext;
import org.dockbox.hartshorn.inject.ApplicationPropertyHolder;
import org.dockbox.hartshorn.inject.InjectionCapableApplication;
import org.dockbox.hartshorn.inject.InjectorEnvironment;
import org.dockbox.hartshorn.inject.binding.HierarchicalBinder;
import org.dockbox.hartshorn.inject.provider.ComponentProvider;
import org.dockbox.hartshorn.util.option.Option;

public class TestInjectionCapableApplication
        extends DefaultContext
        implements InjectionCapableApplication {

    private final InjectorEnvironment environment = new TestInjectorEnvironment();
    private final ComponentProvider provider = null; // TODO: Nothing in hartshorn-inject, need to implement
    private final HierarchicalBinder binder = null; // TODO: Nothing in hartshorn-inject, need to implement

    @Override
    public InjectorEnvironment environment() {
        return this.environment;
    }

    @Override
    public ComponentProvider defaultProvider() {
        return null;
    }

    @Override
    public HierarchicalBinder defaultBinder() {
        return null;
    }

    @Override
    public ApplicationPropertyHolder properties() {
        // TODO: JDKPropertiesPropertyHolder?
        return new ApplicationPropertyHolder() {

            @Override
            public Properties properties() {
                return new Properties();
            }

            @Override
            public Option<String> property(String key) {
                return Option.empty();
            }
        };
    }
}
