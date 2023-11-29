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

package org.dockbox.hartshorn.introspect;

import org.dockbox.hartshorn.component.Scope;
import org.dockbox.hartshorn.context.ApplicationAwareContext;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.ExecutableElementView;
import org.dockbox.hartshorn.util.introspect.view.FieldView;
import org.dockbox.hartshorn.util.introspect.view.GenericTypeView;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Attempt;

public interface ViewContextAdapter extends ApplicationAwareContext {

    ViewContextAdapter scope(Scope scope);

    <T> Attempt<T, Throwable> create(ConstructorView<T> constructor);

    Object[] loadParameters(ExecutableElementView<?> element);

    <P, R> Attempt<R, Throwable> invoke(MethodView<P, R> method);

    <P, R> Attempt<R, Throwable> invokeStatic(MethodView<P, R> method);
    
    <P, R> Attempt<R, Throwable> load(FieldView<P, R> field);

    <T> Attempt<T, Throwable> load(GenericTypeView<T> element);

    boolean isProxy(TypeView<?> type);
}
