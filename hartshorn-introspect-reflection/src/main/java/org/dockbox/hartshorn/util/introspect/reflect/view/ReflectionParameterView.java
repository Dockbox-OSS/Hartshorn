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
import org.dockbox.hartshorn.util.introspect.reflect.ReflectionIntrospector;
import org.dockbox.hartshorn.util.introspect.view.ExecutableElementView;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.view.ParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class ReflectionParameterView<T> extends ReflectionAnnotatedElementView implements ParameterView<T> {

    private final Introspector introspector;
    private final Parameter parameter;
    private String name;
    private ExecutableElementView<?> declaredBy;

    public ReflectionParameterView(final ReflectionIntrospector introspector, final Parameter parameter) {
        super(introspector);
        this.introspector = introspector;
        this.parameter = parameter;
    }

    @Override
    public TypeView<T> type() {
        return (TypeView<T>) this.introspector.introspect(this.parameter.getType());
    }

    @Override
    public TypeView<T> genericType() {
        return (TypeView<T>) this.introspector.introspect(this.parameter.getParameterizedType());
    }

    @Override
    public String name() {
        if (this.name == null) {
            if (this.annotations().has(org.dockbox.hartshorn.util.introspect.Parameter.class)) {
                this.name = this.annotations().get(org.dockbox.hartshorn.util.introspect.Parameter.class).get().value();
            } else {
                this.name = this.parameter.getName();
            }
        }
        return this.name;
    }

    @Override
    public String qualifiedName() {
        final String executableName = this.declaredBy().qualifiedName();
        return executableName + "[" + this.name() + "]";
    }

    @Override
    public boolean isVarArgs() {
        return this.parameter.isVarArgs();
    }

    @Override
    public boolean isNamePresent() {
        return this.parameter.isNamePresent();
    }

    @Override
    public ExecutableElementView<?> declaredBy() {
        if (this.declaredBy == null) {
            final Executable executable = this.parameter.getDeclaringExecutable();
            if (executable instanceof Method method) this.declaredBy = this.introspector.introspect(method);
            else if (executable instanceof Constructor<?> constructor) this.declaredBy = this.introspector.introspect(constructor);
            else throw new RuntimeException("Unexpected executable type: " + executable.getName());
        }
        return this.declaredBy;
    }

    @Override
    protected AnnotatedElement annotatedElement() {
        return this.parameter;
    }

    @Override
    public void report(final DiagnosticsPropertyCollector collector) {
        collector.property("name").write(this.name());
        collector.property("type").write(this.genericType());
    }
}
