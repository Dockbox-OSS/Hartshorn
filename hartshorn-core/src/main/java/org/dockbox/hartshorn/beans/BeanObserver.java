package org.dockbox.hartshorn.beans;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.application.lifecycle.Observer;

public interface BeanObserver extends Observer {

    /**
     * Called when the application is done collecting static beans. This is called directly after the
     * {@link BeanContext} has been created and configured.
     *
     * @param applicationContext The application context
     * @param beanContext The bean context
     */
    void onBeansCollected(ApplicationContext applicationContext, BeanContext beanContext);

}
