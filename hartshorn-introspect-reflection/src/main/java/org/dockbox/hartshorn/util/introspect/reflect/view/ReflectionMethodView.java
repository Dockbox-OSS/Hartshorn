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

package org.dockbox.hartshorn.util.introspect.reflect.view;

import org.dockbox.hartshorn.reporting.DiagnosticsPropertyCollector;
import org.dockbox.hartshorn.reporting.Reportable;
import org.dockbox.hartshorn.util.introspect.IllegalIntrospectionException;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.MethodInvoker;
import org.dockbox.hartshorn.util.introspect.reflect.ReflectionIntrospector;
import org.dockbox.hartshorn.util.introspect.reflect.ReflectionMethodInvoker;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.StringJoiner;

public class ReflectionMethodView<Parent, ReturnType> extends ReflectionExecutableElementView<Parent> implements MethodView<Parent, ReturnType> {

    private final Introspector introspector;
    private final Method method;

    private MethodInvoker<ReturnType, Parent> invoker;
    private String qualifiedName;

    public ReflectionMethodView(ReflectionIntrospector introspector, Method method) {
        super(introspector, method);
        this.introspector = introspector;
        this.method = method;
    }

    @Override
    public Option<Method> method() {
        return Option.of(this.method);
    }

    @Override
    public Option<ReturnType> invoke(Object instance, Collection<?> arguments) throws Throwable {
        if (this.invoker == null) {
            this.invoker = new ReflectionMethodInvoker<>();
        }
        Parent checkedInstance = this.declaredBy().cast(instance);
        return this.invoker.invoke(this, checkedInstance, arguments.toArray());
    }

    @Override
    public Option<ReturnType> invokeStatic(Collection<?> arguments) throws Throwable {
        if (this.modifiers().isStatic()) {
            return this.invoke(null, arguments);
        }
        else {
            throw new IllegalIntrospectionException(this, "Method is not static");
        }
    }

    @Override
    public TypeView<ReturnType> returnType() {
        return (TypeView<ReturnType>) this.introspector.introspect(this.method.getReturnType());
    }

    @Override
    public TypeView<ReturnType> genericReturnType() {
        return (TypeView<ReturnType>) this.introspector.introspect(this.method.getGenericReturnType());
    }

    @Override
    public String name() {
        return this.method.getName();
    }

    @Override
    public String qualifiedName() {
        if (this.qualifiedName == null) {
            StringJoiner j = new StringJoiner(" ");
            String shortSig = MethodType.methodType(this.method.getReturnType(), this.method.getParameterTypes()).toString();
            int split = shortSig.lastIndexOf(')') + 1;
            j.add(shortSig.substring(split)).add(this.method.getName() + shortSig.substring(0, split));
            String k = j.toString();
            this.qualifiedName = this.declaredBy().qualifiedName() + '#' + k.substring(k.indexOf(' ') + 1);
        }
        return this.qualifiedName;
    }

    @Override
    public TypeView<ReturnType> type() {
        return this.returnType();
    }

    @Override
    public TypeView<ReturnType> genericType() {
        return this.genericReturnType();
    }

    @Override
    public void report(DiagnosticsPropertyCollector collector) {
        collector.property("name").write(this.name());
        collector.property("elementType").write("method");
        collector.property("returnType").write(this.genericReturnType());
        collector.property("parameters").write(this.parameters().all().toArray(Reportable[]::new));
        collector.property("declaredBy").write(this.declaredBy());
    }
}
