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

import java.util.List;
import java.util.Set;

public record AmbiguousLibraryFunction(Set<HslLibrary> libraries) implements CallableNode {

    @Override
    public Object call(Token at, Interpreter interpreter, InstanceReference instance, List<Object> arguments)
            throws ApplicationException {
        List<HslLibrary> applicableLibraries = this.libraries.stream()
                .filter(library -> library.declaration().params().size() == arguments.size())
                .toList();

        if(applicableLibraries.isEmpty()) {
            throw new RuntimeError(at, "No applicable library found for " + arguments.size() + " arguments");
        }
        else if(applicableLibraries.size() > 1) {
            throw new RuntimeError(at, "Multiple applicable libraries found for " + arguments.size() + " arguments");
        }
        else {
            HslLibrary library = applicableLibraries.get(0);
            return library.call(at, interpreter, instance, arguments);
        }
    }
}
