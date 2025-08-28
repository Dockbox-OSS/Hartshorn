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

package org.dockbox.hartshorn.hsl.ast.statement;

import org.dockbox.hartshorn.hsl.ast.NamedNode;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.StatementVisitor;

/**
 * A statement that defines a test case, comparable to Java's {@code assert} statement. Unlike
 * an assert, a test statement can have a body of multiple statements, which should return a
 * truthy value if the test passes.
 *
 * <p>For example, the statement below defines a test named <code>isPositive</code>:
 * <pre>{@code
 * test("isPositive") {
 *    return value > 0;
 * }
 * }</pre>
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public class TestStatement extends BodyStatement implements NamedNode {

    private final Token name;

    public TestStatement(Token name, BlockStatement body) {
        super(name, body);
        if (name.literal() == null) {
            throw new IllegalArgumentException("Test name cannot be null");
        }
        this.name = name;
    }

    @Override
    public Token name() {
        return this.name;
    }

    @Override
    public <R> R accept(StatementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
