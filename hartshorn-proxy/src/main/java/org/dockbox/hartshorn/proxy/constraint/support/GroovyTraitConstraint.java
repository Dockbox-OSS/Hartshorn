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

package org.dockbox.hartshorn.proxy.constraint.support;

import org.dockbox.hartshorn.proxy.constraint.ProxyConstraint;
import org.dockbox.hartshorn.proxy.constraint.ProxyConstraintViolation;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * A constraint that prevents the creation of proxies for Groovy traits. This constraint is applied by default when
 * using {@link org.dockbox.hartshorn.proxy.constraint.CollectorProxyValidator#withDefaults()}. This constraint exists
 * as Groovy traits behave differently from Java interfaces, and are not supported by the proxying mechanism.
 *
 * @since 0.5.0
 * @author Guus Lieben
 */
public class GroovyTraitConstraint implements ProxyConstraint {

    private static final String GROOVY_TRAIT = "groovy.transform.Trait";

    @Override
    public Set<ProxyConstraintViolation> validate(TypeView<?> typeView) {
        if (this.isGroovyTrait(typeView.type())) {
            return Set.of(new ProxyConstraintViolation("Cannot create proxy for Groovy trait " + typeView.qualifiedName()));
        }
        return Set.of();
    }

    /**
     * Checks if the given type is a Groovy trait. If the Groovy trait annotation is not available on the classpath,
     * this method will always return {@code false}.
     *
     * @param type the type to check
     * @return {@code true} if the type is a Groovy trait, {@code false} otherwise
     */
    protected boolean isGroovyTrait(Class<?> type) {
        try {
            Class<?> groovyTrait = Class.forName(GROOVY_TRAIT);
            return groovyTrait.isAnnotation() && type.isAnnotationPresent((Class<? extends Annotation>) groovyTrait);
        }
        catch (ClassNotFoundException e) {
            return false;
        }
    }
}
