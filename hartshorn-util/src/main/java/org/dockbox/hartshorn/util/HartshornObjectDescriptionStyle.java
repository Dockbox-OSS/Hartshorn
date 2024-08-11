/*
 * Copyright 2019-2024 the original author or authors.
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

package org.dockbox.hartshorn.util;

/**
 * Standard implementation of {@link ObjectDescriptionStyle} that describes objects in a style similar
 * to standard Java object descriptions.
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public final class HartshornObjectDescriptionStyle implements ObjectDescriptionStyle {

    /**
     * The constant instance of this style. The instance is stateless and can be used by multiple
     * threads.
     */
    public static final ObjectDescriptionStyle INSTANCE = new HartshornObjectDescriptionStyle();

    private HartshornObjectDescriptionStyle() {
        // Use constant instance
    }

    @Override
    public void describeStart(StringBuilder builder, Object object) {
        builder.append(object.getClass().getSimpleName()).append(" {");
    }

    @Override
    public void describeEnd(StringBuilder builder, Object object) {
        builder.append("}");
    }

    @Override
    public void describeField(StringBuilder builder, Object object, String fieldName, Object fieldValue) {
        builder.append(fieldName).append(": ").append(fieldValue);
    }

    @Override
    public void describeFieldSeparator(StringBuilder builder, Object object) {
        builder.append(", ");
    }
}
