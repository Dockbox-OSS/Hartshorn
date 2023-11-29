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
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ContextTests {

    @Test
    void testUnnamedContextFirst() {
        Context context = new TestContext();
        Context child = new TestContext();

        context.add(child);

        ContextIdentity<TestContext> key = new SimpleContextIdentity<>(TestContext.class);
        Option<TestContext> first = context.first(key);
        Assertions.assertTrue(first.present());
        Assertions.assertSame(child, first.get());
    }

    @Test
    void testUnnamedContextAll() {
        Context context = new TestContext();
        Context child = new TestContext();

        context.add(child);

        ContextIdentity<TestContext> key = new SimpleContextIdentity<>(TestContext.class);
        List<TestContext> all = context.all(key);
        Assertions.assertNotNull(all);
        Assertions.assertEquals(1, all.size());
    }

    @Test
    void testNamedContextFirstByName() {
        Context context = new TestContext();
        NamedTestContext named = new NamedTestContext();

        context.add(named);

        ContextIdentity<Context> key = new SimpleContextIdentity<>(Context.class, NamedTestContext.NAME);
        Option<Context> first = context.first(key);
        Assertions.assertTrue(first.present());
        Assertions.assertSame(named, first.get());
    }

    @Test
    void testNamedContextFirstByNameAndType() {
        Context context = new TestContext();
        NamedTestContext named = new NamedTestContext();

        context.add(named);

        ContextIdentity<NamedTestContext> key = new SimpleContextIdentity<>(NamedTestContext.class, NamedTestContext.NAME);
        Option<NamedTestContext> first = context.first(key);
        Assertions.assertTrue(first.present());
        Assertions.assertSame(named, first.get());
    }

    @Test
    void testManuallyNamedContextFirstByName() {
        Context context = new TestContext();
        Context child = new TestContext();

        context.add(NamedTestContext.NAME, child);

        ContextIdentity<Context> key = new SimpleContextIdentity<>(Context.class, NamedTestContext.NAME);
        Option<Context> first = context.first(key);
        Assertions.assertTrue(first.present());
        Assertions.assertSame(child, first.get());
    }

    @Test
    void testNamedContextAllByName() {
        Context context = new TestContext();
        NamedTestContext named = new NamedTestContext();

        context.add(named);

        ContextIdentity<Context> key = new SimpleContextIdentity<>(Context.class, NamedTestContext.NAME);
        List<Context> all = context.all(key);
        Assertions.assertNotNull(all);
        Assertions.assertEquals(1, all.size());
    }

    @Test
    void testNamedContextAllByNameAndType() {
        Context context = new TestContext();
        NamedTestContext named = new NamedTestContext();

        context.add(named);

        ContextIdentity<NamedTestContext> key = new SimpleContextIdentity<>(NamedTestContext.class, NamedTestContext.NAME);
        List<NamedTestContext> all = context.all(key);
        Assertions.assertNotNull(all);
        Assertions.assertEquals(1, all.size());
    }

    @Test
    void testManuallyNamedContextAllByName() {
        Context context = new TestContext();
        Context child = new TestContext();

        context.add(NamedTestContext.NAME, child);

        ContextIdentity<Context> key = new SimpleContextIdentity<>(Context.class, NamedTestContext.NAME);
        List<Context> all = context.all(key);
        Assertions.assertNotNull(all);
        Assertions.assertEquals(1, all.size());
    }

    public static class TestContext extends DefaultContext { }

    public static class NamedTestContext extends DefaultNamedContext {

        static String NAME = "JUnitContext";

        NamedTestContext() {
            super(NAME);
        }
    }
}
