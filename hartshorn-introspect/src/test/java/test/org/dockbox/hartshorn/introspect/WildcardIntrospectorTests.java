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

package test.org.dockbox.hartshorn.introspect;

import org.dockbox.hartshorn.util.introspect.AccessModifier;
import org.dockbox.hartshorn.util.introspect.TypeConstructorsIntrospector;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.introspect.view.wildcard.WildcardTypeView;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class WildcardIntrospectorTests {

    @Test
    void testWildcardIsParentChildAndEqual() {
        final WildcardTypeView view = new WildcardTypeView();

        Assertions.assertTrue(view.isParentOf(Object.class));
        Assertions.assertTrue(view.isChildOf(Object.class));
        Assertions.assertTrue(view.is(Object.class));

        Assertions.assertTrue(view.isParentOf(String.class));
        Assertions.assertTrue(view.isChildOf(String.class));
        Assertions.assertTrue(view.is(String.class));

        Assertions.assertTrue(view.isParentOf(WildcardIntrospectorTests.class));
        Assertions.assertTrue(view.isChildOf(WildcardIntrospectorTests.class));
        Assertions.assertTrue(view.is(WildcardIntrospectorTests.class));
    }

    @Test
    void testNameAndQualifiedNameAreNonValidClassNameCharacter() {
        final TypeView<Object> view = new WildcardTypeView();

        // Expected to be '*', but not enforced. This is just a test to ensure that the name and qualified
        // name are not valid class names which could be used for reflection.
        final String regex = "[a-zA-Z_$][a-zA-Z\\d_$]*";

        final boolean isUsableClassName = view.name().matches(regex);
        Assertions.assertFalse(isUsableClassName);

        final boolean isUsableQualifiedClassName = view.qualifiedName().matches(regex);
        Assertions.assertFalse(isUsableQualifiedClassName);
    }

    @Test
    void testWildcardHasNoElementType() {
        final TypeView<Object> view = new WildcardTypeView();
        Assertions.assertFalse(view.elementType().present());
    }

    @Test
    void testWildcardHasEnumConstants() {
        final TypeView<Object> view = new WildcardTypeView();
        Assertions.assertTrue(view.enumConstants().isEmpty());
    }

    @Test
    void testWildcardHasNullDefault() {
        final TypeView<Object> view = new WildcardTypeView();
        Assertions.assertNull(view.defaultOrNull());
    }

    @Test
    void testWildcardCastsAnyObject() {
        final TypeView<Object> view = new WildcardTypeView();
        Assertions.assertDoesNotThrow(() -> view.cast(new Object()));
        Assertions.assertDoesNotThrow(() -> view.cast(this));
        Assertions.assertDoesNotThrow(() -> view.cast("test"));
    }

    @Test
    void testWildcardHasNoModifiers() {
        final TypeView<Object> view = new WildcardTypeView();
        for (final AccessModifier modifier : AccessModifier.values()) {
            Assertions.assertFalse(view.has(modifier));
        }
    }

    @Test
    void testWildcardIsWildcard() {
        final TypeView<Object> view = new WildcardTypeView();
        Assertions.assertTrue(view.isWildcard());
    }

    @Test
    void testWildcardConstructors() {
        final TypeView<Object> view = new WildcardTypeView();
        final TypeConstructorsIntrospector<Object> constructors = view.constructors();
        Assertions.assertEquals(0, constructors.count());
        Assertions.assertTrue(constructors.all().isEmpty());
        Assertions.assertTrue(constructors.defaultConstructor().absent());
    }

    @Test
    void testWildcardHasNoFields() {
        final TypeView<Object> view = new WildcardTypeView();
        Assertions.assertTrue(view.fields().all().isEmpty());
    }

    @Test
    void testWildcardHasNoMethods() {
        final TypeView<Object> view = new WildcardTypeView();
        Assertions.assertTrue(view.methods().all().isEmpty());
    }

    @Test
    void testWildcardIsOwnSuperClass() {
        final TypeView<Object> view = new WildcardTypeView();
        Assertions.assertSame(view, view.superClass());
    }

    @Test
    void testWildcardIsNoExplicitType() {
        final TypeView<Object> view = new WildcardTypeView();
        Assertions.assertFalse(view.isVoid());
        Assertions.assertFalse(view.isAnonymous());
        Assertions.assertFalse(view.isPrimitive());
        Assertions.assertFalse(view.isEnum());
        Assertions.assertFalse(view.isAnnotation());
        Assertions.assertFalse(view.isInterface());
        Assertions.assertFalse(view.isRecord());
    }

    @Test
    void testWildcardPackageIsEmpty() {
        final TypeView<Object> view = new WildcardTypeView();
        Assertions.assertEquals("", view.packageInfo().name());
        Assertions.assertEquals("", view.packageInfo().qualifiedName());
    }
}
