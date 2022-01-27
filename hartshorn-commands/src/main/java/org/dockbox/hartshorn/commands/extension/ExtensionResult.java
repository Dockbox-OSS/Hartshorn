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
