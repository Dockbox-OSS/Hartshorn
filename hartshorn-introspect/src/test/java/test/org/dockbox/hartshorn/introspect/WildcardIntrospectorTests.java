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

package test.org.dockbox.hartshorn.introspect;

import org.dockbox.hartshorn.util.introspect.AccessModifier;
import org.dockbox.hartshorn.util.introspect.TypeConstructorsIntrospector;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.introspect.view.wildcard.WildcardTypeView;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class WildcardIntrospectorTests {

    @Test
    void wildcardIsParentChildAndEqual() {
        TypeView<Object> view = new WildcardTypeView();

        assertThat(view.isParentOf(Object.class)).isTrue();
        assertThat(view.is(Object.class)).isTrue();
        assertThat(view.isChildOf(Object.class)).isFalse();

        assertThat(view.isParentOf(String.class)).isTrue();
        assertThat(view.is(String.class)).isTrue();
        assertThat(view.isChildOf(String.class)).isFalse();

        assertThat(view.isParentOf(WildcardIntrospectorTests.class)).isTrue();
        assertThat(view.is(WildcardIntrospectorTests.class)).isTrue();
        assertThat(view.isChildOf(WildcardIntrospectorTests.class)).isFalse();
    }

    @Test
    void nameAndQualifiedNameAreNonValidClassNameCharacter() {
        TypeView<Object> view = new WildcardTypeView();

        // Expected to be '*', but not enforced. This is just a test to ensure that the name and qualified
        // name are not valid class names which could be used for reflection.
        String regex = "[a-zA-Z_$][a-zA-Z\\d_$]*";

        boolean isUsableClassName = view.name().matches(regex);
        assertThat(isUsableClassName).isFalse();

        boolean isUsableQualifiedClassName = view.qualifiedName().matches(regex);
        assertThat(isUsableQualifiedClassName).isFalse();
    }

    @Test
    void wildcardHasNoElementType() {
        TypeView<Object> view = new WildcardTypeView();
        assertThat(view.elementType().present()).isFalse();
    }

    @Test
    void wildcardHasEnumConstants() {
        TypeView<Object> view = new WildcardTypeView();
        assertThat(view.enumConstants()).isEmpty();
    }

    @Test
    void wildcardHasNullDefault() {
        TypeView<Object> view = new WildcardTypeView();
        assertThat(view.defaultOrNull()).isNull();
    }

    @Test
    void wildcardCastsAnyObject() {
        TypeView<Object> view = new WildcardTypeView();
        assertThatCode(() -> view.cast(new Object())).doesNotThrowAnyException();
        assertThatCode(() -> view.cast(this)).doesNotThrowAnyException();
        assertThatCode(() -> view.cast("test")).doesNotThrowAnyException();
    }

    @Test
    void wildcardHasNoModifiers() {
        TypeView<Object> view = new WildcardTypeView();
        for (AccessModifier modifier : AccessModifier.values()) {
            assertThat(view.modifiers().has(modifier)).isFalse();
        }
    }

    @Test
    void wildcardIsWildcard() {
        TypeView<Object> view = new WildcardTypeView();
        assertThat(view.isWildcard()).isTrue();
    }

    @Test
    void wildcardConstructors() {
        TypeView<Object> view = new WildcardTypeView();
        TypeConstructorsIntrospector<Object> constructors = view.constructors();
        assertThat(constructors.count()).isZero();
        assertThat(constructors.all()).isEmpty();
        assertThat(constructors.defaultConstructor().absent()).isTrue();
    }

    @Test
    void wildcardHasNoFields() {
        TypeView<Object> view = new WildcardTypeView();
        assertThat(view.fields().all()).isEmpty();
    }

    @Test
    void wildcardHasNoMethods() {
        TypeView<Object> view = new WildcardTypeView();
        assertThat(view.methods().all()).isEmpty();
    }

    @Test
    void wildcardIsOwnSuperClass() {
        TypeView<Object> view = new WildcardTypeView();
        assertThat(view.superClass()).isSameAs(view);
    }

    @Test
    void wildcardIsNoExplicitType() {
        TypeView<Object> view = new WildcardTypeView();
        assertThat(view.isVoid()).isFalse();
        assertThat(view.isAnonymous()).isFalse();
        assertThat(view.isPrimitive()).isFalse();
        assertThat(view.isEnum()).isFalse();
        assertThat(view.isAnnotation()).isFalse();
        assertThat(view.isInterface()).isFalse();
        assertThat(view.isRecord()).isFalse();
    }

    @Test
    void wildcardPackageIsEmpty() {
        TypeView<Object> view = new WildcardTypeView();
        assertThat(view.packageInfo().name()).isEmpty();
        assertThat(view.packageInfo().qualifiedName()).isEmpty();
    }
}
