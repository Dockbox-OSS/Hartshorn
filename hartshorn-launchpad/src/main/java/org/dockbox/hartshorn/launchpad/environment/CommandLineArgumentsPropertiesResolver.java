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

package org.dockbox.hartshorn.launchpad.environment;

import java.util.List;
import org.dockbox.hartshorn.context.SingleElementContext;
import org.dockbox.hartshorn.launchpad.launch.ApplicationBuildContext;

/**
 * Resolves command line arguments from the {@link ApplicationBuildContext} as properties for
 * the application environment.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class CommandLineArgumentsPropertiesResolver extends AbstractCustomPropertiesResolver {

    @Override
    protected List<String> resolveStringProperties(SingleElementContext<ApplicationEnvironment> initializerContext) {
        return initializerContext.firstContext(ApplicationBuildContext.class)
            .map(ApplicationBuildContext::arguments)
            .orElseGet(List::of);
    }
}
