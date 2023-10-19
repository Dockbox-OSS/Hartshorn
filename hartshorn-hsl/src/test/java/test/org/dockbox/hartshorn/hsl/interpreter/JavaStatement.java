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

package test.org.dockbox.hartshorn.hsl.interpreter;

import java.util.function.Consumer;

import org.dockbox.hartshorn.hsl.ast.statement.Statement;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.FunctionTokenType;
import org.dockbox.hartshorn.hsl.visitors.StatementVisitor;

public class JavaStatement extends Statement {

    private final Consumer<StatementVisitor<?>> consumer;

    public JavaStatement(final Consumer<StatementVisitor<?>> consumer) {
        super(Token.of(FunctionTokenType.NATIVE).build());
        this.consumer = consumer;
    }

    @Override
    public <R> R accept(final StatementVisitor<R> visitor) {
        this.consumer.accept(visitor);
        return null;
    }
}
