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

package org.dockbox.hartshorn.inject;

import java.lang.annotation.Annotation;

import javax.inject.Named;

/**
 * An implementation of the {@link Named} annotation. This is used by {@link Key}s to allow for {@link String}
 * based names instead of always requiring a {@link Named} instance.
 *
 * @author Guus Lieben
 * @since 21.2
 */
public class NamedImpl implements Named {

    private final String value;

    public NamedImpl(final String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return Named.class;
    }

    @Override
    public int hashCode() {
        // This is specified in java.lang.Annotation.
        return (127 * "value".hashCode()) ^ this.value.hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof Named other)) return false;
        return this.value.equals(other.value());
    }

    @Override
    public String toString() {
        return "@" + Named.class.getName() + "(value=" + this.value + ")";
    }
}
