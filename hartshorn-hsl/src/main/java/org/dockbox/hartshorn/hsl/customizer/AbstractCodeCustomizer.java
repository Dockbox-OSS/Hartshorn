/*
 * Copyright 2019-2022 the original author or authors.
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

package org.dockbox.hartshorn.hsl.customizer;

import org.dockbox.hartshorn.hsl.runtime.Phase;

/**
 * Standard implementation of {@link CodeCustomizer}, to simplify the registration of the
 * {@link CodeCustomizer}'s {@link Phase}.
 *
 * @author Guus Lieben
 * @since 22.4
 */
public abstract class AbstractCodeCustomizer implements CodeCustomizer {

    private final Phase phase;

    protected AbstractCodeCustomizer(final Phase phase) {
        this.phase = phase;
    }

    @Override
    public Phase phase() {
        return this.phase;
    }
}
