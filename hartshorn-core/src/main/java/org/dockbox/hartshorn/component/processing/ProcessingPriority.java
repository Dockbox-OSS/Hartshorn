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

package org.dockbox.hartshorn.component.processing;

public class ProcessingPriority {

    public static final int HIGHEST_PRECEDENCE = Integer.MIN_VALUE;
    public static final int HIGH_PRECEDENCE = HIGHEST_PRECEDENCE / 2;

    public static final int LOWEST_PRECEDENCE = Integer.MAX_VALUE;
    public static final int LOW_PRECEDENCE = LOWEST_PRECEDENCE / 2;

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
