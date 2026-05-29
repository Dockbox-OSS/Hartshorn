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

package org.dockbox.hartshorn.launchpad.test;

import org.dockbox.hartshorn.inject.DefaultInjectionApplicationAwareContext;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.launchpad.context.ApplicationContextCarrier;

/**
 * Component carrying the {@link ApplicationContext} for a test application. This component should
 * only ever be present when an application is bootstrapped through the Hartshorn Test Suite.
 *
 * @see RequiresTestApplication
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class ApplicationTestManager extends DefaultInjectionApplicationAwareContext
        implements ApplicationContextCarrier {

    protected ApplicationTestManager(ApplicationContext application) {
        super(application);
    }

    @Override
    public ApplicationContext application() {
        return (ApplicationContext) super.application();
    }

    @Override
    public ApplicationContext applicationContext() {
        return this.application();
    }
}
