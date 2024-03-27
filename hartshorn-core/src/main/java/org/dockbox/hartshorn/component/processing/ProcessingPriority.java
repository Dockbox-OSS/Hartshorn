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

package org.dockbox.hartshorn.component.processing;

/**
 * Constants for processing priorities. These constants can be used to define the order in which components are processed.
 * The lower the value, the higher the priority. The default priority is {@link #NORMAL_PRECEDENCE}.
 *
 * <p>These constants are commonly used in {@link ComponentPreProcessor} and {@link ComponentPostProcessor} implementations.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class ProcessingPriority {

    /**
     * The highest precedence value. This should be used for components that need to be processed first. This value is
     * {@value #HIGHEST_PRECEDENCE} ({@link Integer#MIN_VALUE}, plus {@code 1024} to account for reasonable delta).
     */
    public static final int HIGHEST_PRECEDENCE = Integer.MIN_VALUE + 1024;

    /**
     * High precedence value. This should be used for components that need to be processed early. This value is
     * {@value #HIGH_PRECEDENCE} ({@link #HIGHEST_PRECEDENCE} divided by {@code 2}).
     */
    public static final int HIGH_PRECEDENCE = HIGHEST_PRECEDENCE / 2;

    /**
     * The lowest precedence value. This should be used for components that need to be processed last. This value is
     * {@value #LOWEST_PRECEDENCE} ({@link Integer#MAX_VALUE}, minus {@code 1024} to account for reasonable delta).
     */
    public static final int LOWEST_PRECEDENCE = Integer.MAX_VALUE - 1024;

    /**
     * Low precedence value. This should be used for components that need to be processed late. This value is
     * {@value #LOW_PRECEDENCE} ({@link #LOWEST_PRECEDENCE} divided by {@code 2}).
     */
    public static final int LOW_PRECEDENCE = LOWEST_PRECEDENCE / 2;

    /**
     * Normal precedence value. This should be used for components that need to be processed at the default order. This
     * value is {@value #NORMAL_PRECEDENCE}.
     */

    public static final int NORMAL_PRECEDENCE = 0;

    /**
     * @deprecated Use {@link #HIGHEST_PRECEDENCE} instead.
     */
    @Deprecated(forRemoval = true, since = "0.5.0")
    public static final int FIRST = -256;

    /**
     * @deprecated Use {@link #HIGH_PRECEDENCE} instead.
     */
    @Deprecated(forRemoval = true, since = "0.5.0")
    public static final int EARLY = -128;

    /**
     * @deprecated Use {@link #NORMAL_PRECEDENCE} instead.
     */
    @Deprecated(forRemoval = true, since = "0.5.0")
    public static final int NORMAL = 0;

    /**
     * @deprecated Use {@link #LOW_PRECEDENCE} instead.
     */
    @Deprecated(forRemoval = true, since = "0.5.0")
    public static final int LATE = 128;

    /**
     * @deprecated Use {@link #LOWEST_PRECEDENCE} instead.
     */
    @Deprecated(forRemoval = true, since = "0.5.0")
    public static final int LAST = 256;

}
