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

package org.dockbox.hartshorn.launchpad.logging;

/**
 * An enum that represents the ANSI color codes for console output.
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public enum AnsiColor {
    BLACK("30"),
    RED("31"),
    GREEN("32"),
    YELLOW("33"),
    BLUE("34"),
    MAGENTA("35"),
    CYAN("36"),
    WHITE("37"),
    RESET("39");

    private final String code;

    AnsiColor(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "\u001B[" + this.code + "m";
    }
}
