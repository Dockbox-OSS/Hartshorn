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

package test.org.dockbox.hartshorn.launchpad;

import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.launchpad.HartshornApplication;
import org.dockbox.hartshorn.launchpad.launch.StandardApplicationContextFactory;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.launchpad.environment.ConfigurableApplicationEnvironment;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@Execution(ExecutionMode.CONCURRENT)
class ApplicationBatchingTest {

    /**
     * Test that multiple applications can be created and be active at the same time without interfering with each other.
     */
    @Disabled("Only for manual testing")
    @RepeatedTest(100)
    void applicationContextBatching() {
        ApplicationContext applicationContext = assertThatCode(() ->
                HartshornApplication.create(ApplicationBatchingTest.class, builder ->
                        builder.applicationContextFactory(StandardApplicationContextFactory.create(constructor -> {
                            constructor.includeBasePackages(false);
                            constructor.standaloneComponents(components -> components.add(SimpleComponent.class));
                            constructor.environment(
                                    ConfigurableApplicationEnvironment.create(ConfigurableApplicationEnvironment.Configurer::enableBatchMode)
                            );
                        })
                        ))).doesNotThrowAnyException();

        assertThat(applicationContext).isNotNull();

        SimpleComponent component = applicationContext.get(SimpleComponent.class);
        assertThat(component).isNotNull();
        assertThat(component.applicationContext()).isSameAs(applicationContext);
    }

    public record SimpleComponent(ApplicationContext applicationContext) {

        @Inject
        public SimpleComponent {}
    }
}
