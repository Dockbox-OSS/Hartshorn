/*
 * Copyright 2019-2026 the original author or authors.
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

package test.org.dockbox.hartshorn.hsl.ast.statement;

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.statement.ClassMemberStatement;
import org.dockbox.hartshorn.hsl.ast.statement.FieldStatement;
import org.dockbox.hartshorn.hsl.ast.statement.Statement;
import org.dockbox.hartshorn.hsl.customizer.DefaultScriptStatementsParserCustomizer;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.interpreter.statement.ClassMemberInterpreter;
import org.dockbox.hartshorn.hsl.objects.virtual.VirtualClassBuilder;
import org.dockbox.hartshorn.hsl.objects.virtual.VirtualProperty;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.parser.statement.ClassMemberParser;
import org.dockbox.hartshorn.hsl.runtime.DiagnosticMessage;
import org.dockbox.hartshorn.hsl.runtime.FormattedDiagnostic;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.BaseTokenType;
import org.dockbox.hartshorn.hsl.token.type.TokenType;
import org.dockbox.hartshorn.hsl.visitors.StatementVisitor;
import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Test;
import test.org.dockbox.hartshorn.hsl.support.HSLTestHelper;
import test.org.dockbox.hartshorn.hsl.support.ScriptAssertions;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@HartshornIntegrationTest(includeBasePackages = false)
public class DynamicClassMemberExtensionTests {

    @Inject
    private ApplicationContext applicationContext;

    public static class CustomMemberStatement extends Statement implements ClassMemberStatement {

        private final Token keyword;
        private final Token name;
        private final Expression initializer;

        public CustomMemberStatement(Token keyword, Token name, Expression initializer) {
            super(keyword);
            this.keyword = keyword;
            this.name = name;
            this.initializer = initializer;
        }

        public Token keyword() {
            return this.keyword;
        }

        public Token name() {
            return this.name;
        }

        public Expression initializer() {
            return this.initializer;
        }

        @Override
        public <R> R accept(StatementVisitor<R> visitor) {
            return null;
        }
    }

    public static class CustomMemberParser
        implements ClassMemberParser<CustomMemberStatement> {

        @Override
        public Option<? extends CustomMemberStatement> parse(
            TokenParser parser,
            TokenStepValidator validator
        ) {
            TokenType identifier = parser.tokenRegistry().literals().identifier();
            if (parser.check(identifier) && "custom_prop".equals(parser.peek().lexeme())) {
                Token keyword = parser.advance();
                Token name = validator.expect(identifier, "custom property name");
                Expression initializer = null;
                if (parser.match(BaseTokenType.EQUAL)) {
                    initializer = parser.expression();
                }
                validator.expectAfter(parser.tokenRegistry().statementEnd(),
                    "custom property declaration");
                return Option.of(new CustomMemberStatement(keyword, name, initializer));
            }
            return Option.empty();
        }

        @Override
        public Set<Class<? extends CustomMemberStatement>> types() {
            return Set.of(CustomMemberStatement.class);
        }
    }

    public static class CustomMemberInterpreter
        implements ClassMemberInterpreter<CustomMemberStatement> {

        @Override
        public void interpret(
            CustomMemberStatement node,
            Interpreter interpreter,
            VirtualClassBuilder builder
        ) {
            FieldStatement fieldStatement =
                new FieldStatement(node.keyword(), node.name(), node.initializer(), false);
            VirtualProperty virtualProperty = new VirtualProperty(fieldStatement);
            builder.field(node.name().lexeme(), virtualProperty);
        }

        @Override
        public Set<Class<? extends CustomMemberStatement>> types() {
            return Set.of(CustomMemberStatement.class);
        }
    }

    @Test
    void customClassMemberCanBeRegisteredAndEvaluated() {
        HSLTestHelper helper = HSLTestHelper.of(this.applicationContext, """
                class Server {
                    custom_prop port = 8080;
                }
                var server = Server();
                capture(server.port)
                """)
            .withCaptureModule()
            .parser(new DefaultScriptStatementsParserCustomizer())
            .classMemberParser(new CustomMemberParser())
            .classMemberInterpreter(new CustomMemberInterpreter())
            .build();

        Object value = helper.interpretValue();
        assertThat(value).isEqualTo(8080.0);
    }

    @Test
    void unsupportedClassBodyMemberThrowsParsingError() {
        HSLTestHelper helper = HSLTestHelper.of(this.applicationContext, """
                class Invalid {
                    123;
                }
                """)
            .parser(new DefaultScriptStatementsParserCustomizer())
            .build();

        FormattedDiagnostic expectedMessage = FormattedDiagnostic.builder()
            .message(DiagnosticMessage.UNSUPPORTED_BODY_STATEMENT)
            .argument("number")
            .build();

        ScriptEvaluationError error =
            assertThatExceptionOfType(ScriptEvaluationError.class)
                .isThrownBy(helper::interpret)
                .actual();

        ScriptAssertions.assertEvaluationError(error, expectedMessage);
    }

    @Test
    void customMemberCoexistsWithStandardMembers() {
        HSLTestHelper helper = HSLTestHelper.of(this.applicationContext, """
                class User {
                    name;
                    title = "Dr";
                    custom_prop specialty = "Cardiology";
                    constructor(name) {
                        this.name = name;
                    }
                    function fullTitle() {
                        return this.title + " " + this.name + " (" + this.specialty + ")";
                    }
                }
                var user = User("House");
                capture(user.fullTitle())
                """)
            .withCaptureModule()
            .parser(new DefaultScriptStatementsParserCustomizer())
            .classMemberParser(new CustomMemberParser())
            .classMemberInterpreter(new CustomMemberInterpreter())
            .build();

        Object value = helper.interpretValue();
        assertThat(value).isEqualTo("Dr House (Cardiology)");
    }
}
