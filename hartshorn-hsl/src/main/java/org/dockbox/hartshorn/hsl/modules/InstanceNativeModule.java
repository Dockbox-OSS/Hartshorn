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

package org.dockbox.hartshorn.hsl.modules;

import org.dockbox.hartshorn.launchpad.ApplicationContext;

import java.util.Objects;

/**
 * Implementation of {@link AbstractNativeModule} which provides the instance of the module
 * based on a predefined instance.
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public class InstanceNativeModule extends AbstractNativeModule {

    private final Class<?> moduleClass;
    private final Object instance;
    private final ApplicationContext applicationContext;

    public InstanceNativeModule(ApplicationContext applicationContext, Object instance) {
        this.instance = Objects.requireNonNull(instance);
        this.moduleClass = instance.getClass();
        this.applicationContext = applicationContext;
    }

    @Override
    protected Class<?> moduleClass() {
        return this.moduleClass;
    }

    @Override
    protected Object instance() {
        return this.instance;
    }

    @Override
    public ApplicationContext applicationContext() {
        return this.applicationContext;
    }
}
