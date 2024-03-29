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

package org.dockbox.hartshorn.testsuite;

import org.dockbox.hartshorn.application.HartshornApplication;
import org.dockbox.hartshorn.application.StandardApplicationContextConstructor;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.application.environment.ContextualApplicationEnvironment;
import test.org.dockbox.hartshorn.ApplicationBatchingTest;

public class HartshornTestApplication {

    public static ApplicationContext createWithoutBasePackages() {
        return HartshornApplication.create(ApplicationBatchingTest.class, builder -> {
            builder.constructor(StandardApplicationContextConstructor.create(constructor -> {
                constructor.includeBasePackages(false);
                constructor.environment(ContextualApplicationEnvironment.create(ContextualApplicationEnvironment.Configurer::enableBatchMode));
            }));
        });
    }
}
