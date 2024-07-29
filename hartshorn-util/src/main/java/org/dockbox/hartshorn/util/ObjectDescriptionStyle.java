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
 * A style definition for describing objects. This interface is used by {@link ObjectDescriber} to
 * determine how to describe objects.
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public interface ObjectDescriptionStyle {

    /**
     * Describes the start of the object. This is typically the class name of the object.
     *
     * @param builder the builder to append the description to
     * @param object the object to describe
     */
    void describeStart(StringBuilder builder, Object object);

    /**
     * Describes the end of the object. This is typically the closing bracket of the object.
     *
     * @param builder the builder to append the description to
     * @param object the object to describe
     */
    void describeEnd(StringBuilder builder, Object object);

    /**
     * Describes a single field of the object. This will be called for each field of the object.
     *
     * @param builder the builder to append the description to
     * @param object the object to describe
     * @param fieldName the name of the field
     * @param fieldValue the value of the field
     */
    void describeField(StringBuilder builder, Object object, String fieldName, Object fieldValue);

    /**
     * Describes the separator between fields. This will be called for each field separator of the
     * object.
     *
     * @param builder the builder to append the description to
     * @param object the object to describe
     */
    void describeFieldSeparator(StringBuilder builder, Object object);
}
