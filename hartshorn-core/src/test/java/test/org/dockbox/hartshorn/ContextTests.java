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

import java.util.List;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.context.ApplicationAwareContext;
import org.dockbox.hartshorn.context.AutoCreating;
import org.dockbox.hartshorn.context.Context;
import org.dockbox.hartshorn.context.DefaultApplicationAwareContext;
import org.dockbox.hartshorn.context.DefaultContext;
import org.dockbox.hartshorn.context.DefaultNamedContext;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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

        final Option<TestContext> first = context.first(TestContext.class);
        Assertions.assertTrue(first.present());
        Assertions.assertSame(child, first.get());
    }

    @Test
    void testUnnamedContextAll() {
        final Context context = new TestContext(this.applicationContext);
        final Context child = new TestContext(this.applicationContext);

        context.add(child);

        final List<TestContext> all = context.all(TestContext.class);
        Assertions.assertNotNull(all);
        Assertions.assertEquals(1, all.size());
    }

    @Test
    void testNamedContextFirstByName() {
        final Context context = new TestContext(this.applicationContext);
        final NamedTestContext named = new NamedTestContext();

        context.add(named);

        final Option<Context> first = context.first(NamedTestContext.NAME);
        Assertions.assertTrue(first.present());
        Assertions.assertSame(named, first.get());
    }

    @Test
    void testNamedContextFirstByNameAndType() {
        final Context context = new TestContext(this.applicationContext);
        final NamedTestContext named = new NamedTestContext();

        context.add(named);

        final Option<NamedTestContext> first = context.first(NamedTestContext.NAME, NamedTestContext.class);
        Assertions.assertTrue(first.present());
        Assertions.assertSame(named, first.get());
    }

    @Test
    void testManuallyNamedContextFirstByName() {
        final Context context = new TestContext(this.applicationContext);
        final Context child = new TestContext(this.applicationContext);

        context.add(NamedTestContext.NAME, child);

        final Option<Context> first = context.first(NamedTestContext.NAME);
        Assertions.assertTrue(first.present());
        Assertions.assertSame(child, first.get());
    }

    @Test
    void testNamedContextAllByName() {
        final Context context = new TestContext(this.applicationContext);
        final NamedTestContext named = new NamedTestContext();

        context.add(named);

        final List<Context> all = context.all(NamedTestContext.NAME);
        Assertions.assertNotNull(all);
        Assertions.assertEquals(1, all.size());
    }

    @Test
    void testNamedContextAllByNameAndType() {
        final Context context = new TestContext(this.applicationContext);
        final NamedTestContext named = new NamedTestContext();

        context.add(named);

        final List<NamedTestContext> all = context.all(NamedTestContext.NAME, NamedTestContext.class);
        Assertions.assertNotNull(all);
        Assertions.assertEquals(1, all.size());
    }

    @Test
    void testManuallyNamedContextAllByName() {
        final Context context = new TestContext(this.applicationContext);
        final Context child = new TestContext(this.applicationContext);

        context.add(NamedTestContext.NAME, child);

        final List<Context> all = context.all(NamedTestContext.NAME);
        Assertions.assertNotNull(all);
        Assertions.assertEquals(1, all.size());
    }

    @Test
    void testAutoCreatingContext() {
        final ApplicationAwareContext context = new TestContext(this.applicationContext);
        final Option<AutoCreatingContext> first = context.first(AutoCreatingContext.class);
        Assertions.assertTrue(first.present());
    }

    static class TestContext extends DefaultApplicationAwareContext {
        public TestContext(final ApplicationContext applicationContext) {
            super(applicationContext);
        }
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
