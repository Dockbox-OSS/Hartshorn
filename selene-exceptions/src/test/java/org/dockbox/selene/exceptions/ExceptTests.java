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

package org.dockbox.selene.exceptions;

import org.dockbox.selene.api.exceptions.Except;
import org.dockbox.selene.api.exceptions.ExceptionHandle;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ExceptTests {

    @Test
    public void testExceptKeepsPreferences() {
        Except.useStackTraces(true);

        TestExceptionHandle handle = new TestExceptionHandle();
        Except.with(handle);

        Throwable throwable = new Exception("Test");
        Except.handle("Test", throwable);

        Assertions.assertTrue(handle.isStacktrace());
        Assertions.assertEquals("Test", handle.getMessage());
        Assertions.assertSame(throwable, handle.getException());
    }

    @Test
    public void testExceptHandleCatchesRegularExceptions() {
        TestExceptionHandle handle = new TestExceptionHandle();
        Except.with(handle);

        Exception throwable = new Exception();
        Except.handle(() -> {
            throw throwable;
        });

        Assertions.assertSame(throwable, handle.getException());
    }

    @Test
    public void testExceptHandleCatchesRuntimeExceptions() {
        TestExceptionHandle handle = new TestExceptionHandle();
        Except.with(handle);

        RuntimeException throwable = new RuntimeException();
        Except.handle(() -> {
            throw throwable;
        });

        Assertions.assertSame(throwable, handle.getException());
    }

    @Test
    public void testExceptUsesExceptionMessageIfNoneProvided() {
        TestExceptionHandle handle = new TestExceptionHandle();
        Except.with(handle);

        Exception throwable = new Exception("Something broke!");
        Except.handle(throwable);

        Assertions.assertSame(throwable, handle.getException());
        Assertions.assertEquals("Something broke!", handle.getMessage());
    }

    @Test
    public void testExceptUsesFirstExceptionMessageIfNoneProvided() {
        TestExceptionHandle handle = new TestExceptionHandle();
        Except.with(handle);

        Exception cause = new Exception("I caused it!");
        Exception throwable = new Exception("Something broke!", cause);
        Except.handle(throwable);

        Assertions.assertSame(throwable, handle.getException());
        Assertions.assertEquals("Something broke!", handle.getMessage());
    }

    @Test
    public void testGetFirstUsesParentFirst() {
        ExceptionHandle handle = new TestExceptionHandle();
        Except.with(handle);

        Exception cause = new Exception("I caused it!");
        Exception throwable = new Exception("Something broke!", cause);

        String message = Except.firstMessage(throwable);

        Assertions.assertEquals("Something broke!", message);
    }

    @Test
    public void testGetFirstUsesCauseIfParentMessageAbsent() {
        ExceptionHandle handle = new TestExceptionHandle();
        Except.with(handle);

        Exception cause = new Exception("I caused it!");
        Exception throwable = new Exception(null, cause);

        String message = Except.firstMessage(throwable);

        Assertions.assertEquals("I caused it!", message);
    }
}
