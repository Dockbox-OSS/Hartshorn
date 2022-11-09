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

package org.dockbox.hartshorn.component.processing;

import java.util.Objects;
import java.util.function.Predicate;

public final class ProcessingPhase implements Predicate<Integer> {

    private final String name;
    private final Predicate<Integer> predicate;
    private final boolean isModifiable;

    ProcessingPhase(final String name, final Predicate<Integer> predicate, final boolean isModifiable) {
        this.name = name;
        this.predicate = predicate;
        this.isModifiable = isModifiable;
    }

    public String name() {
        return this.name;
    }

    @Override
    public boolean test(final Integer integer) {
        return this.predicate.test(integer);
    }

    public boolean modifiable() {
        return this.isModifiable;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        final ProcessingPhase that = (ProcessingPhase) o;
        return this.name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name);
    }
}
