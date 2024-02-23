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

package org.dockbox.hartshorn.hsl.ast;

/**
 * Represents a keyword that causes a flow control change in the interpreter. This is used to signal
 * to the interpreter that a loop or switch statement should be exited, or that the current iteration
 * should be skipped.
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public class FlowControlKeyword extends RuntimeException {

    public enum ScopeType {
        NONE,
        LOOP,
        SWITCH,
    }

    public enum MoveType {
        BREAK,
        CONTINUE,
    }

    private final MoveType moveType;

    public FlowControlKeyword(MoveType type) {
        super(null, null, false, false);
        this.moveType = type;
    }

    public MoveType moveType() {
        return this.moveType;
    }
}
