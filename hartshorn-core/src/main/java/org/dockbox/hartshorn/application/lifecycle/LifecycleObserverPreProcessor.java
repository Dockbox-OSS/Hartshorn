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

package org.dockbox.hartshorn.application.lifecycle;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.processing.ComponentPreProcessor;
import org.dockbox.hartshorn.component.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.component.processing.ProcessingOrder;

public class LifecycleObserverPreProcessor extends ComponentPreProcessor {

    @Override
    public <T> void process(final ApplicationContext context, final ComponentProcessingContext<T> processingContext) {
        if (processingContext.type().isChildOf(Observer.class)) {
            context.environment().register((Class<? extends Observer>) processingContext.type().type());
        }
    }

    @Override
    public Integer order() {
        return (Integer.MIN_VALUE / 2) - (ProcessingOrder.LAST * 4);
    }
}
