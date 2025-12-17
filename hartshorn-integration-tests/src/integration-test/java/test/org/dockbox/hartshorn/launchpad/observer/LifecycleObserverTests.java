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

package test.org.dockbox.hartshorn.launchpad.observer;

import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.annotations.CompositeMember;
import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.inject.annotations.configuration.Configuration;
import org.dockbox.hartshorn.inject.annotations.configuration.Singleton;
import org.dockbox.hartshorn.inject.collection.ComponentCollection;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.launchpad.environment.ApplicationEnvironment;
import org.dockbox.hartshorn.launchpad.lifecycle.LifecycleObservable;
import org.dockbox.hartshorn.launchpad.lifecycle.LifecycleObserver;
import org.dockbox.hartshorn.launchpad.lifecycle.ObservableApplicationEnvironment;
import org.dockbox.hartshorn.test.annotations.TestComponents;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.dockbox.hartshorn.util.stream.CollectorUtilities;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@HartshornIntegrationTest(includeBasePackages = false)
@TestComponents(LifecycleObserverTests.ObserverConfiguration.class)
public class LifecycleObserverTests {

    @Configuration
    public static class ObserverConfiguration {

        @Singleton
        @CompositeMember
        public LifecycleObserver observer() {
            return new TestLifecycleObserver();
        }
    }

    @Inject
    private ApplicationContext applicationContext;

    @Test
    void testServiceLifecycleObserverIsPresentAndObserving() {
        TestLifecycleObserver observer = getObserver(TestLifecycleObserver.class);

        Assertions.assertTrue(observer.started());

        Assertions.assertDoesNotThrow(applicationContext::close);
        Assertions.assertTrue(observer.stopped());
    }

    @Test
    void testNonRegisteredObserverIsNotPresentOnStart() {
        NonRegisteredObserver observer = applicationContext.get(NonRegisteredObserver.class);
        Assertions.assertFalse(observer.started());
        Assertions.assertFalse(observer.stopped());

        ApplicationEnvironment environment = applicationContext.environment();
        Assertions.assertTrue(environment instanceof ObservableApplicationEnvironment);

        ((LifecycleObservable) environment).register(observer);
        Assertions.assertDoesNotThrow(applicationContext::close);

        // Do not late-fire events
        Assertions.assertFalse(observer.started());
        Assertions.assertTrue(observer.stopped());
    }

    @Test
    void testRegistrationFromClassIsValid() {
        // Static as observer instance is lazily created by the observable, so we cannot
        // access it directly.
        Assertions.assertFalse(StaticNonRegisteredObserver.started());
        Assertions.assertFalse(StaticNonRegisteredObserver.stopped());

        ApplicationEnvironment environment = applicationContext.environment();
        Assertions.assertTrue(environment instanceof ObservableApplicationEnvironment);

        ((LifecycleObservable) environment).register(StaticNonRegisteredObserver.class);
        Assertions.assertDoesNotThrow(applicationContext::close);

        // Do not late-fire events
        Assertions.assertFalse(StaticNonRegisteredObserver.started());
        Assertions.assertTrue(StaticNonRegisteredObserver.stopped());
    }

    private <T extends LifecycleObserver> T getObserver(Class<T> type) {
        ComponentCollection<LifecycleObserver> observers = applicationContext.get(
                ComponentKey.collect(LifecycleObserver.class)
        );
        return observers.stream()
                .filter(type::isInstance)
                .collect(CollectorUtilities.toOption())
                .cast(type)
                .orElseGet(() -> Assertions.fail("Expected %s to be present in observers".formatted(type.getSimpleName())));
    }
}
