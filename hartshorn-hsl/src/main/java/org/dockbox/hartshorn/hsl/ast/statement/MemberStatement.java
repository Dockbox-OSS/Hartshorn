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

package org.dockbox.hartshorn.hsl.ast.statement;

import org.dockbox.hartshorn.hsl.ast.NamedNode;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.MemberModifierTokenType;

/**
 * A statement representing a member of a class, for example a property field.
 *
 * @since 0.4.12
 * 
 * @author Guus Lieben
 */
public interface MemberStatement extends NamedNode {

    /**
     * Returns the modifier token of the member, which indicates its visibility (e.g., public,
     * private).
     *
     * @return the member modifier token, or null if no modifier is present
     */
    Token modifier();

    /**
     * Checks if the member is public. A member is considered public if it has no modifier or if
     * its modifier is of type {@link MemberModifierTokenType#PUBLIC}.
     *
     * @return true if the member is public, false otherwise
     */
    default boolean isPublic() {
        return this.modifier() == null || this.modifier().type() == MemberModifierTokenType.PUBLIC;
    }

    /**
     * Checks if the member is private. A member is considered private if its modifier is of type
     * {@link MemberModifierTokenType#PRIVATE}.
     *
     * @return true if the member is private, false otherwise
     */
    default boolean isPrivate() {
        return this.modifier() != null && this.modifier().type() == MemberModifierTokenType.PRIVATE;
    }
}
