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

package org.dockbox.hartshorn.hsl.objects;

/**
 * Represents an object that can be made final. This means that the object cannot be modified
 * after it has been made final. This is useful for e.g. constants. Instances of this class
 * are not necessarily immutable, but they are guaranteed to be unmodifiable after they have
 * been made final.
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public interface Finalizable {

    /**
     * @return {@code true} if this object is final, {@code false} otherwise.
     */
    boolean isFinal();

    /**
     * Makes this object final. After this method has been invoked, {@link #isFinal()} will
     * return {@code true}. If this object is already final, this method has no effect.
     */
    void makeFinal();
}
