/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.commands.extension;

import org.dockbox.hartshorn.commands.CommandSource;
import org.dockbox.hartshorn.i18n.Message;

import lombok.AllArgsConstructor;

/**
 * Indicates the result of a {@link CommandExecutorExtension}.
 */
@AllArgsConstructor
public final class ExtensionResult {

    private final boolean proceed;
    private final Message reason;
    private final boolean send;

    /**
     * Gets a new {@link ExtensionResult} which allows the {@link org.dockbox.hartshorn.commands.CommandExecutor} to
     * proceed without any notifications to the user.
     *
     * @return The {@link ExtensionResult}
     */
    public static ExtensionResult accept() {
        return new ExtensionResult(true, null, false);
    }

    /**
     * Gets a new {@link ExtensionResult} which rejects the {@link org.dockbox.hartshorn.commands.CommandExecutor} to
     * proceed. This result will send the provided {@link Message} to the {@link CommandSource}.
     *
     * @param reason The reason
     *
     * @return The {@link ExtensionResult}
     */
    public static ExtensionResult reject(final Message reason) {
        return reject(reason, true);
    }

    /**
     * Gets a new {@link ExtensionResult} which rejects the {@link org.dockbox.hartshorn.commands.CommandExecutor} to
     * proceed. This result will send the provided {@link Message} to the {@link CommandSource}
     * if <code>send</code> is <code>true</code>.
     *
     * @param reason The reason
     * @param send Whether to send the reason to the {@link CommandSource}
     *
     * @return The {@link ExtensionResult}
     */
    public static ExtensionResult reject(final Message reason, final boolean send) {
        return new ExtensionResult(false, reason, send);
    }

    /**
     * Gets whether the {@link org.dockbox.hartshorn.commands.CommandExecutor} requesting
     * the extension should proceed to perform the command directly.
     *
     * @return <code>true</code> if the executor should proceed, or <code>false</code>
     */
    public boolean proceed() {
        return this.proceed;
    }

    /**
     * Gets the reason an extension has been performed. This will only be sent to the
     * {@link CommandSource} if {@link #send()} is
     * <code>true</code>.
     *
     * @return The reason
     */
    public Message reason() {
        return this.reason;
    }

    /**
     * Gets whether the {@link #reason()} should be sent to the {@link CommandSource}
     * of the executor.
     *
     * @return <code>true</code> if the {@link #reason()} should be sent, or <code>false</code>
     */
    public boolean send() {
        return this.send;
    }
}
