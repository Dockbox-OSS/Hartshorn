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

package org.dockbox.hartshorn.logging;

/**
 * Represents a message that can be printed to the console with ANSI color and style. This class is immutable and
 * can only be chained together with other messages. Chaining messages allows you to mix colors and styles in a single
 * message.
 *
 * <p>Example usage:
 * <pre>{@code
 * AnsiMessage hello = AnsiMessage.of("Hello, ", AnsiColor.GREEN, AnsiStyle.BOLD);
 * AnsiMessage world = AnsiMessage.of("world!", AnsiColor.BLUE, AnsiStyle.UNDERLINE);
 * AnsiMessage helloWorld = hello.append(world);
 * }</pre>
 *
 * <p>This will print "Hello, " in green and bold, and "world!" in blue and underlined.
 *
 * @param message the message to print
 * @param color the color of the message
 * @param style the style of the message
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public record AnsiMessage(String message, AnsiColor color, AnsiStyle style) {

    /**
     * Creates a new message with the given color, using the message and style of this message.
     *
     * @param color the color to use
     * @return a new message with the given color
     */
    public AnsiMessage color(AnsiColor color) {
        return of(this.message, color, this.style);
    }

    /**
     * Creates a new message with the given style, using the message and color of this message.
     *
     * @param style the style to use
     * @return a new message with the given style
     */
    public AnsiMessage style(AnsiStyle style) {
        return of(this.message, this.color, style);
    }

    /**
     * Appends the given message to this message, using the same color and style as this message.
     *
     * @param message the message to append
     * @return a new message with the appended message
     */
    public AnsiMessage append(String message) {
        return of(this + message);
    }

    /**
     * Appends the given object to this message, retaining the color and style of each message.
     *
     * @param message the message to append
     * @return a new message with the appended message
     */
    public AnsiMessage append(AnsiMessage message) {
        return this.append(message.toString());
    }

    /**
     * Creates a new message with the given message, without any color or style.
     *
     * @param message the message to use
     * @return a new message with the given message
     */
    public static AnsiMessage of(String message) {
        return of(message, AnsiColor.RESET, AnsiStyle.RESET);
    }

    /**
     * Creates a new message with the given message and color, without any style.
     *
     * @param message the message to use
     * @param color the color to use
     * @return a new message with the given message and color
     */
    public static AnsiMessage of(String message, AnsiColor color) {
        return of(message, color, AnsiStyle.RESET);
    }

    /**
     * Creates a new message with the given message and style, without any color.
     *
     * @param message the message to use
     * @param style the style to use
     * @return a new message with the given message and style
     */
    public static AnsiMessage of(String message, AnsiStyle style) {
        return of(message, AnsiColor.RESET, style);
    }

    /**
     * Creates a new message with the given message, color, and style.
     *
     * @param message the message to use
     * @param color the color to use
     * @param style the style to use
     * @return a new message with the given message, color, and style
     */
    public static AnsiMessage of(String message, AnsiColor color, AnsiStyle style) {
        return new AnsiMessage(message, color, style);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.color);
        builder.append(this.style);
        builder.append(this.message);
        // If already reset, don't reset again
        if (this.style != AnsiStyle.RESET) {
            builder.append(AnsiStyle.RESET);
        }
        if (this.color != AnsiColor.RESET) {
            builder.append(AnsiColor.RESET);
        }
        return builder.toString();
    }
}
