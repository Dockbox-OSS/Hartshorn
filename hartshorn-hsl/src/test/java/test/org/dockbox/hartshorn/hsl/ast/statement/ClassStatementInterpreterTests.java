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

import org.dockbox.hartshorn.hsl.customizer.CodeCustomizer;
import org.dockbox.hartshorn.hsl.objects.ClassReference;
import org.dockbox.hartshorn.hsl.objects.external.CompositeInstance;
import org.dockbox.hartshorn.hsl.objects.external.ExternalClass;
import org.dockbox.hartshorn.hsl.objects.virtual.VirtualClass;
import org.dockbox.hartshorn.hsl.objects.virtual.VirtualInstance;
import org.dockbox.hartshorn.hsl.objects.virtual.VirtualProperty;
import org.dockbox.hartshorn.hsl.parser.expression.CallExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.IdentifierExpressionParser;
import org.dockbox.hartshorn.hsl.parser.statement.ClassStatementParser;
import org.dockbox.hartshorn.hsl.parser.statement.FieldStatementParser;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import test.org.dockbox.hartshorn.hsl.support.HSLTestHelper;

@HartshornIntegrationTest(includeBasePackages = false)
public class ClassStatementInterpreterTests {

    @Inject
    private ApplicationContext applicationContext;

    @Test
    void virtualClassDefinitionCanInitialize() {
        HSLTestHelper helper = HSLTestHelper.of(this.applicationContext, """
                        class Person {
                            name;
                        }
                        capture(Person())
                        """)
                .withCaptureModule()
                .statementParser(new ClassStatementParser(new FieldStatementParser()))
                .expressionParser(new CallExpressionParser())
                .expressionParser(new IdentifierExpressionParser())
                .build();

        helper.interpret();

        Object classVariable = helper.findVariable("Person");
        VirtualClass virtualClass = Assertions.assertInstanceOf(VirtualClass.class, classVariable);
        Assertions.assertNull(virtualClass.superClass());
        Assertions.assertFalse(virtualClass.isDynamic());

        VirtualProperty name = virtualClass.property("name");
        Assertions.assertNotNull(name);

        Object instance = helper.captures().capturedValue();
        VirtualInstance virtualInstance = Assertions.assertInstanceOf(
                VirtualInstance.class,
                instance
        );
        Assertions.assertEquals(virtualClass, virtualInstance.virtualClass());
    }

    @Test
    void dynamicClassDefinitionCanInitialize() {
        HSLTestHelper helper = HSLTestHelper.of(this.applicationContext, """
                        class Person? {
                            name;
                        }
                        capture(Person())
                        """)
                .withCaptureModule()
                .statementParser(new ClassStatementParser(new FieldStatementParser()))
                .expressionParser(new CallExpressionParser())
                .expressionParser(new IdentifierExpressionParser())
                .build();

        helper.interpret();

        Object classVariable = helper.findVariable("Person");
        VirtualClass virtualClass = Assertions.assertInstanceOf(VirtualClass.class, classVariable);
        Assertions.assertNull(virtualClass.superClass());
        Assertions.assertTrue(virtualClass.isDynamic());

        VirtualProperty name = virtualClass.property("name");
        Assertions.assertNotNull(name);

        Object instance = helper.captures().capturedValue();
        VirtualInstance virtualInstance = Assertions.assertInstanceOf(
                VirtualInstance.class,
                instance
        );
        Assertions.assertEquals(virtualClass, virtualInstance.virtualClass());
    }

    @Test
    void virtualChildClassDefinitionCanInitialize() {
        HSLTestHelper helper = HSLTestHelper.of(this.applicationContext, """
                        class LivingThing {
                            age;
                        }
                        class Person extends LivingThing {
                            name;
                        }
                        capture(Person())
                        """)
                .withCaptureModule()
                .statementParser(new ClassStatementParser(new FieldStatementParser()))
                .expressionParser(new CallExpressionParser())
                .expressionParser(new IdentifierExpressionParser())
                .build();

        helper.interpret();

        Object classVariable = helper.findVariable("Person");
        VirtualClass virtualClass = Assertions.assertInstanceOf(VirtualClass.class, classVariable);
        Assertions.assertFalse(virtualClass.isDynamic());

        VirtualProperty name = virtualClass.property("name");
        Assertions.assertNotNull(name);

        ClassReference superClass = virtualClass.superClass();
        Assertions.assertNotNull(superClass);
        Assertions.assertEquals("LivingThing", superClass.name());

        VirtualClass virtualSuperClass = Assertions.assertInstanceOf(VirtualClass.class, superClass);
        VirtualProperty age = virtualSuperClass.property("age");
        Assertions.assertNotNull(age);

        VirtualProperty childAge = virtualClass.property("age");
        // No override, so definition remains in super class
        Assertions.assertNull(childAge);

        Object instance = helper.captures().capturedValue();
        VirtualInstance virtualInstance = Assertions.assertInstanceOf(
                VirtualInstance.class,
                instance
        );
        Assertions.assertEquals(virtualClass, virtualInstance.virtualClass());
    }

    static class ExternalThing {
        public int age;
    }

    @Test
    void virtualChildClassOfExternalClassDefinitionCanInitialize() {
        HSLTestHelper helper = HSLTestHelper.of(this.applicationContext, """
                        class Person extends ExternalThing {
                            name;
                        }
                        capture(Person())
                        """)
                .withCaptureModule()
                .customize(CodeCustomizer.of(Phase.INTERPRETING, context -> {
                    context.runtime().imports(ExternalThing.class);
                }))
                .statementParser(new ClassStatementParser(new FieldStatementParser()))
                .expressionParser(new CallExpressionParser())
                .expressionParser(new IdentifierExpressionParser())
                .build();

        helper.interpret();

        Object classVariable = helper.findVariable("Person");
        VirtualClass virtualClass = Assertions.assertInstanceOf(VirtualClass.class, classVariable);
        Assertions.assertFalse(virtualClass.isDynamic());

        VirtualProperty name = virtualClass.property("name");
        Assertions.assertNotNull(name);

        ClassReference superClass = virtualClass.superClass();
        ExternalClass<?> externalClass = Assertions.assertInstanceOf(ExternalClass.class, superClass);
        Assertions.assertEquals("ExternalThing", externalClass.name());
        Assertions.assertTrue(externalClass.type().is(ExternalThing.class));

        Object instance = helper.captures().capturedValue();
        CompositeInstance<?> compositeInstance = Assertions.assertInstanceOf(
                CompositeInstance.class,
                instance
        );
        Assertions.assertEquals(virtualClass, compositeInstance.virtualClass());

        Object externalObject = compositeInstance.externalObject();
        Assertions.assertInstanceOf(ExternalThing.class, externalObject);
    }
}