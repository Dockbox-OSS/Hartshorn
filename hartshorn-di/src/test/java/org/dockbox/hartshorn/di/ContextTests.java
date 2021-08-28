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

package org.dockbox.hartshorn.di;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.di.annotations.context.AutoCreating;
import org.dockbox.hartshorn.di.context.Context;
import org.dockbox.hartshorn.di.context.DefaultContext;
import org.dockbox.hartshorn.di.context.DefaultNamedContext;
import org.dockbox.hartshorn.test.ApplicationAwareTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ContextTests extends ApplicationAwareTest {

    @Test
    void testUnnamedContextFirst() {
        final Context context = new TestContext();
        final Context child = new TestContext();

        context.add(child);

        final Exceptional<TestContext> first = context.first(TestContext.class);
        Assertions.assertTrue(first.present());
        Assertions.assertSame(child, first.get());
    }

    @Test
    void testUnnamedContextAll() {
        final Context context = new TestContext();
        final Context child = new TestContext();

        context.add(child);

        final List<TestContext> all = context.all(TestContext.class);
        Assertions.assertNotNull(all);
        Assertions.assertEquals(1, all.size());
    }

    @Test
    void testNamedContextFirstByName() {
        final Context context = new TestContext();
        final NamedTestContext named = new NamedTestContext();

        context.add(named);

        final Exceptional<Context> first = context.first(NamedTestContext.NAME);
        Assertions.assertTrue(first.present());
        Assertions.assertSame(named, first.get());
    }

    @Test
    void testNamedContextFirstByNameAndType() {
        final Context context = new TestContext();
        final NamedTestContext named = new NamedTestContext();

        context.add(named);

        final Exceptional<NamedTestContext> first = context.first(NamedTestContext.NAME, NamedTestContext.class);
        Assertions.assertTrue(first.present());
        Assertions.assertSame(named, first.get());
    }

    @Test
    void testManuallyNamedContextFirstByName() {
        final Context context = new TestContext();
        final Context child = new TestContext();

        context.add(NamedTestContext.NAME, child);

        final Exceptional<Context> first = context.first(NamedTestContext.NAME);
        Assertions.assertTrue(first.present());
        Assertions.assertSame(child, first.get());
    }

    @Test
    void testNamedContextAllByName() {
        final Context context = new TestContext();
        final NamedTestContext named = new NamedTestContext();

        context.add(named);

        final List<Context> all = context.all(NamedTestContext.NAME);
        Assertions.assertNotNull(all);
        Assertions.assertEquals(1, all.size());
    }

    @Test
    void testNamedContextAllByNameAndType() {
        final Context context = new TestContext();
        final NamedTestContext named = new NamedTestContext();

        context.add(named);

        final List<NamedTestContext> all = context.all(NamedTestContext.NAME, NamedTestContext.class);
        Assertions.assertNotNull(all);
        Assertions.assertEquals(1, all.size());
    }

    @Test
    void testManuallyNamedContextAllByName() {
        final Context context = new TestContext();
        final Context child = new TestContext();

        context.add(NamedTestContext.NAME, child);

        final List<Context> all = context.all(NamedTestContext.NAME);
        Assertions.assertNotNull(all);
        Assertions.assertEquals(1, all.size());
    }

    @Test
    void testAutoCreatingContext() {
        final Context context = new TestContext();
        final Exceptional<AutoCreatingContext> first = context.first(AutoCreatingContext.class);
        Assertions.assertTrue(first.present());
    }

    static class TestContext extends DefaultContext {
    }

    @AutoCreating
    static class AutoCreatingContext extends DefaultContext {
    }

    static class NamedTestContext extends DefaultNamedContext {

        public static final String NAME = "JUnitContext";

        public NamedTestContext() {
            super(NAME);
        }
    }
}
