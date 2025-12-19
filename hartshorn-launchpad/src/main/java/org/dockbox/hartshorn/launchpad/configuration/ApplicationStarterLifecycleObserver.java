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

package org.dockbox.hartshorn.launchpad.configuration;

import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.launchpad.ApplicationStarter;
import org.dockbox.hartshorn.launchpad.lifecycle.LifecycleObserver;
import org.dockbox.hartshorn.util.ApplicationException;

/**
 * A lifecycle observer that runs the {@link ApplicationStarter} when the application has started.
 * The lookup for the {@link ApplicationStarter} is explicitly fuzzy, to allow for minimum
 * boilerplate for simple applications.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
class ApplicationStarterLifecycleObserver implements LifecycleObserver {

    @Override
    public void onStarted(ApplicationContext applicationContext) {
        // Late lookup for ApplicationStarter, to allow for maximum flexibility early in the
        // application lifecycle.
        ComponentKey<ApplicationStarter> componentKey = ComponentKey
                .builder(ApplicationStarter.class)
                .fuzzy()
                .optional()
                .build();
        ApplicationStarter applicationStarter = applicationContext.get(componentKey);
        // OK to do nothing if no ApplicationStarter is present, as this is optional. Other
        // observers may still be present, and will be invoked.
        if (applicationStarter != null) {
            try {
                applicationStarter.run(applicationContext);
            } catch (ApplicationException e) {
                applicationContext.handle(e);
            }
        }
    }
}
