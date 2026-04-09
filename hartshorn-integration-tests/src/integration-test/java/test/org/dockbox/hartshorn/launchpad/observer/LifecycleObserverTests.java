/*
 * Copyright 2019-2025 the original author or authors.
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
import org.dockbox.hartshorn.test.annotations.TestProperties;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.fail;

@HartshornIntegrationTest(includeBasePackages = false)
@TestComponents(LifecycleObserverTests.ObserverConfiguration.class)
@TestProperties("hartshorn.container.close.reentry-policy=IGNORE")
class LifecycleObserverTests {

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
    void serviceLifecycleObserverIsPresentAndObserving() {
        TestLifecycleObserver observer = getObserver(TestLifecycleObserver.class);

        assertThat(observer.started()).isTrue();

        assertThatCode(applicationContext::close).doesNotThrowAnyException();
        assertThat(observer.stopped()).isTrue();
    }

    @Test
    void nonRegisteredObserverIsNotPresentOnStart() {
        NonRegisteredObserver observer = applicationContext.get(NonRegisteredObserver.class);
        assertThat(observer.started()).isFalse();
        assertThat(observer.stopped()).isFalse();

        ApplicationEnvironment environment = applicationContext.environment();
        assertThat(environment).isInstanceOf(ObservableApplicationEnvironment.class);

        ((LifecycleObservable) environment).register(observer);
        assertThatCode(applicationContext::close).doesNotThrowAnyException();

        // Do not late-fire events
        assertThat(observer.started()).isFalse();
        assertThat(observer.stopped()).isTrue();
    }

    @Test
    void registrationFromClassIsValid() {
        // Static as observer instance is lazily created by the observable, so we cannot
        // access it directly.
        assertThat(StaticNonRegisteredObserver.started()).isFalse();
        assertThat(StaticNonRegisteredObserver.stopped()).isFalse();

        ApplicationEnvironment environment = applicationContext.environment();
        assertThat(environment).isInstanceOf(ObservableApplicationEnvironment.class);

        ((LifecycleObservable) environment).register(StaticNonRegisteredObserver.class);
        assertThatCode(applicationContext::close).doesNotThrowAnyException();

        // Do not late-fire events
        assertThat(StaticNonRegisteredObserver.started()).isFalse();
        assertThat(StaticNonRegisteredObserver.stopped()).isTrue();
    }

    private <T extends LifecycleObserver> T getObserver(Class<T> type) {
        ComponentCollection<LifecycleObserver> observers = applicationContext.get(
                ComponentKey.collect(LifecycleObserver.class)
        );
        return observers.stream()
                .filter(type::isInstance)
                .collect(Option.collector())
                .cast(type)
                .orElseGet(() -> fail("Expected %s to be present in observers".formatted(type.getSimpleName())));
    }
}
