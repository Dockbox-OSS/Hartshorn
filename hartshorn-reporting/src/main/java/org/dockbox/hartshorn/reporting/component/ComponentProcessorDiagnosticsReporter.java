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

package org.dockbox.hartshorn.reporting.component;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.application.context.ProcessableApplicationContext;
import org.dockbox.hartshorn.component.ComponentProvider;
import org.dockbox.hartshorn.component.PostProcessingComponentProvider;
import org.dockbox.hartshorn.component.processing.ComponentPostProcessor;
import org.dockbox.hartshorn.component.processing.ComponentPreProcessor;
import org.dockbox.hartshorn.component.processing.ProcessingOrder;
import org.dockbox.hartshorn.component.processing.ProcessingPhase;
import org.dockbox.hartshorn.reporting.CategorizedDiagnosticsReporter;
import org.dockbox.hartshorn.reporting.ConfigurableDiagnosticsReporter;
import org.dockbox.hartshorn.reporting.DiagnosticsPropertyCollector;
import org.dockbox.hartshorn.reporting.Reportable;
import org.dockbox.hartshorn.util.collections.MultiMap;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ComponentProcessorDiagnosticsReporter implements ConfigurableDiagnosticsReporter<ComponentProcessorReportingConfiguration>, CategorizedDiagnosticsReporter {

    public static final String COMPONENT_PROCESSORS_CATEGORY = "componentProcessors";

    private final ComponentProcessorReportingConfiguration configuration = new ComponentProcessorReportingConfiguration();
    private final ApplicationContext applicationContext;

    public ComponentProcessorDiagnosticsReporter(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void report(final DiagnosticsPropertyCollector collector) {
        if (this.applicationContext instanceof ProcessableApplicationContext processableApplicationContext) {
            this.reportPreProcessors(collector, processableApplicationContext);
        }
        final ComponentProvider componentProvider = this.applicationContext.get(ComponentProvider.class);
        if (componentProvider instanceof PostProcessingComponentProvider provider) {
            this.reportPostProcessors(collector, provider);
        }
    }

    private void reportPreProcessors(final DiagnosticsPropertyCollector collector,
                                     final ProcessableApplicationContext processableApplicationContext) {
        final MultiMap<Integer, ComponentPreProcessor> processors = processableApplicationContext.processors();
        final Reportable[] reporters = processors.allValues().stream()
                .map(processor -> (Reportable) processorCollector -> {
                    processorCollector.property("name").write(processor.getClass().getCanonicalName());
                    processorCollector.property("order").write(processor.order());
                }).toArray(Reportable[]::new);

        collector.property("pre").write(reporters);
    }

    private void reportPostProcessors(final DiagnosticsPropertyCollector collector,
                                      final PostProcessingComponentProvider standardComponentProvider) {
        final MultiMap<Integer, ComponentPostProcessor> processors = standardComponentProvider.postProcessors();
        collector.property("post").write(phaseCollector -> {
            final Map<ProcessingPhase, List<Integer>> processorsByPhase = processors.keySet().stream()
                    .collect(Collectors.groupingBy(order -> {
                        for (final ProcessingPhase processingPhase : ProcessingOrder.PHASES) {
                            if (processingPhase.test(order)) {
                                return processingPhase;
                            }
                        }
                        throw new IllegalStateException("No phase found for order " + order);
                    }));

            for (final ProcessingPhase phase : ProcessingOrder.PHASES) {
                final Reportable[] reporters = processorsByPhase.get(phase).stream()
                        .flatMap(order -> processors.get(order).stream())
                        .map(processor -> (Reportable) processorCollector -> {
                            processorCollector.property("name").write(processor.getClass().getCanonicalName());
                            processorCollector.property("order").write(processor.order());
                        }).toArray(Reportable[]::new);

                phaseCollector.property(phase.name().toLowerCase()).write(reporters);
            }
        });
    }

    @Override
    public ComponentProcessorReportingConfiguration configuration() {
        return this.configuration;
    }

    @Override
    public String category() {
        return COMPONENT_PROCESSORS_CATEGORY;
    }
}
