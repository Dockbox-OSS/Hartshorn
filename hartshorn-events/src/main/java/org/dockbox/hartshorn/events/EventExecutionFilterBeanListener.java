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
