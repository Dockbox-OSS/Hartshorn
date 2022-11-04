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

package org.dockbox.hartshorn.util.introspect.reflect.view;

import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.reflect.MethodInvoker;
import org.dockbox.hartshorn.util.introspect.reflect.ReflectionMethodInvoker;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.FailableOption;

import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.StringJoiner;

public class ReflectionMethodView<Parent, ReturnType> extends ReflectionExecutableElementView<Parent> implements MethodView<Parent, ReturnType> {

    private final Introspector introspector;
    private final Method method;

    private MethodInvoker<ReturnType, Parent> invoker;
    private String qualifiedName;

    public ReflectionMethodView(final Introspector introspector, final Method method) {
        super(introspector, method);
        this.introspector = introspector;
        this.method = method;
    }

    @Override
    public Method method() {
        return this.method;
    }

    @Override
    public FailableOption<ReturnType, Throwable> invoke(final Parent instance, final Collection<?> arguments) {
        if (this.invoker == null) {
            this.invoker = new ReflectionMethodInvoker<>();
        }
        return this.invoker.invoke(this, instance, arguments.toArray());
    }

    @Override
    public FailableOption<ReturnType, Throwable> invokeWithContext(final Parent instance) {
        final Object[] args = this.parameters().loadFromContext();
        return this.invoke(instance, args);
    }

    @Override
    public FailableOption<ReturnType, Throwable> invokeWithContext(final Collection<?> arguments) {
        final Parent instance = this.introspector.applicationContext().get(this.declaredBy().type());
        return this.invoke(instance, arguments);
    }

    @Override
    public FailableOption<ReturnType, Throwable> invokeWithContext() {
        final Object[] args = this.parameters().loadFromContext();
        if (this.isStatic()) {
            return this.invokeStatic(args);
        }
        else {
            final Parent instance = this.introspector.applicationContext().get(this.declaredBy().type());
            return this.invoke(instance, args);
        }
    }

    @Override
    public FailableOption<ReturnType, Throwable> invokeStatic(final Collection<?> arguments) {
        if (this.isStatic()) return this.invoke(null, arguments);
        else return FailableOption.of(new IllegalAccessException("Method is not static"));
    }

    @Override
    public FailableOption<ReturnType, Throwable> invokeStaticWithContext() {
        final Object[] args = this.parameters().loadFromContext();
        return this.invokeStatic(args);
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
            final StringJoiner j = new StringJoiner(" ");
            final String shortSig = MethodType.methodType(this.method().getReturnType(), this.method().getParameterTypes()).toString();
            final int split = shortSig.lastIndexOf(')') + 1;
            j.add(shortSig.substring(split)).add(this.method().getName() + shortSig.substring(0, split));
            final String k = j.toString();
            this.qualifiedName = this.declaredBy().name() + '#' + k.substring(k.indexOf(' ') + 1);
        }
        return this.qualifiedName;
    }

    @Override
    public boolean isProtected() {
        return Modifier.isProtected(this.method.getModifiers());
    }

    @Override
    public boolean isPublic() {
        return Modifier.isPublic(this.method.getModifiers());
    }

    @Override
    public boolean isPrivate() {
        return Modifier.isPrivate(this.method.getModifiers());
    }

    @Override
    public boolean isStatic() {
        return Modifier.isStatic(this.method.getModifiers());
    }

    @Override
    public boolean isFinal() {
        return Modifier.isFinal(this.method.getModifiers());
    }

    @Override
    public boolean isAbstract() {
        return Modifier.isAbstract(this.method.getModifiers());
    }

    @Override
    public boolean isDefault() {
        return this.method.isDefault();
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
    public FailableOption<ReturnType, Throwable> getWithContext() {
        return this.invokeWithContext();
    }
}
