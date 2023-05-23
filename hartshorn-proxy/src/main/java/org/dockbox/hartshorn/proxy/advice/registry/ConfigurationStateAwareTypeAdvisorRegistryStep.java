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

package org.dockbox.hartshorn.proxy.advice.registry;

import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.option.Option;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class ConfigurationStateAwareTypeAdvisorRegistryStep<S, T> implements StateAwareTypeAdvisorRegistryStep<S, T> {

    private final StateAwareAdvisorRegistry<T> registry;
    private final Class<S> type;
    private S delegate;

    public ConfigurationStateAwareTypeAdvisorRegistryStep(final StateAwareAdvisorRegistry<T> registry, final Class<S> type) {
        this.registry = registry;
        this.type = type;
    }

    @Override
    public Option<S> delegate() {
        return Option.of(this.delegate);
    }

    @Override
    public Class<S> advisedType() {
        return type;
    }

    @Override
    public AdvisorRegistry<T> delegate(final S delegateInstance) {
        if (delegateInstance != null) {
            for (final Method declaredMethod : this.type.getDeclaredMethods()) {
                this.addDelegateAdvice(delegateInstance, declaredMethod);
            }
            this.delegate = delegateInstance;
        }
        return this.exit();
    }

    @Override
    public AdvisorRegistry<T> delegateAbstractOnly(final S delegateInstance) {
        if (delegateInstance != null) {
            for (final Method declaredMethod : this.type.getDeclaredMethods()) {
                this.delegateAbstractOverrideCandidate(delegateInstance, declaredMethod);
            }
        }
        return this.exit();
    }

    private StateAwareAdvisorRegistry<T> exit() {
        this.registry.state().modify();
        return this.registry;
    }

    private void delegateAbstractOverrideCandidate(final S delegateInstance, final Method declaredMethod) {
        try {
            final Method override = this.registry.advisedType().getMethod(declaredMethod.getName(), declaredMethod.getParameterTypes());
            if (!Modifier.isAbstract(override.getModifiers()) || override.isDefault() || declaredMethod.isDefault()) {
                return;
            }
        } catch (final NoSuchMethodException e) {
            // Ignore error, delegate is not concrete
        }
        this.addDelegateAdvice(delegateInstance, declaredMethod);
    }

    private void addDelegateAdvice(final S delegateInstance, final Method declaredMethod) {
        final MethodAdvisorRegistryStep<T, ?> method = this.registry.method(declaredMethod);
        method.delegate(TypeUtils.adjustWildcards(delegateInstance, Object.class));
    }
}
