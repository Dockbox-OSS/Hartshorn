/*
 * Copyright 2019-2024 the original author or authors.
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

package org.dockbox.hartshorn.inject.introspect;

import org.dockbox.hartshorn.context.Context;
import org.dockbox.hartshorn.inject.scope.Scope;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.ExecutableElementView;
import org.dockbox.hartshorn.util.introspect.view.FieldView;
import org.dockbox.hartshorn.util.introspect.view.GenericTypeView;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

/**
 * TODO: #1060 Add documentation
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public interface ViewContextAdapter extends Context {

    ViewContextAdapter scope(Scope scope);

    <T> Option<T> create(ConstructorView<T> constructor) throws Throwable;

    Object[] loadParameters(ExecutableElementView<?> element);

    <P, R> Option<R> invoke(MethodView<P, R> method) throws Throwable;

    <P, R> Option<R> invokeStatic(MethodView<P, R> method) throws Throwable;

    <P, R> Option<R> load(FieldView<P, R> field) throws Throwable;

    <T> Option<T> load(GenericTypeView<T> element) throws Throwable;

    boolean isProxy(TypeView<?> type);
}
