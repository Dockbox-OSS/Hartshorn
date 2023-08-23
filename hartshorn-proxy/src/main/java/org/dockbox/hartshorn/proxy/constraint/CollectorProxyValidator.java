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

package org.dockbox.hartshorn.proxy.constraint;

import org.dockbox.hartshorn.proxy.constraint.support.FinalClassConstraint;
import org.dockbox.hartshorn.proxy.constraint.support.GroovyTraitConstraint;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * A {@link ProxyValidator} that collects multiple {@link ProxyConstraint}s and applies them all when validating a
 * type. This class is thread-safe.
 *
 * @since 0.5.0
 * @author Guus Lieben
 */
public class CollectorProxyValidator implements ProxyValidator {

    private final Set<ProxyConstraint> constraints = ConcurrentHashMap.newKeySet();

    @Override
    public void add(final ProxyConstraint constraint) {
        this.constraints.add(constraint);
    }

    @Override
    public Set<ProxyConstraint> constraints() {
        return Set.copyOf(this.constraints);
    }

    @Override
    public Set<ProxyConstraintViolation> validate(final TypeView<?> type) {
        return this.constraints.stream()
                .flatMap(constraint -> constraint.validate(type).stream())
                .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Adds the default constraints to this validator. The default constraints are:
     * <ul>
     *     <li>{@link FinalClassConstraint}</li>
     *     <li>{@link GroovyTraitConstraint}</li>
     * </ul>
     *
     * @return this validator
     */
    public CollectorProxyValidator withDefaults() {
        this.add(new FinalClassConstraint());
        this.add(new GroovyTraitConstraint());
        return this;
    }
}
