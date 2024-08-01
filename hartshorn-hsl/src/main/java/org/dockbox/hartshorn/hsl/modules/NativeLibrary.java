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

package org.dockbox.hartshorn.hsl.modules;

import java.util.List;
import java.util.Map;

import org.dockbox.hartshorn.hsl.ast.statement.ModuleStatement;
import org.dockbox.hartshorn.hsl.ast.statement.NativeFunctionStatement;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.objects.CallableNode;
import org.dockbox.hartshorn.hsl.objects.InstanceReference;
import org.dockbox.hartshorn.hsl.objects.NativeExecutionException;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.util.ApplicationException;

/**
 * A common library containing multiple {@link NativeModule native modules} which can be
 * used to invoke native functions. The library is loaded into the interpreter when a
 * {@link NativeFunctionStatement} or {@link ModuleStatement} is encountered.
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public class NativeLibrary implements CallableNode {

    private final NativeFunctionStatement declaration;
    private final Map<String, NativeModule> externalModules;

    public NativeLibrary(NativeFunctionStatement declaration, Map<String, NativeModule> externalModules) {
        this.declaration = declaration;
        this.externalModules = externalModules;
    }

    public NativeLibrary(NativeFunctionStatement declaration, String moduleName, NativeModule externalModule) {
        this(declaration, Map.of(moduleName, externalModule));
    }

    public NativeFunctionStatement declaration() {
        return this.declaration;
    }

    @Override
    public Object call(Token at, Interpreter interpreter, InstanceReference instance, List<Object> arguments) throws ApplicationException {
        String moduleName = this.declaration.moduleName().lexeme();

        if (!this.externalModules.containsKey(moduleName)) {
            throw new NativeExecutionException("Module Loader : Can't find class with name : " + moduleName);
        }

        NativeModule module = this.externalModules.get(moduleName);
        return module.call(at, interpreter, this.declaration, arguments);
    }
}
