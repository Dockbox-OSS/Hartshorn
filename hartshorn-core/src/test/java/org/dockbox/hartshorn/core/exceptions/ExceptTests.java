/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.core.exceptions;

import org.dockbox.hartshorn.core.boot.ExceptionHandler;
import org.dockbox.hartshorn.core.boot.HartshornApplicationManager;
import org.dockbox.hartshorn.core.boot.HartshornExceptionHandler;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import lombok.Getter;

@HartshornTest
public class ExceptTests {

    @Inject
    @Getter
    private ApplicationContext applicationContext;

    @Test
    public void testExceptKeepsPreferences() {
        final TestExceptionHandle handle = new TestExceptionHandle();
        ((HartshornApplicationManager) this.applicationContext().environment().manager()).exceptionHandler(handle);
        this.applicationContext().environment().manager().stacktraces(true);

        final Throwable throwable = new Exception("Test");
        this.applicationContext().handle("Test", throwable);

        Assertions.assertTrue(handle.stacktrace());
        Assertions.assertEquals("Test", handle.message());
        Assertions.assertSame(throwable, handle.exception());
    }

    @Test
    public void testExceptUsesExceptionMessageIfNoneProvided() {
        final TestExceptionHandle handle = new TestExceptionHandle();
        ((HartshornApplicationManager) this.applicationContext().environment().manager()).exceptionHandler(handle);

        final Exception throwable = new Exception("Something broke!");
        this.applicationContext().handle(throwable);

        Assertions.assertSame(throwable, handle.exception());
        Assertions.assertEquals("Something broke!", handle.message());
    }

    @Test
    public void testExceptUsesFirstExceptionMessageIfNoneProvided() {
        final TestExceptionHandle handle = new TestExceptionHandle();
        ((HartshornApplicationManager) this.applicationContext().environment().manager()).exceptionHandler(handle);

        final Exception cause = new Exception("I caused it!");
        final Exception throwable = new Exception("Something broke!", cause);
        this.applicationContext().handle(throwable);

        Assertions.assertSame(throwable, handle.exception());
        Assertions.assertEquals("Something broke!", handle.message());
    }

    @Test
    public void testGetFirstUsesParentFirst() {
        final ExceptionHandler handle = new TestExceptionHandle();
        ((HartshornApplicationManager) this.applicationContext().environment().manager()).exceptionHandler(handle);

        final Exception cause = new Exception("I caused it!");
        final Exception throwable = new Exception("Something broke!", cause);

        final String message = HartshornExceptionHandler.firstMessage(throwable);

        Assertions.assertEquals("Something broke!", message);
    }

    @Test
    public void testGetFirstUsesCauseIfParentMessageAbsent() {
        final ExceptionHandler handle = new TestExceptionHandle();
        ((HartshornApplicationManager) this.applicationContext().environment().manager()).exceptionHandler(handle);

        final Exception cause = new Exception("I caused it!");
        final Exception throwable = new Exception(null, cause);

        final String message = HartshornExceptionHandler.firstMessage(throwable);

        Assertions.assertEquals("I caused it!", message);
    }
}
