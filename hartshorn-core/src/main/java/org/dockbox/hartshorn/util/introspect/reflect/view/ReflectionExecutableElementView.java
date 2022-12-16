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

import org.dockbox.hartshorn.util.introspect.ExecutableParametersIntrospector;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.TypeVariablesIntrospector;
import org.dockbox.hartshorn.util.introspect.reflect.ReflectionExecutableParametersIntrospector;
import org.dockbox.hartshorn.util.introspect.reflect.ReflectionModifierCarrierView;
import org.dockbox.hartshorn.util.introspect.reflect.ReflectionTypeVariablesIntrospector;
import org.dockbox.hartshorn.util.introspect.view.ExecutableElementView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Executable;
import java.util.List;

public abstract class ReflectionExecutableElementView<Parent> extends ReflectionAnnotatedElementView implements ExecutableElementView<Parent>, ReflectionModifierCarrierView {

    private final Introspector introspector;
    private final Executable executable;

    private ExecutableParametersIntrospector parametersIntrospector;
    private TypeVariablesIntrospector typeVariablesIntrospector;

    public ReflectionExecutableElementView(final Introspector introspector, final Executable executable) {
        super(introspector);
        if (!executable.trySetAccessible()) {
            if (executable.getDeclaringClass() != Object.class) {
                introspector.applicationContext().log().debug("Unable to set executable {} accessible", executable);
            }
        }
        this.introspector = introspector;
        this.executable = executable;
    }

    public Executable executable() {
        return this.executable;
    }

    @Override
    public ExecutableParametersIntrospector parameters() {
        if (this.parametersIntrospector == null) {
            this.parametersIntrospector = new ReflectionExecutableParametersIntrospector(this.introspector, this);
        }
        return this.parametersIntrospector;
    }

    @Override
    public TypeVariablesIntrospector typeVariables() {
        if (this.typeVariablesIntrospector == null) {
            this.typeVariablesIntrospector = new ReflectionTypeVariablesIntrospector(this.introspector, List.of(this.executable.getTypeParameters()));
        }
        return this.typeVariablesIntrospector;
    }

    @Override
    public TypeView<Parent> declaredBy() {
        return (TypeView<Parent>) this.introspector.introspect(this.executable.getDeclaringClass());
    }

    @Override
    protected AnnotatedElement annotatedElement() {
        return this.executable;
    }

    @Override
    public int modifiers() {
        return this.executable.getModifiers();
    }
}
