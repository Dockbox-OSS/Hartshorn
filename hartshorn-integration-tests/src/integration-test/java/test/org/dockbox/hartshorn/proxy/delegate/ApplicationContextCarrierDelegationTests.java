/*
 * Copyright 2019-2026 the original author or authors.
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

package test.org.dockbox.hartshorn.proxy.delegate;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.launchpad.context.ApplicationContextCarrier;
import org.dockbox.hartshorn.proxy.Proxy;
import org.dockbox.hartshorn.test.annotations.TestComponents;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.dockbox.hartshorn.test.HartshornAssertions.assertThat;

@HartshornIntegrationTest(includeBasePackages = false)
class ApplicationContextCarrierDelegationTests {

    @Test
    @TestComponents(ContextCarrierComponent.class)
    void contextCarrierDelegation(@Inject ContextCarrierComponent component) throws Exception {
        assertThat(findTypeDelegate(component)).present();
        assertThat(findMethodDelegate(component)).absent();
        assertThat(component.applicationContext()).isNotNull();
    }

    @Test
    @TestComponents(OverrideContextCarrierComponentInterface.class)
    void defaultCarrierDelegation(@Inject OverrideContextCarrierComponentInterface component) throws Exception {
        assertThat(findTypeDelegate(component)).absent();
        assertThat(findMethodDelegate(component)).absent();
        // Default method, should return null (see OverrideContextCarrierComponentInterface)
        assertThat(component.applicationContext()).isNull();
    }

    @Test
    @TestComponents(ContextCarrierComponentInterface.class)
    void carrierDelegation(
        @Inject ContextCarrierComponentInterface component,
        @Inject ApplicationContext applicationContext
    ) throws Exception {
        assertThat(component).isInstanceOf(Proxy.class);
        Method method = ApplicationContextCarrier.class.getMethod("applicationContext");
        Option<?> methodDelegate = ((Proxy<?>) component).manager()
            .advisor()
            .resolver()
            .method(method)
            .delegate();
        assertThat(methodDelegate).present();
        assertThat(component.applicationContext()).isNotNull();
        assertThat(applicationContext).isSameAs(component.applicationContext());
    }

    private static Option<ApplicationContextCarrier> findTypeDelegate(Object object) {
        Proxy<?> proxy = assertThat(object)
                .asInstanceOf(InstanceOfAssertFactories.type(Proxy.class))
                .actual();
        return proxy.manager()
                .advisor()
                .resolver()
                .type(ApplicationContextCarrier.class)
                .delegate();
    }

    private static Option<?> findMethodDelegate(Object object) throws NoSuchMethodException {
        Proxy<?> proxy = assertThat(object)
                .asInstanceOf(InstanceOfAssertFactories.type(Proxy.class))
                .actual();
        Method method = ApplicationContextCarrier.class.getMethod("applicationContext");

        return proxy.manager()
            .advisor()
            .resolver()
            .method(method)
            .delegate();
    }
}
