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

package org.dockbox.hartshorn.testsuite;

import org.dockbox.hartshorn.application.StandardApplicationBuilder;
import org.dockbox.hartshorn.application.StandardApplicationContextFactory;
import org.dockbox.hartshorn.application.context.SimpleApplicationContext;
import org.dockbox.hartshorn.application.environment.ContextualApplicationEnvironment;
import org.dockbox.hartshorn.util.Customizer;

public final class TestCustomizer<T> {

    public static final TestCustomizer<StandardApplicationBuilder.Configurer> BUILDER = new TestCustomizer<>();
    public static final TestCustomizer<ContextualApplicationEnvironment.Configurer> ENVIRONMENT = new TestCustomizer<>();
    public static final TestCustomizer<StandardApplicationContextFactory.Configurer> CONSTRUCTOR = new TestCustomizer<>();
    public static final TestCustomizer<SimpleApplicationContext.Configurer> APPLICATION_CONTEXT = new TestCustomizer<>();

    private Customizer<T> customizer = Customizer.useDefaults();

    private TestCustomizer() {
        // Private constructor, use static instances
    }

    public void compose(Customizer<T> customizer) {
        this.customizer = this.customizer.compose(customizer);
    }

    Customizer<T> customizer() {
        return this.customizer;
    }

    public static void resetAll() {
        BUILDER.reset();
        ENVIRONMENT.reset();
        CONSTRUCTOR.reset();
        APPLICATION_CONTEXT.reset();
    }

    private void reset() {
        this.customizer = Customizer.useDefaults();
    }
}
