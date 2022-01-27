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

package org.dockbox.hartshorn.core;

import org.dockbox.hartshorn.core.annotations.context.AutoCreating;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.Context;
import org.dockbox.hartshorn.core.context.DefaultContext;
import org.dockbox.hartshorn.core.context.DefaultNamedContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import javax.inject.Inject;

import lombok.Getter;

@HartshornTest
public class ContextTests {

    @Inject
    @Getter
    private ApplicationContext applicationContext;

    @Test
    void testUnnamedContextFirst() {
        final Context context = new TestContext();
        final Context child = new TestContext();

        context.add(child);

        final Exceptional<TestContext> first = context.first(this.applicationContext(), TestContext.class);
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
        final Exceptional<AutoCreatingContext> first = context.first(this.applicationContext(), AutoCreatingContext.class);
        Assertions.assertTrue(first.present());
    }

    static class TestContext extends DefaultContext {
    }

    @AutoCreating
    static class AutoCreatingContext extends DefaultContext {
    }

    static class NamedTestContext extends DefaultNamedContext {

        static final String NAME = "JUnitContext";

        NamedTestContext() {
            super(NAME);
        }
    }
}
