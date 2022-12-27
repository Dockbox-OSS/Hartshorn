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

package test.org.dockbox.hartshorn;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.context.ApplicationAwareContext;
import org.dockbox.hartshorn.context.Context;
import org.dockbox.hartshorn.context.ContextKey;
import org.dockbox.hartshorn.context.DefaultApplicationAwareContext;
import org.dockbox.hartshorn.context.DefaultContext;
import org.dockbox.hartshorn.context.DefaultNamedContext;
import org.dockbox.hartshorn.context.InstallIfAbsent;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import jakarta.inject.Inject;

@HartshornTest(includeBasePackages = false)
public class ContextTests {

    @Inject
    private ApplicationContext applicationContext;

    @Test
    void testUnnamedContextFirst() {
        final ApplicationAwareContext context = new TestContext(this.applicationContext);
        final Context child = new TestContext(this.applicationContext);

        context.add(child);

        final ContextKey<TestContext> key = ContextKey.of(TestContext.class);
        final Option<TestContext> first = context.first(key);
        Assertions.assertTrue(first.present());
        Assertions.assertSame(child, first.get());
    }

    @Test
    void testUnnamedContextAll() {
        final Context context = new TestContext(this.applicationContext);
        final Context child = new TestContext(this.applicationContext);

        context.add(child);

        final ContextKey<TestContext> key = ContextKey.of(TestContext.class);
        final List<TestContext> all = context.all(key);
        Assertions.assertNotNull(all);
        Assertions.assertEquals(1, all.size());
    }

    @Test
    void testNamedContextFirstByName() {
        final Context context = new TestContext(this.applicationContext);
        final NamedTestContext named = new NamedTestContext();

        context.add(named);

        final ContextKey<Context> key = ContextKey.builder(Context.class).name(NamedTestContext.NAME).build();
        final Option<Context> first = context.first(key);
        Assertions.assertTrue(first.present());
        Assertions.assertSame(named, first.get());
    }

    @Test
    void testNamedContextFirstByNameAndType() {
        final Context context = new TestContext(this.applicationContext);
        final NamedTestContext named = new NamedTestContext();

        context.add(named);

        final ContextKey<NamedTestContext> key = ContextKey.builder(NamedTestContext.class).name(NamedTestContext.NAME).build();
        final Option<NamedTestContext> first = context.first(key);
        Assertions.assertTrue(first.present());
        Assertions.assertSame(named, first.get());
    }

    @Test
    void testManuallyNamedContextFirstByName() {
        final Context context = new TestContext(this.applicationContext);
        final Context child = new TestContext(this.applicationContext);

        context.add(NamedTestContext.NAME, child);

        final ContextKey<Context> key = ContextKey.builder(Context.class).name(NamedTestContext.NAME).build();
        final Option<Context> first = context.first(key);
        Assertions.assertTrue(first.present());
        Assertions.assertSame(child, first.get());
    }

    @Test
    void testNamedContextAllByName() {
        final Context context = new TestContext(this.applicationContext);
        final NamedTestContext named = new NamedTestContext();

        context.add(named);

        final ContextKey<Context> key = ContextKey.builder(Context.class).name(NamedTestContext.NAME).build();
        final List<Context> all = context.all(key);
        Assertions.assertNotNull(all);
        Assertions.assertEquals(1, all.size());
    }

    @Test
    void testNamedContextAllByNameAndType() {
        final Context context = new TestContext(this.applicationContext);
        final NamedTestContext named = new NamedTestContext();

        context.add(named);

        final ContextKey<NamedTestContext> key = ContextKey.builder(NamedTestContext.class).name(NamedTestContext.NAME).build();
        final List<NamedTestContext> all = context.all(key);
        Assertions.assertNotNull(all);
        Assertions.assertEquals(1, all.size());
    }

    @Test
    void testManuallyNamedContextAllByName() {
        final Context context = new TestContext(this.applicationContext);
        final Context child = new TestContext(this.applicationContext);

        context.add(NamedTestContext.NAME, child);

        final ContextKey<Context> key = ContextKey.builder(Context.class).name(NamedTestContext.NAME).build();
        final List<Context> all = context.all(key);
        Assertions.assertNotNull(all);
        Assertions.assertEquals(1, all.size());
    }

    @Test
    void testAutoCreatingContext() {
        final ApplicationAwareContext context = new TestContext(this.applicationContext);
        final ContextKey<AutoCreatingContext> key = ContextKey.builder(AutoCreatingContext.class)
                .fallback(AutoCreatingContext::new)
                .build();
        final Option<AutoCreatingContext> first = context.first(key);
        Assertions.assertTrue(first.present());
    }

    public static class TestContext extends DefaultApplicationAwareContext {
        public TestContext(final ApplicationContext applicationContext) {
            super(applicationContext);
        }

    }

    @InstallIfAbsent
    public static class AutoCreatingContext extends DefaultContext {
    }

    public static class NamedTestContext extends DefaultNamedContext {

        static final String NAME = "JUnitContext";

        NamedTestContext() {
            super(NAME);
        }
    }
}
