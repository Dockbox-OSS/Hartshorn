package org.dockbox.hartshorn.core.beans;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.beans.BeanContext;
import org.dockbox.hartshorn.beans.BeanObserver;
import org.dockbox.hartshorn.component.Service;

import java.util.List;

@Service
public class TestBeanObserver implements BeanObserver {

    private List<BeanObject> beans;

    @Override
    public void onBeansCollected(final ApplicationContext applicationContext, final BeanContext beanContext) {
        this.beans = beanContext.provider().all(BeanObject.class);
    }

    public List<BeanObject> beans() {
        return this.beans;
    }
}
