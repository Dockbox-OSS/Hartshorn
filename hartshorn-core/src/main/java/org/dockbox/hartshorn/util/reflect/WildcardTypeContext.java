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

package org.dockbox.hartshorn.util.reflect;

public final class WildcardTypeContext extends TypeContext<Void> {

    private WildcardTypeContext() {
        super(Void.class);
    }

    public static WildcardTypeContext create() {
        return new WildcardTypeContext();
    }

    @Override
    public boolean childOf(final Class<?> to) {
        return true;
    }

    @Override
    public String toString() {
        return "TypeContext{*}";
    }
}
