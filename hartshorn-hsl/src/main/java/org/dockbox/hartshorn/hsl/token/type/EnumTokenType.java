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

package org.dockbox.hartshorn.hsl.token.type;

import java.util.Locale;

/**
 * Represents a token type that is an enum. This is a convenience interface that allows for the
 * creation of token types that are based on an enum. The name of the enum is used as the token
 * name, and the token type is delegated to the enum's metadata.
 */
public interface EnumTokenType extends DelegateTokenType {

    /**
     * Returns the name of this enum constant, exactly as declared in its enum declaration.
     * 
     * @return the name of this enum constant
     */
    String name();

    @Override
    default String tokenName() {
        return this.name().toLowerCase(Locale.ROOT);
    }
}
