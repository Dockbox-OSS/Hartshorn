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

package org.dockbox.hartshorn.util.introspect.view.wildcard;

import org.dockbox.hartshorn.reporting.DiagnosticsPropertyCollector;
import org.dockbox.hartshorn.util.introspect.AccessModifier;
import org.dockbox.hartshorn.util.introspect.ElementAnnotationsIntrospector;
import org.dockbox.hartshorn.util.introspect.TypeConstructorsIntrospector;
import org.dockbox.hartshorn.util.introspect.TypeFieldsIntrospector;
import org.dockbox.hartshorn.util.introspect.TypeMethodsIntrospector;
import org.dockbox.hartshorn.util.introspect.TypeParametersIntrospector;
import org.dockbox.hartshorn.util.introspect.view.PackageView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

import java.util.ArrayList;
import java.util.List;

public class WildcardTypeView implements TypeView<Object> {

    @Override
    public ElementAnnotationsIntrospector annotations() {
        return new WildcardElementAnnotationsIntrospector();
    }

    @Override
    public Class<Object> type() {
        return Object.class;
    }

    @Override
    public boolean isVoid() {
        return false;
    }

    @Override
    public boolean isAnonymous() {
        return false;
    }

    @Override
    public boolean isPrimitive() {
        return false;
    }

    @Override
    public boolean isEnum() {
        return false;
    }

    @Override
    public boolean isAnnotation() {
        return false;
    }

    @Override
    public boolean isInterface() {
        return false;
    }

    @Override
    public boolean isRecord() {
        return false;
    }

    @Override
    public boolean isAbstract() {
        return false;
    }

    @Override
    public boolean isFinal() {
        return false;
    }

    @Override
    public boolean isStatic() {
        return false;
    }

    @Override
    public boolean isArray() {
        return false;
    }

    @Override
    public boolean isWildcard() {
        return true;
    }

    @Override
    public boolean isDeclaredIn(final String prefix) {
        return false;
    }

    @Override
    public boolean isInstance(final Object object) {
        return true;
    }

    @Override
    public List<TypeView<?>> interfaces() {
        return new ArrayList<>();
    }

    @Override
    public TypeView<?> superClass() {
        return this;
    }

    @Override
    public TypeMethodsIntrospector<Object> methods() {
        return new WildcardTypeMethodsIntrospector();
    }

    @Override
    public TypeFieldsIntrospector<Object> fields() {
        return new WildcardTypeFieldsIntrospector();
    }

    @Override
    public TypeConstructorsIntrospector<Object> constructors() {
        return new WildcardTypeConstructorsIntrospector();
    }

    @Override
    public TypeParametersIntrospector typeParameters() {
        return new WildcardTypeParametersIntrospector();
    }

    @Override
    public boolean isParentOf(final Class<?> type) {
        return true;
    }

    @Override
    public boolean isChildOf(final Class<?> type) {
        return true;
    }

    @Override
    public boolean is(final Class<?> type) {
        return true;
    }

    @Override
    public String name() {
        return "*";
    }

    @Override
    public String qualifiedName() {
        return "*";
    }

    @Override
    public Option<TypeView<?>> elementType() {
        return Option.empty();
    }

    @Override
    public List<Object> enumConstants() {
        return new ArrayList<>();
    }

    @Override
    public Object defaultOrNull() {
        return null;
    }

    @Override
    public Object cast(final Object object) {
        return object;
    }

    @Override
    public boolean has(final AccessModifier modifier) {
        return false;
    }

    @Override
    public PackageView packageInfo() {
        return new WildcardPackageView();
    }

    @Override
    public void report(final DiagnosticsPropertyCollector collector) {
        collector.property("name").write("*");
        collector.property("wildcard").write(true);
    }
}
