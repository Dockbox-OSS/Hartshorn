/*
 * Copyright 2019-2023 the original author or authors.
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

package test.org.dockbox.hartshorn.util;

import org.dockbox.hartshorn.context.Context;
import org.dockbox.hartshorn.context.ContextIdentity;
import org.dockbox.hartshorn.context.DefaultContext;
import org.dockbox.hartshorn.context.DefaultNamedContext;
import org.dockbox.hartshorn.context.SimpleContextIdentity;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

@HartshornTest(includeBasePackages = false)
public class ContextTests {

    @Test
    void testUnnamedContextFirst() {
        final Context context = new TestContext();
        final Context child = new TestContext();

        context.add(child);

        final ContextIdentity<TestContext> key = new SimpleContextIdentity<>(TestContext.class);
        final Option<TestContext> first = context.first(key);
        Assertions.assertTrue(first.present());
        Assertions.assertSame(child, first.get());
    }

    @Test
    void testUnnamedContextAll() {
        final Context context = new TestContext();
        final Context child = new TestContext();

        context.add(child);

        final ContextIdentity<TestContext> key = new SimpleContextIdentity<>(TestContext.class);
        final List<TestContext> all = context.all(key);
        Assertions.assertNotNull(all);
        Assertions.assertEquals(1, all.size());
    }

    @Test
    void testNamedContextFirstByName() {
        final Context context = new TestContext();
        final NamedTestContext named = new NamedTestContext();

        context.add(named);

        final ContextIdentity<Context> key = new SimpleContextIdentity<>(Context.class, NamedTestContext.NAME);
        final Option<Context> first = context.first(key);
        Assertions.assertTrue(first.present());
        Assertions.assertSame(named, first.get());
    }

    @Test
    void testNamedContextFirstByNameAndType() {
        final Context context = new TestContext();
        final NamedTestContext named = new NamedTestContext();

        context.add(named);

        final ContextIdentity<NamedTestContext> key = new SimpleContextIdentity<>(NamedTestContext.class, NamedTestContext.NAME);
        final Option<NamedTestContext> first = context.first(key);
        Assertions.assertTrue(first.present());
        Assertions.assertSame(named, first.get());
    }

    @Test
    void testManuallyNamedContextFirstByName() {
        final Context context = new TestContext();
        final Context child = new TestContext();

        context.add(NamedTestContext.NAME, child);

        final ContextIdentity<Context> key = new SimpleContextIdentity<>(Context.class, NamedTestContext.NAME);
        final Option<Context> first = context.first(key);
        Assertions.assertTrue(first.present());
        Assertions.assertSame(child, first.get());
    }

    @Test
    void testNamedContextAllByName() {
        final Context context = new TestContext();
        final NamedTestContext named = new NamedTestContext();

        context.add(named);

        final ContextIdentity<Context> key = new SimpleContextIdentity<>(Context.class, NamedTestContext.NAME);
        final List<Context> all = context.all(key);
        Assertions.assertNotNull(all);
        Assertions.assertEquals(1, all.size());
    }

    @Test
    void testNamedContextAllByNameAndType() {
        final Context context = new TestContext();
        final NamedTestContext named = new NamedTestContext();

        context.add(named);

        final ContextIdentity<NamedTestContext> key = new SimpleContextIdentity<>(NamedTestContext.class, NamedTestContext.NAME);
        final List<NamedTestContext> all = context.all(key);
        Assertions.assertNotNull(all);
        Assertions.assertEquals(1, all.size());
    }

    @Test
    void testManuallyNamedContextAllByName() {
        final Context context = new TestContext();
        final Context child = new TestContext();

        context.add(NamedTestContext.NAME, child);

        final ContextIdentity<Context> key = new SimpleContextIdentity<>(Context.class, NamedTestContext.NAME);
        final List<Context> all = context.all(key);
        Assertions.assertNotNull(all);
        Assertions.assertEquals(1, all.size());
    }

    public static class TestContext extends DefaultContext { }

    public static class NamedTestContext extends DefaultNamedContext {

        static final String NAME = "JUnitContext";

        NamedTestContext() {
            super(NAME);
        }
    }
}
