/*
 * Copyright 2019-2022 the original author or authors.
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

package test.org.dockbox.hartshorn.exceptions;

import org.dockbox.hartshorn.application.ApplicationBuilder;
import org.dockbox.hartshorn.application.LoggingExceptionHandler;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.testsuite.ModifyApplication;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;

@HartshornTest(includeBasePackages = false)
public class ExceptTests {

    @Inject
    private ApplicationContext applicationContext;
    private static TestExceptionHandle HANDLE;

    @ModifyApplication
    public static ApplicationBuilder<?, ?> factory(final ApplicationBuilder<?, ?> factory) {
        return factory.exceptionHandler(ctx -> (HANDLE  = new TestExceptionHandle()));
    }

    @Test
    public void testExceptKeepsPreferences() {
        this.applicationContext.environment().stacktraces(true);

        final Throwable throwable = new Exception("Test");
        this.applicationContext.handle("Test", throwable);

        Assertions.assertTrue(HANDLE.stacktrace());
        Assertions.assertEquals("Test", HANDLE.message());
        Assertions.assertSame(throwable, HANDLE.exception());
    }

    @Test
    public void testExceptUsesExceptionMessageIfNoneProvided() {
        final Exception throwable = new Exception("Something broke!");
        this.applicationContext.handle(throwable);

        Assertions.assertSame(throwable, HANDLE.exception());
        Assertions.assertEquals("Something broke!", HANDLE.message());
    }

    @Test
    public void testExceptUsesFirstExceptionMessageIfNoneProvided() {
        final Exception cause = new Exception("I caused it!");
        final Exception throwable = new Exception("Something broke!", cause);
        this.applicationContext.handle(throwable);

        Assertions.assertSame(throwable, HANDLE.exception());
        Assertions.assertEquals("Something broke!", HANDLE.message());
    }

    @Test
    public void testGetFirstUsesParentFirst() {
        final Exception cause = new Exception("I caused it!");
        final Exception throwable = new Exception("Something broke!", cause);

        final String message = LoggingExceptionHandler.firstMessage(throwable);

        Assertions.assertEquals("Something broke!", message);
    }

    @Test
    public void testGetFirstUsesCauseIfParentMessageAbsent() {
        final Exception cause = new Exception("I caused it!");
        final Exception throwable = new Exception(null, cause);

        final String message = LoggingExceptionHandler.firstMessage(throwable);

        Assertions.assertEquals("I caused it!", message);
    }
}
