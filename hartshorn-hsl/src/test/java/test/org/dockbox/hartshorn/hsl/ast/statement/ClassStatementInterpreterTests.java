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
import org.junit.jupiter.api.Test;
import test.org.dockbox.hartshorn.hsl.support.HSLTestHelper;

import static org.assertj.core.api.Assertions.assertThat;

@HartshornIntegrationTest(includeBasePackages = false)
class ClassStatementInterpreterTests {

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
        VirtualClass virtualClass = assertThat(classVariable)
                .asInstanceOf(InstanceOfAssertFactories.type(VirtualClass.class))
                .actual();
        assertThat(virtualClass.superClass()).isNull();
        assertThat(virtualClass.isDynamic()).isFalse();

        VirtualProperty name = virtualClass.property("name");
        assertThat(name).isNotNull();

        Object instance = helper.captures().capturedValue();
        VirtualInstance virtualInstance = assertThat(instance)
                .asInstanceOf(InstanceOfAssertFactories.type(VirtualInstance.class))
                .actual();
        assertThat(virtualInstance.virtualClass()).isEqualTo(virtualClass);
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
        VirtualClass virtualClass = assertThat(classVariable)
                .asInstanceOf(InstanceOfAssertFactories.type(VirtualClass.class))
                .actual();
        assertThat(virtualClass.superClass()).isNull();
        assertThat(virtualClass.isDynamic()).isTrue();

        VirtualProperty name = virtualClass.property("name");
        assertThat(name).isNotNull();

        Object instance = helper.captures().capturedValue();
        VirtualInstance virtualInstance = assertThat(instance)
                .asInstanceOf(InstanceOfAssertFactories.type(VirtualInstance.class))
                .actual();
        assertThat(virtualInstance.virtualClass()).isEqualTo(virtualClass);
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
        VirtualClass virtualClass = assertThat(classVariable)
                .asInstanceOf(InstanceOfAssertFactories.type(VirtualClass.class))
                .actual();
        assertThat(virtualClass.isDynamic()).isFalse();

        VirtualProperty name = virtualClass.property("name");
        assertThat(name).isNotNull();

        ClassReference superClass = virtualClass.superClass();
        assertThat(superClass).isNotNull();
        assertThat(superClass.name()).isEqualTo("LivingThing");

        VirtualClass virtualSuperClass = assertThat(superClass)
                .asInstanceOf(InstanceOfAssertFactories.type(VirtualClass.class))
                .actual();
        VirtualProperty age = virtualSuperClass.property("age");
        assertThat(age).isNotNull();

        VirtualProperty childAge = virtualClass.property("age");
        // No override, so definition remains in super class
        assertThat(childAge).isNull();

        Object instance = helper.captures().capturedValue();
        VirtualInstance virtualInstance = assertThat(instance)
                .asInstanceOf(InstanceOfAssertFactories.type(VirtualInstance.class))
                .actual();
        assertThat(virtualInstance.virtualClass()).isEqualTo(virtualClass);
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
        VirtualClass virtualClass = assertThat(classVariable)
                .asInstanceOf(InstanceOfAssertFactories.type(VirtualClass.class))
                .actual();
        assertThat(virtualClass.isDynamic()).isFalse();

        VirtualProperty name = virtualClass.property("name");
        assertThat(name).isNotNull();

        ClassReference superClass = virtualClass.superClass();
        ExternalClass<?> externalClass = assertThat(superClass)
                .asInstanceOf(InstanceOfAssertFactories.type(ExternalClass.class))
                .actual();
        assertThat(externalClass.name()).isEqualTo("ExternalThing");
        assertThat(externalClass.type().is(ExternalThing.class)).isTrue();

        Object instance = helper.captures().capturedValue();
        CompositeInstance<?> compositeInstance = assertThat(instance)
                .asInstanceOf(InstanceOfAssertFactories.type(CompositeInstance.class))
                .actual();
        assertThat(compositeInstance.virtualClass()).isEqualTo(virtualClass);

        Object externalObject = compositeInstance.externalObject();
        assertThat(externalObject).isInstanceOf(ExternalThing.class);
    }
}