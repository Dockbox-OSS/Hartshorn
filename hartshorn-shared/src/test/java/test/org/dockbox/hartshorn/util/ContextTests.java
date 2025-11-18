/*
 * Copyright 2019-2025 the original author or authors.
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

import java.util.List;

import org.dockbox.hartshorn.context.Context;

import static org.assertj.core.api.Assertions.assertThat;
import org.dockbox.hartshorn.context.ContextIdentity;
import org.dockbox.hartshorn.context.ContextView;
import org.dockbox.hartshorn.context.DefaultContext;
import org.dockbox.hartshorn.context.DefaultNamedContext;
import org.dockbox.hartshorn.context.SimpleContextIdentity;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Test;

class ContextTests {

    @Test
    void unnamedContextFirst() {
        Context context = new TestContext();
        Context child = new TestContext();

        context.addContext(child);

        ContextIdentity<TestContext> key = new SimpleContextIdentity<>(TestContext.class);
        Option<TestContext> first = context.firstContext(key);
        assertThat(first.present()).isTrue();
        assertThat(first.get()).isSameAs(child);
    }

    @Test
    void unnamedContextAll() {
        Context context = new TestContext();
        Context child = new TestContext();

        context.addContext(child);

        ContextIdentity<TestContext> key = new SimpleContextIdentity<>(TestContext.class);
        List<TestContext> all = context.contexts(key);
        assertThat(all).isNotNull();
        assertThat(all).hasSize(1);
    }

    @Test
    void namedContextFirstByName() {
        Context context = new TestContext();
        NamedTestContext named = new NamedTestContext();

        context.addContext(named);

        ContextIdentity<ContextView> key = new SimpleContextIdentity<>(ContextView.class, NamedTestContext.NAME);
        Option<ContextView> first = context.firstContext(key);
        assertThat(first.present()).isTrue();
        assertThat(first.get()).isSameAs(named);
    }

    @Test
    void namedContextFirstByNameAndType() {
        Context context = new TestContext();
        NamedTestContext named = new NamedTestContext();

        context.addContext(named);

        ContextIdentity<NamedTestContext> key = new SimpleContextIdentity<>(NamedTestContext.class, NamedTestContext.NAME);
        Option<NamedTestContext> first = context.firstContext(key);
        assertThat(first.present()).isTrue();
        assertThat(first.get()).isSameAs(named);
    }

    @Test
    void manuallyNamedContextFirstByName() {
        Context context = new TestContext();
        ContextView child = new TestContext();

        context.addContext(NamedTestContext.NAME, child);

        ContextIdentity<ContextView> key = new SimpleContextIdentity<>(ContextView.class, NamedTestContext.NAME);
        Option<ContextView> first = context.firstContext(key);
        assertThat(first.present()).isTrue();
        assertThat(first.get()).isSameAs(child);
    }

    @Test
    void namedContextAllByName() {
        Context context = new TestContext();
        NamedTestContext named = new NamedTestContext();

        context.addContext(named);

        ContextIdentity<ContextView> key = new SimpleContextIdentity<>(ContextView.class, NamedTestContext.NAME);
        List<ContextView> all = context.contexts(key);
        assertThat(all).isNotNull();
        assertThat(all).hasSize(1);
    }

    @Test
    void namedContextAllByNameAndType() {
        Context context = new TestContext();
        NamedTestContext named = new NamedTestContext();

        context.addContext(named);

        ContextIdentity<NamedTestContext> key = new SimpleContextIdentity<>(NamedTestContext.class, NamedTestContext.NAME);
        List<NamedTestContext> all = context.contexts(key);
        assertThat(all).isNotNull();
        assertThat(all).hasSize(1);
    }

    @Test
    void manuallyNamedContextAllByName() {
        Context context = new TestContext();
        ContextView child = new TestContext();

        context.addContext(NamedTestContext.NAME, child);

        ContextIdentity<ContextView> key = new SimpleContextIdentity<>(ContextView.class, NamedTestContext.NAME);
        List<ContextView> all = context.contexts(key);
        assertThat(all).isNotNull();
        assertThat(all).hasSize(1);
    }

    public static class TestContext extends DefaultContext { }

    public static class NamedTestContext extends DefaultNamedContext {

        static String NAME = "JUnitContext";

        NamedTestContext() {
            super(NAME);
        }
    }
}
