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

package org.dockbox.hartshorn.util.describe;

/**
 * Marker interface for {@link Iterable} or {@link java.util.Map} types that should be described
 * using their object representation, rather than their individual elements.
 *
 * <p>This is a hint to the {@link ObjectDescriber}, and will be respected by all {@link
 * ObjectDescriptionStyle}s.
 *
 * @author Guus Lieben
 * @since 0.7.0
 */
public interface DescribeAsObject {
}
