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

package org.dockbox.hartshorn.inject;

import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.ComponentKey.Builder;
import org.dockbox.hartshorn.component.ComponentPopulateException;
import org.dockbox.hartshorn.component.QualifierKey;
import org.dockbox.hartshorn.component.Scope;
import org.dockbox.hartshorn.component.processing.Binds;
import org.dockbox.hartshorn.util.Tristate;
import org.dockbox.hartshorn.util.introspect.ElementAnnotationsIntrospector;
import org.dockbox.hartshorn.util.introspect.view.AnnotatedGenericTypeView;
import org.dockbox.hartshorn.util.introspect.view.ExecutableElementView;
import org.dockbox.hartshorn.util.introspect.view.ParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A {@link ComponentKeyResolver} that resolves the {@link ComponentKey} of a component based on the
 * presence of annotations on the component type. The key is resolved as follows:
 * <ul>
 *     <li>The type of the component is used as the type of the key</li>
 *     <li>The scope of the key is set to the given scope</li>
 *     <li>The qualifiers of the key are set to the meta data of {@link Qualifier meta qualifiers} on the component type</li>
 *     <li>The strictness of the key is set to the value of the {@link Strict} annotation on the component type</li>
 *     <li>The auto-enabling of the key is set to the value of the {@link Enable} annotation on the component type</li>
 *     <li>The priority of the key is set to the value of the {@link Priority} annotation on the component type,
 *     or configured based on the declaring element if applicable</li>
 *     <li>If the component type is a {@link Collection}, the key is treated as a collector and the element type of the
 *     collection is used as the type of the key</li>
 * </ul>
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public class StandardAnnotationComponentKeyResolver implements ComponentKeyResolver {

    @Override
    public ComponentKey<?> resolve(AnnotatedGenericTypeView<?> view, Scope scope) {
        TypeView<?> type = view.genericType();
        ComponentKey.Builder<?> builder = ComponentKey.builder(type).scope(scope);

        ElementAnnotationsIntrospector annotations = view.annotations();
        this.configureQualifiers(builder, annotations);
        this.configureStrict(builder, annotations);
        this.configureAutoEnabling(builder, annotations);
        this.configurePriority(builder, view, scope);

        return this.postConfigureKey(builder, view);
    }

    protected ComponentKey<?> postConfigureKey(Builder<?> builder, AnnotatedGenericTypeView<?> view) {
        TypeView<?> type = view.genericType();
        if (type.isChildOf(Collection.class)) {
            TypeView<?> elementType = this.resolveCollectionElementType(type);
            return builder.type(elementType).collector().build();
        }
        else {
            return builder.build();
        }
    }

    protected void configureQualifiers(Builder<?> builder, ElementAnnotationsIntrospector annotations) {
        Set<QualifierKey<?>> qualifiers = this.resolveQualifiers(annotations);
        builder.qualifiers(qualifiers);
    }

    protected Set<QualifierKey<?>> resolveQualifiers(ElementAnnotationsIntrospector annotations) {
        Set<Annotation> metaQualifiers = annotations.annotedWith(Qualifier.class);
        return metaQualifiers.stream()
                .map(QualifierKey::of)
                .collect(Collectors.toSet());
    }

    private void configureStrict(Builder<?> builder, ElementAnnotationsIntrospector annotations) {
        Tristate isStrict = this.isStrict(annotations);
        if (isStrict != Tristate.UNDEFINED) {
            builder.strict(isStrict.booleanValue());
        }
    }

    protected Tristate isStrict(ElementAnnotationsIntrospector annotations) {
        return annotations.get(Strict.class).map(Strict::value)
                .map(Tristate::valueOf)
                .orElse(Tristate.UNDEFINED);
    }

    private void configureAutoEnabling(Builder<?> builder, ElementAnnotationsIntrospector annotations) {
        builder.enable(this.isAutoEnabled(annotations));
    }

    protected boolean isAutoEnabled(ElementAnnotationsIntrospector annotations) {
        return annotations.get(Enable.class).map(Enable::value).orElse(true);
    }

    private void configurePriority(Builder<?> builder, AnnotatedGenericTypeView<?> view, Scope scope) {
        Option<Priority> priorityOption = view.annotations().get(Priority.class);
        if (priorityOption.present()) {
            int parameterPriority = priorityOption.get().value();
            builder.strategy(new ExactPriorityProviderSelectionStrategy(parameterPriority));
        }
        else if(view instanceof ParameterView<?> parameterView) {
            this.configureSelfProvisionCandidate(builder, scope, parameterView);
        }
    }

    private void configureSelfProvisionCandidate(Builder<?> builder, Scope scope, ParameterView<?> parameterView) {
        ExecutableElementView<?> declaredBy = parameterView.declaredBy();
        if (declaredBy instanceof AnnotatedGenericTypeView<?> annotatedDeclaredBy) {
            ComponentKey<?> key = this.resolve(annotatedDeclaredBy, scope);
            boolean selfProvision = key.view().matches(builder.view());
            if (selfProvision) {
                Option<Binds> binds = declaredBy.annotations().get(Binds.class);
                if (binds.present()) {
                    builder.strategy(new MaximumPriorityProviderSelectionStrategy(binds.get().priority()));
                }
            }
        }
    }

    /**
     * Resolves the element type of the given {@link Collection} injection point. If the element type
     * cannot be resolved, an exception is thrown.
     *
     * @param type the injection point to resolve the element type for
     * @return the element type
     *
     * @throws ComponentPopulateException if the element type cannot be resolved
     */
    protected TypeView<?> resolveCollectionElementType(TypeView<?> type) {
        Option<TypeView<?>> elementType = type
                .typeParameters()
                .resolveInputFor(Collection.class)
                .atIndex(0)
                .flatMap(TypeParameterView::resolvedType);

        if (elementType.absent()) {
            throw new ComponentPopulateException("Failed to populate injection point " + type.qualifiedName() + ", could not resolve collection element type");
        }
        return elementType.get();
    }
}
