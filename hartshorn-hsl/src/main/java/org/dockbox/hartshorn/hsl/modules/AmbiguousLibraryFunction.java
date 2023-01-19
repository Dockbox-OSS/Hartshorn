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

package org.dockbox.hartshorn.hsl.modules;

import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.objects.CallableNode;
import org.dockbox.hartshorn.hsl.objects.InstanceReference;
import org.dockbox.hartshorn.hsl.runtime.RuntimeError;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.util.List;
import java.util.Set;

public class AmbiguousLibraryFunction implements CallableNode {

    private final Set<HslLibrary> libraries;

    public AmbiguousLibraryFunction(final Set<HslLibrary> libraries) {
        this.libraries = libraries;
    }

    public Set<HslLibrary> libraries() {
        return this.libraries;
    }

    @Override
    public Object call(final Token at, final Interpreter interpreter, final InstanceReference instance, final List<Object> arguments) throws ApplicationException {
        final List<HslLibrary> applicableLibraries = this.libraries.stream()
                .filter(library -> library.declaration().params().size() == arguments.size())
                .toList();

        if (applicableLibraries.isEmpty()) {
            throw new RuntimeError(at, "No applicable library found for " + arguments.size() + " arguments");
        }
        else if (applicableLibraries.size() > 1) {
            throw new RuntimeError(at, "Multiple applicable libraries found for " + arguments.size() + " arguments");
        }
        else {
            final HslLibrary library = applicableLibraries.get(0);
            return library.call(at, interpreter, instance, arguments);
        }
    }

    private boolean isApplicableFor(final HslLibrary library, final List<Object> arguments) {
        if (library.declaration().params().size() != arguments.size()) {
            return false;
        }
        final MethodView<?, ?> method = library.declaration().method();
        final List<TypeView<?>> parameters = method.parameters().types();
        for (int i = 0; i < parameters.size(); i++) {
            final TypeView<?> parameter = parameters.get(i);
            final Object argument = arguments.get(i);
            if (argument == null && !parameter.isPrimitive()) continue;
            if (!parameter.isParentOf(argument.getClass())) return false;
        }
        return true;
    }
}
