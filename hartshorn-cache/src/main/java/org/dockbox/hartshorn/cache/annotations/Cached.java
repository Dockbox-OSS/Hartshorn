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

package org.dockbox.hartshorn.cache.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Method decorator which indicates the return value of the method can and
 * should be cached.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Cached {
    /**
     * The ID of the target cache. If this is left empty a name will be
     * generated based on the owning service.
     *
     * @return the cache ID
     */
    String value() default "";

    /**
     * Indicates whether the cache should automatically expire. If this is
     * left empty the cache will never expire unless manually evicted. The
     * duration indicated is activated the moment the method is first called.
     *
     * @return the lifetime
     */
    Expire expires() default @Expire(amount = -1, unit = TimeUnit.NANOSECONDS);
}
