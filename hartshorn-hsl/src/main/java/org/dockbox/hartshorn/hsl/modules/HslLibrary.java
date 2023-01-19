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

import org.dockbox.hartshorn.hsl.ast.statement.ModuleStatement;
import org.dockbox.hartshorn.hsl.ast.statement.NativeFunctionStatement;
import org.dockbox.hartshorn.hsl.objects.CallableNode;
import org.dockbox.hartshorn.hsl.objects.InstanceReference;
import org.dockbox.hartshorn.hsl.objects.NativeExecutionException;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.util.ApplicationException;

import java.util.List;
import java.util.Map;

/**
 * A common library containing multiple {@link NativeModule native modules} which can be
 * used to invoke native functions. The library is loaded into the interpreter when a
 * {@link NativeFunctionStatement} or {@link ModuleStatement} is encountered.
 *
 * @author Guus Lieben
 * @since 22.4
 */
public class HslLibrary implements CallableNode {

    private final NativeFunctionStatement declaration;
    private final Map<String, NativeModule> externalModules;

    public HslLibrary(final NativeFunctionStatement declaration, final Map<String, NativeModule> externalModules) {
        this.declaration = declaration;
        this.externalModules = externalModules;
    }

    public HslLibrary(final NativeFunctionStatement declaration, final String moduleName, final NativeModule externalModule) {
        this(declaration, Map.of(moduleName, externalModule));
    }

    public NativeFunctionStatement declaration() {
        return this.declaration;
    }

    @Override
    public Object call(final Token at, final Interpreter interpreter, final InstanceReference instance, final List<Object> arguments) throws ApplicationException {
        final String moduleName = this.declaration.moduleName().lexeme();

        if (!this.externalModules.containsKey(moduleName)) {
            throw new NativeExecutionException("Module Loader : Can't find class with name : " + moduleName);
        }

        final NativeModule module = this.externalModules.get(moduleName);
        return module.call(at, interpreter, this.declaration, arguments);
    }
}
