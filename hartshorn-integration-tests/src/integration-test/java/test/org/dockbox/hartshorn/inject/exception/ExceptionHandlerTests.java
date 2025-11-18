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

package test.org.dockbox.hartshorn.inject.exception;

import org.dockbox.hartshorn.inject.LoggingExceptionHandler;
import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.launchpad.environment.ConfigurableApplicationEnvironment;
import org.dockbox.hartshorn.test.TestApplicationCustomizer;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@HartshornIntegrationTest(
        includeBasePackages = false,
        customizers = ExceptionHandlerTests.ExceptionHandlerTestsCustomizer.class
)
class ExceptionHandlerTests {

    public static class ExceptionHandlerTestsCustomizer implements TestApplicationCustomizer {
        @Override
        public void customizeEnvironment(ConfigurableApplicationEnvironment.Configurer configurer) {
            ExceptionHandlerTests.HANDLE = new CachingExceptionHandler();
            configurer.exceptionHandler(ExceptionHandlerTests.HANDLE);
        }
    }

    @Inject
    private ApplicationContext applicationContext;
    private static CachingExceptionHandler HANDLE;

    @Test
    void exceptKeepsPreferences() {
        this.applicationContext.environment().printStackTraces(true);

        Throwable throwable = new Exception("Test");
        this.applicationContext.handle("Test", throwable);

        assertThat(HANDLE.stacktrace()).isTrue();
        assertThat(HANDLE.message()).isEqualTo("Test");
        assertThat(HANDLE.exception()).isSameAs(throwable);
    }

    @Test
    void exceptUsesExceptionMessageIfNoneProvided() {
        Exception throwable = new Exception("Something broke!");
        this.applicationContext.handle(throwable);

        assertThat(HANDLE.exception()).isSameAs(throwable);
        assertThat(HANDLE.message()).isEqualTo("Something broke!");
    }

    @Test
    void exceptUsesFirstExceptionMessageIfNoneProvided() {
        Exception cause = new Exception("I caused it!");
        Exception throwable = new Exception("Something broke!", cause);
        this.applicationContext.handle(throwable);

        assertThat(HANDLE.exception()).isSameAs(throwable);
        assertThat(HANDLE.message()).isEqualTo("Something broke!");
    }

    @Test
    void getFirstUsesParentFirst() {
        Exception cause = new Exception("I caused it!");
        Exception throwable = new Exception("Something broke!", cause);

        String message = LoggingExceptionHandler.firstMessage(throwable);

        assertThat(message).isEqualTo("Something broke!");
    }

    @Test
    void getFirstUsesCauseIfParentMessageAbsent() {
        Exception cause = new Exception("I caused it!");
        Exception throwable = new Exception(null, cause);

        String message = LoggingExceptionHandler.firstMessage(throwable);

        assertThat(message).isEqualTo("I caused it!");
    }
}
