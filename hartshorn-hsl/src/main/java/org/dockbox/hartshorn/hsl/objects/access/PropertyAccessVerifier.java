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

package org.dockbox.hartshorn.hsl.objects.access;

import org.dockbox.hartshorn.hsl.interpreter.VariableScope;
import org.dockbox.hartshorn.hsl.objects.InstanceReference;
import org.dockbox.hartshorn.hsl.objects.virtual.VirtualProperty;
import org.dockbox.hartshorn.hsl.runtime.FormattedDiagnostic;
import org.dockbox.hartshorn.hsl.token.Token;

/**
 * A verifier that checks if a property access is allowed. For example, a private property can only be
 * accessed from within the class that defines it.
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public interface PropertyAccessVerifier {

    FormattedDiagnostic read(Token at, VirtualProperty property, InstanceReference instance, VariableScope fromScope);

    FormattedDiagnostic write(Token at, VirtualProperty property, InstanceReference instance, VariableScope fromScope);
}
