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

package test.org.dockbox.hartshorn.inject.exception;

import org.dockbox.hartshorn.inject.LoggingExceptionHandler;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.test.TestCustomizer;
import org.dockbox.hartshorn.test.annotations.CustomizeTests;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.dockbox.hartshorn.inject.annotations.Inject;

@HartshornIntegrationTest(includeBasePackages = false)
public class ExceptionHandlerTests {

    @Inject
    private ApplicationContext applicationContext;
    private static CachingExceptionHandler HANDLE;

    @CustomizeTests
    public static void customize() {
        TestCustomizer.ENVIRONMENT.compose(environment -> {
            ExceptionHandlerTests.HANDLE = new CachingExceptionHandler();
            environment.exceptionHandler(ExceptionHandlerTests.HANDLE);
        });
    }

    @Test
    public void testExceptKeepsPreferences() {
        this.applicationContext.environment().printStackTraces(true);

        Throwable throwable = new Exception("Test");
        this.applicationContext.handle("Test", throwable);

        Assertions.assertTrue(HANDLE.stacktrace());
        Assertions.assertEquals("Test", HANDLE.message());
        Assertions.assertSame(throwable, HANDLE.exception());
    }

    @Test
    public void testExceptUsesExceptionMessageIfNoneProvided() {
        Exception throwable = new Exception("Something broke!");
        this.applicationContext.handle(throwable);

        Assertions.assertSame(throwable, HANDLE.exception());
        Assertions.assertEquals("Something broke!", HANDLE.message());
    }

    @Test
    public void testExceptUsesFirstExceptionMessageIfNoneProvided() {
        Exception cause = new Exception("I caused it!");
        Exception throwable = new Exception("Something broke!", cause);
        this.applicationContext.handle(throwable);

        Assertions.assertSame(throwable, HANDLE.exception());
        Assertions.assertEquals("Something broke!", HANDLE.message());
    }

    @Test
    public void testGetFirstUsesParentFirst() {
        Exception cause = new Exception("I caused it!");
        Exception throwable = new Exception("Something broke!", cause);

        String message = LoggingExceptionHandler.firstMessage(throwable);

        Assertions.assertEquals("Something broke!", message);
    }

    @Test
    public void testGetFirstUsesCauseIfParentMessageAbsent() {
        Exception cause = new Exception("I caused it!");
        Exception throwable = new Exception(null, cause);

        String message = LoggingExceptionHandler.firstMessage(throwable);

        Assertions.assertEquals("I caused it!", message);
    }
}
