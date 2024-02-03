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

package test.org.dockbox.hartshorn.hsl.interpreter;

import org.dockbox.hartshorn.hsl.ast.ASTNode;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.CacheOnlyResultCollector;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.interpreter.ResultCollector;
import org.dockbox.hartshorn.hsl.interpreter.SimpleVisitorInterpreter;
import org.dockbox.hartshorn.hsl.token.DefaultTokenRegistry;
import org.dockbox.hartshorn.hsl.token.TokenPairList;
import org.dockbox.hartshorn.hsl.token.TokenRegistry;

public final class InterpreterTestHelper {

    private InterpreterTestHelper() {}

    public static Interpreter createInterpreter() {
        final ResultCollector resultCollector = new CacheOnlyResultCollector(null);
        return new SimpleVisitorInterpreter(resultCollector, null, defaultTokenRegistry());
    }

    public static <T extends ASTNode, R> R interpret(T node, ASTNodeInterpreter<R, T> interpreter) {
        return interpreter.interpret(node, createInterpreter());
    }

    public static TokenRegistry defaultTokenRegistry() {
        return DefaultTokenRegistry.createDefault();
    }

    public static TokenPairList defaultTokenPairs() {
        return defaultTokenRegistry().tokenPairs();
    }
}
