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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ExceptTests {

    @Test
    public void testExceptKeepsPreferences() {
        Except.useStackTraces(true);

        final TestExceptionHandle handle = new TestExceptionHandle();
        Except.with(handle);

        final Throwable throwable = new Exception("Test");
        Except.handle("Test", throwable);

        Assertions.assertTrue(handle.stacktrace());
        Assertions.assertEquals("Test", handle.message());
        Assertions.assertSame(throwable, handle.exception());
    }

    @Test
    public void testExceptUsesExceptionMessageIfNoneProvided() {
        final TestExceptionHandle handle = new TestExceptionHandle();
        Except.with(handle);

        final Exception throwable = new Exception("Something broke!");
        Except.handle(throwable);

        Assertions.assertSame(throwable, handle.exception());
        Assertions.assertEquals("Something broke!", handle.message());
    }

    @Test
    public void testExceptUsesFirstExceptionMessageIfNoneProvided() {
        final TestExceptionHandle handle = new TestExceptionHandle();
        Except.with(handle);

        final Exception cause = new Exception("I caused it!");
        final Exception throwable = new Exception("Something broke!", cause);
        Except.handle(throwable);

        Assertions.assertSame(throwable, handle.exception());
        Assertions.assertEquals("Something broke!", handle.message());
    }

    @Test
    public void testGetFirstUsesParentFirst() {
        final ExceptionHandle handle = new TestExceptionHandle();
        Except.with(handle);

        final Exception cause = new Exception("I caused it!");
        final Exception throwable = new Exception("Something broke!", cause);

        final String message = Except.firstMessage(throwable);

        Assertions.assertEquals("Something broke!", message);
    }

    @Test
    public void testGetFirstUsesCauseIfParentMessageAbsent() {
        final ExceptionHandle handle = new TestExceptionHandle();
        Except.with(handle);

        final Exception cause = new Exception("I caused it!");
        final Exception throwable = new Exception(null, cause);

        final String message = Except.firstMessage(throwable);

        Assertions.assertEquals("I caused it!", message);
    }
}
