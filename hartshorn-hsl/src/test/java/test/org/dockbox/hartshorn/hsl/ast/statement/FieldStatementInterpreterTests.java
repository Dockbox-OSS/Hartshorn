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

package test.org.dockbox.hartshorn.hsl.ast.statement;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.dockbox.hartshorn.hsl.objects.virtual.VirtualInstance;
import org.dockbox.hartshorn.hsl.parser.expression.CallExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.IdentifierExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.LiteralExpressionParser;
import org.dockbox.hartshorn.hsl.parser.statement.ClassStatementParser;
import org.dockbox.hartshorn.hsl.parser.statement.FieldStatementParser;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.LiteralTokenType;
import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.dockbox.hartshorn.util.StringUtilities;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import test.org.dockbox.hartshorn.hsl.support.CaptureModule;
import test.org.dockbox.hartshorn.hsl.support.HSLTestHelper;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@HartshornIntegrationTest(includeBasePackages = false)
public class FieldStatementInterpreterTests {

    @Inject
    private ApplicationContext applicationContext;

    public static Stream<Arguments> fieldStatementVariants() {
        return StringUtilities.matrix()
            .optionalSegment("private ", "public ")
            .optionalSegment("final ")
            .segment("age")
            .build()
            .stream()
            .map(Arguments::of);
    }

    public static Stream<Arguments> fieldStatementVariantsWithInitializer() {
        return StringUtilities.matrix()
            .optionalSegment("private ", "public ")
            .optionalSegment("final ")
            .segment("age = 25")
            .build()
            .stream()
            .map(Arguments::of);
    }

    @ParameterizedTest(name = "Field statement variant: ''{0}''")
    @MethodSource("fieldStatementVariants")
    void fieldVariantsWithoutInitializerDefineAndAssignNull(String variant) {
        HSLTestHelper helper = this.createHelper(variant);
        helper.interpret();
        assertFieldInitialized(helper, null);
    }

    @ParameterizedTest(name = "Field statement variant: ''{0}''")
    @MethodSource("fieldStatementVariantsWithInitializer")
    void fieldVariantsWithoutInitializerDefineAndAssignValue(String variant) {
        HSLTestHelper helper = this.createHelper(variant);
        helper.interpret();
        assertFieldInitialized(helper, 25d);
    }

    private HSLTestHelper createHelper(String variant) {
        return HSLTestHelper.of(this.applicationContext, """
                class Dummy { %s }
                capture(Dummy())
                """.formatted(variant))
            .extensions(extensions -> extensions.statementModules(new CaptureModule()))
            .statementParser(new ClassStatementParser(new FieldStatementParser()))
            .expressionParser(new CallExpressionParser())
            .expressionParser(new LiteralExpressionParser())
            .expressionParser(new IdentifierExpressionParser())
            .build();
    }

    private static void assertFieldInitialized(HSLTestHelper helper, Object expected) {
        Object value = helper.captures().capturedValue();
        VirtualInstance instance = assertThat(value)
                .asInstanceOf(InstanceOfAssertFactories.type(VirtualInstance.class))
                .actual();
        Token ageToken = Token.of(LiteralTokenType.IDENTIFIER)
            .lexeme("age")
            .build();
        Object age = instance.get(
            helper.interpreter(),
            ageToken,
            // Class scope, to ensure private fields are accessible
            instance.virtualClass().variableScope()
        );
        assertThat(age).isEqualTo(expected);
    }
}