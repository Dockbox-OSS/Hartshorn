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

import org.dockbox.hartshorn.launchpad.HartshornApplication;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.launchpad.environment.ApplicationEnvironment;
import org.dockbox.hartshorn.launchpad.lifecycle.LifecycleObservable;
import org.dockbox.hartshorn.launchpad.lifecycle.ObservableApplicationEnvironment;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class LifecycleObserverTests {

    @Test
    void serviceLifecycleObserverIsPresentAndObserving() {
        ApplicationContext applicationContext = HartshornApplication.create();
        TestLifecycleObserver observer = applicationContext.get(TestLifecycleObserver.class);
        assertThat(observer.started()).isTrue();

        assertThatCode(applicationContext::close).doesNotThrowAnyException();
        assertThat(observer.stopped()).isTrue();
    }

    @Test
    void nonRegisteredObserverIsNotPresentOnStart() {
        ApplicationContext applicationContext = HartshornApplication.create();
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
        ApplicationContext applicationContext = HartshornApplication.create();
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
}
