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

package org.dockbox.hartshorn.reporting.component;

import org.dockbox.hartshorn.inject.processing.ComponentPostProcessor;
import org.dockbox.hartshorn.inject.processing.ComponentPreProcessor;
import org.dockbox.hartshorn.inject.processing.ComponentProcessor;
import org.dockbox.hartshorn.inject.processing.ComponentProcessorRegistry;
import org.dockbox.hartshorn.inject.provider.PostProcessingComponentProvider;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.launchpad.ProcessableApplicationContext;
import org.dockbox.hartshorn.reporting.CategorizedDiagnosticsReporter;
import org.dockbox.hartshorn.reporting.DiagnosticsPropertyCollector;
import org.dockbox.hartshorn.util.collections.MultiMap;

/**
 * A diagnostics reporter that reports all {@link ComponentPreProcessor pre-processors} and {@link
 * ComponentPostProcessor post-processors} that are registered in the application context.
 *
 * <p>Pre-processors are only reported if the application context is a {@link ProcessableApplicationContext}, and
 * post-processors are only reported if the application context has a {@link PostProcessingComponentProvider}.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class ComponentProcessorDiagnosticsReporter implements CategorizedDiagnosticsReporter {

    public static final String COMPONENT_PROCESSORS_CATEGORY = "componentProcessors";

    private final ApplicationContext applicationContext;

    public ComponentProcessorDiagnosticsReporter(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void report(DiagnosticsPropertyCollector collector) {
        if (this.applicationContext instanceof ProcessableApplicationContext processableApplicationContext) {
            ComponentProcessorRegistry registry = processableApplicationContext.defaultProvider().processorRegistry();
            this.reportPreProcessors(collector, registry);
            this.reportPostProcessors(collector, registry);
        }
    }

    private void reportPreProcessors(DiagnosticsPropertyCollector collector,
            ComponentProcessorRegistry registry) {
        MultiMap<Integer, ? extends ComponentProcessor> processors = registry.preProcessors();
        collector.property("pre").writeDelegate(new ComponentProcessorsReportable(processors));
    }

    private void reportPostProcessors(DiagnosticsPropertyCollector collector,
            ComponentProcessorRegistry registry) {
        MultiMap<Integer, ? extends ComponentProcessor> processors = registry.postProcessors();
        collector.property("post").writeDelegate(new ComponentProcessorsReportable(processors));
    }

    @Override
    public String category() {
        return COMPONENT_PROCESSORS_CATEGORY;
    }
}
