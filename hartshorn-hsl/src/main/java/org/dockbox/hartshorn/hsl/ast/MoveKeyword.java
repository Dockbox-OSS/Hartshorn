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

package org.dockbox.hartshorn.hsl.ast;


public class MoveKeyword extends RuntimeException {

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

    public MoveKeyword(final MoveType type) {
        super(null, null, false, false);
        this.moveType = type;
    }

    public MoveType moveType() {
        return this.moveType;
    }
}
