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

package org.dockbox.hartshorn.cache;

import org.dockbox.hartshorn.util.introspect.view.AnnotatedElementView;

/**
 * The default {@link KeyGenerator} implementation. This implementation uses the
 * {@link #hashCode()} method of the annotated element to generate a key.
 *
 * @author Guus Lieben
 * @since 22.4
 */
public class HashCodeKeyGenerator implements KeyGenerator {

    @Override
    public String generateKey(final AnnotatedElementView<?> element) {
        return element.name() + "_" + element.hashCode();
    }
}
