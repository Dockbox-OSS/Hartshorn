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

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.application.context.ProcessableApplicationContext;
import org.dockbox.hartshorn.component.ComponentProvider;
import org.dockbox.hartshorn.component.PostProcessingComponentProvider;
import org.dockbox.hartshorn.component.processing.ComponentPostProcessor;
import org.dockbox.hartshorn.component.processing.ComponentPreProcessor;
import org.dockbox.hartshorn.component.processing.ComponentProcessor;
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
            this.reportPreProcessors(collector, processableApplicationContext);
        }
        ComponentProvider componentProvider = this.applicationContext.get(ComponentProvider.class);
        if (componentProvider instanceof PostProcessingComponentProvider provider) {
            this.reportPostProcessors(collector, provider);
        }
    }

    private void reportPreProcessors(DiagnosticsPropertyCollector collector,
                                     ProcessableApplicationContext processableApplicationContext) {
        MultiMap<Integer, ? extends ComponentProcessor> processors = processableApplicationContext.processors();
        collector.property("pre").write(new ComponentProcessorsReportable(processors));
    }

    private void reportPostProcessors(DiagnosticsPropertyCollector collector,
                                      PostProcessingComponentProvider standardComponentProvider) {
        MultiMap<Integer, ? extends ComponentProcessor> processors = standardComponentProvider.postProcessors();
        collector.property("post").write(new ComponentProcessorsReportable(processors));
    }

    @Override
    public String category() {
        return COMPONENT_PROCESSORS_CATEGORY;
    }
}
