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

package test.org.dockbox.hartshorn.hsl.interpreter.expression;

import org.dockbox.hartshorn.hsl.ast.expression.LiteralExpression;
import org.dockbox.hartshorn.hsl.ast.expression.SetExpression;
import org.dockbox.hartshorn.hsl.ast.expression.VariableExpression;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterAdapter;
import org.dockbox.hartshorn.hsl.interpreter.expression.SetExpressionInterpreter;
import org.dockbox.hartshorn.hsl.objects.external.ExternalInstance;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.dockbox.hartshorn.util.introspect.NativeProxyLookup;
import org.dockbox.hartshorn.util.introspect.annotations.VirtualHierarchyAnnotationLookup;
import org.dockbox.hartshorn.util.introspect.reflect.ReflectionIntrospector;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import test.org.dockbox.hartshorn.hsl.interpreter.InterpreterTestHelper;

public class SetExpressionInterpreterTests {

    public static class TestClass {
        public String value;
    }

    @Test
    void testSetExpressionCanSetIfDefined() {
        InterpreterAdapter adapter = InterpreterTestHelper.createInterpreterAdapter();
        TestClass reference = new TestClass();

        ReflectionIntrospector introspector = new ReflectionIntrospector(new NativeProxyLookup(),
                new VirtualHierarchyAnnotationLookup());
        TypeView<TestClass> typeView = introspector.introspect(TestClass.class);

        ExternalInstance externalInstance = new ExternalInstance(reference, typeView);
        adapter.visitingScope().define("reference", externalInstance);

        Token externalName = Token.of(TokenType.IDENTIFIER).lexeme("reference").build();
        VariableExpression variableExpression = new VariableExpression(externalName);

        Token propertyValue = Token.of(TokenType.STRING).literal("test").build();
        LiteralExpression literalValue = new LiteralExpression(propertyValue, propertyValue.literal());

        Token propertyName = Token.of(TokenType.IDENTIFIER).lexeme("value").build();
        SetExpression setExpression = new SetExpression(variableExpression, propertyName, literalValue);
        SetExpressionInterpreter interpreter = new SetExpressionInterpreter();

        Object interpretedValue = interpreter.interpret(setExpression, adapter);
        Assertions.assertEquals(literalValue.value(), interpretedValue);

        Object value = reference.value;
        Assertions.assertEquals(literalValue.value(), value);
    }
}