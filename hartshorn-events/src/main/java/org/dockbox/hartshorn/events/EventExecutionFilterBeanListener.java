/*
 * Copyright 2019-2022 the original author or authors.
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

package org.dockbox.hartshorn.events;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.beans.BeanContext;
import org.dockbox.hartshorn.beans.BeanObserver;
import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.component.condition.RequiresActivator;
import org.dockbox.hartshorn.events.annotations.UseEvents;
import org.dockbox.hartshorn.events.handle.EventExecutionFilter;
import org.dockbox.hartshorn.util.reflect.TypeContext;


@Service
@RequiresActivator(UseEvents.class)
public class EventExecutionFilterBeanListener implements BeanObserver {

    @Override
    public void onBeansCollected(final ApplicationContext applicationContext, final BeanContext beanContext) {
        beanContext.provider().all(EventExecutionFilter.class).forEach(filter -> {
            applicationContext.log().debug("Adding filter " + TypeContext.of(filter).name() + " to event context");
            applicationContext.first(EventExecutionFilterContext.class).get().add(filter);
        });
    }
}
