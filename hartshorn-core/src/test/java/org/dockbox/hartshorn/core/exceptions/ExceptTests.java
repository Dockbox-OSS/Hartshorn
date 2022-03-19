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

package org.dockbox.hartshorn.core.exceptions;

import org.dockbox.hartshorn.application.environment.DelegatingApplicationManager;
import org.dockbox.hartshorn.application.ExceptionHandler;
import org.dockbox.hartshorn.application.LoggingExceptionHandler;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

@HartshornTest
public class ExceptTests {

    @Inject
    private ApplicationContext applicationContext;

    @Test
    public void testExceptKeepsPreferences() {
        final TestExceptionHandle handle = new TestExceptionHandle();
        ((DelegatingApplicationManager) this.applicationContext.environment().manager()).exceptionHandler(handle);
        this.applicationContext.environment().manager().stacktraces(true);

        final Throwable throwable = new Exception("Test");
        this.applicationContext.handle("Test", throwable);

        Assertions.assertTrue(handle.stacktrace());
        Assertions.assertEquals("Test", handle.message());
        Assertions.assertSame(throwable, handle.exception());
    }

    @Test
    public void testExceptUsesExceptionMessageIfNoneProvided() {
        final TestExceptionHandle handle = new TestExceptionHandle();
        ((DelegatingApplicationManager) this.applicationContext.environment().manager()).exceptionHandler(handle);

        final Exception throwable = new Exception("Something broke!");
        this.applicationContext.handle(throwable);

        Assertions.assertSame(throwable, handle.exception());
        Assertions.assertEquals("Something broke!", handle.message());
    }

    @Test
    public void testExceptUsesFirstExceptionMessageIfNoneProvided() {
        final TestExceptionHandle handle = new TestExceptionHandle();
        ((DelegatingApplicationManager) this.applicationContext.environment().manager()).exceptionHandler(handle);

        final Exception cause = new Exception("I caused it!");
        final Exception throwable = new Exception("Something broke!", cause);
        this.applicationContext.handle(throwable);

        Assertions.assertSame(throwable, handle.exception());
        Assertions.assertEquals("Something broke!", handle.message());
    }

    @Test
    public void testGetFirstUsesParentFirst() {
        final ExceptionHandler handle = new TestExceptionHandle();
        ((DelegatingApplicationManager) this.applicationContext.environment().manager()).exceptionHandler(handle);

        final Exception cause = new Exception("I caused it!");
        final Exception throwable = new Exception("Something broke!", cause);

        final String message = LoggingExceptionHandler.firstMessage(throwable);

        Assertions.assertEquals("Something broke!", message);
    }

    @Test
    public void testGetFirstUsesCauseIfParentMessageAbsent() {
        final ExceptionHandler handle = new TestExceptionHandle();
        ((DelegatingApplicationManager) this.applicationContext.environment().manager()).exceptionHandler(handle);

        final Exception cause = new Exception("I caused it!");
        final Exception throwable = new Exception(null, cause);

        final String message = LoggingExceptionHandler.firstMessage(throwable);

        Assertions.assertEquals("I caused it!", message);
    }
}
