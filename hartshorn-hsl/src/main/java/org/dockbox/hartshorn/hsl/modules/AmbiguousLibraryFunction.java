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

package org.dockbox.hartshorn.hsl.modules;

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.objects.CallableNode;
import org.dockbox.hartshorn.hsl.objects.InstanceReference;
import org.dockbox.hartshorn.hsl.runtime.DiagnosticMessage;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.util.ApplicationException;

import java.util.List;
import java.util.Set;

/**
 * Represents a function that is ambiguous due to multiple libraries with the same name being present. At
 * runtime the correct library will be chosen based on the number of arguments provided. If no libraries
 * match the number of arguments, or if multiple libraries match, an error will be thrown due to the ambiguity.
 *
 * @param libraries The set of libraries that share the same name.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public record AmbiguousLibraryFunction(Set<NativeLibrary> libraries) implements CallableNode {

    @Override
    public Object call(Token at, Interpreter interpreter, InstanceReference instance, List<Object> arguments)
            throws ApplicationException {

        List<NativeLibrary> applicableLibraries = this.libraries.stream()
                .filter(library -> library.declaration().params().size() == arguments.size())
                .toList();

        if(applicableLibraries.isEmpty()) {
            throw ScriptEvaluationError.builder(Phase.INTERPRETING)
                    .message(DiagnosticMessage.NO_COMPATIBLE_LIBRARY_FUNCTION, arguments.size())
                    .at(at)
                    .build();
        }
        else if(applicableLibraries.size() > 1) {
            throw ScriptEvaluationError.builder(Phase.INTERPRETING)
                    .message(DiagnosticMessage.MULTIPLE_COMPATIBLE_LIBRARY_FUNCTIONS, arguments.size())
                    .at(at)
                    .build();
        }
        else {
            CallableNode library = applicableLibraries.getFirst();
            return library.call(at, interpreter, instance, arguments);
        }
    }
}
