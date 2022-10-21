package org.dockbox.hartshorn.core.beans;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.beans.BeanContext;
import org.dockbox.hartshorn.beans.BeanProvider;
import org.dockbox.hartshorn.inject.Context;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.testsuite.InjectTest;
import org.dockbox.hartshorn.util.Result;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Assertions;

import java.util.List;

@HartshornTest
public class BeanTests {

    @InjectTest
    void testApplicationHasBeanContext(final ApplicationContext applicationContext) {
        final Result<BeanContext> beanContext = applicationContext.first(BeanContext.class);
        Assertions.assertTrue(beanContext.present());

        final BeanProvider provider = beanContext.get().provider();
        Assertions.assertNotNull(provider);
    }

    @InjectTest
    void testBeansAreCollected(@Context final BeanContext beanContext) {
        final BeanProvider beanProvider = beanContext.provider();
        final List<BeanObject> beanObjects = beanProvider.all(BeanObject.class);
        Assertions.assertEquals(3, beanObjects.size());

        final BeanObject user = beanProvider.first(BeanObject.class, "user");
        Assertions.assertNotNull(user);

        final BeanObject admin = beanProvider.first(BeanObject.class, "admin");
        Assertions.assertNotNull(admin);

        final BeanObject guest = beanProvider.first(BeanObject.class, "guest");
        Assertions.assertNotNull(guest);
    }

    @InjectTest
    void testBeansAreObserved(final TestBeanObserver observer) {
        final List<BeanObject> beans = observer.beans();
        Assertions.assertEquals(3, beans.size());

        final BeanObject user = findBeanInList(beans, "user");
        Assertions.assertNotNull(user);

        final BeanObject admin = findBeanInList(beans, "admin");
        Assertions.assertNotNull(admin);

        final BeanObject guest = findBeanInList(beans, "guest");
        Assertions.assertNotNull(guest);
    }

    @Nullable
    private static BeanObject findBeanInList(final List<BeanObject> beans, final String user) {
        return beans.stream()
                .filter(bean -> user.equals(bean.name()))
                .findFirst()
                .orElse(null);
    }
}
