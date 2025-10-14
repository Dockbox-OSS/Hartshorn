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

package org.dockbox.hartshorn.hsl.modules;

import org.dockbox.hartshorn.launchpad.ApplicationContext;

/**
 * TODO: #1061 Add documentation
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class UtilityClassNativeModule extends AbstractNativeModule {

    private final Class<?> utilityClass;
    private final ApplicationContext applicationContext;

    public UtilityClassNativeModule(Class<?> utilityClass, ApplicationContext applicationContext) {
        this.utilityClass = utilityClass;
        this.applicationContext = applicationContext;
    }

    @Override
    protected Class<?> moduleClass() {
        return this.utilityClass;
    }

    @Override
    protected Object instance() {
        return null;
    }

    @Override
    public ApplicationContext applicationContext() {
        return this.applicationContext;
    }
}
