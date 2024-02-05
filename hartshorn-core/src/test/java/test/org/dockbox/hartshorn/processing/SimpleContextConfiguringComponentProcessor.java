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

package test.org.dockbox.hartshorn.processing;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.component.processing.ContextConfiguringComponentProcessor;
import org.dockbox.hartshorn.component.processing.ProcessingPriority;

public class SimpleContextConfiguringComponentProcessor extends ContextConfiguringComponentProcessor<SimpleContext> {

    public SimpleContextConfiguringComponentProcessor() {
        super(SimpleContext.class);
    }

    @Override
    protected boolean supports(ComponentProcessingContext<?> processingContext) {
        return true;
    }

    @Override
    protected <T> void configure(
            ApplicationContext context,
            SimpleContext componentContext,
            ComponentProcessingContext<T> processingContext
    ) {
        componentContext.value("Foo");
    }

    @Override
    protected SimpleContext createContext(
            ApplicationContext context,
            ComponentProcessingContext<?> processingContext
    ) {
        return new SimpleContext();
    }

    @Override
    public int priority() {
        return ProcessingPriority.NORMAL_PRECEDENCE;
    }
}
