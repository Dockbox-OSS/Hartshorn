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

import org.dockbox.hartshorn.proxy.constraint.CollectorProxyValidator;
import org.dockbox.hartshorn.proxy.constraint.ProxyConstraint;
import org.dockbox.hartshorn.proxy.constraint.ProxyConstraintViolation;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.util.Set;

/**
 * A constraint that prevents the creation of proxies for classes. This includes sealed classes and records.
 * This constraint is applied by default when using {@link CollectorProxyValidator#withDefaults()}.
 *
 * @since 0.5.0
 * @author Guus Lieben
 */
public class FinalClassConstraint implements ProxyConstraint {

    @Override
    public Set<ProxyConstraintViolation> validate(TypeView<?> typeView) {
        String classType = null;
        if (typeView.isSealed()) {
            classType = "sealed class";
        }
        else if (typeView.isRecord()) {
            classType = "record";
        }
        else if (typeView.modifiers().isFinal()) {
            classType = "class";
        }

        if (classType != null) {
            return Set.of(new ProxyConstraintViolation("Cannot create proxy for " + classType + " " + typeView.qualifiedName()));
        }
        return Set.of();
    }
}
