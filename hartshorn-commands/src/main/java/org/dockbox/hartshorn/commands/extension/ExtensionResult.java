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

package org.dockbox.hartshorn.commands.extension;

import org.dockbox.hartshorn.commands.CommandSource;
import org.dockbox.hartshorn.i18n.Message;

/**
 * Indicates the result of a {@link CommandExecutorExtension}.
 */
public record ExtensionResult(boolean proceed, Message reason, boolean send) {

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
    public static ExtensionResult reject(Message reason) {
        return reject(reason, true);
    }

    /**
     * Gets a new {@link ExtensionResult} which rejects the {@link org.dockbox.hartshorn.commands.CommandExecutor} to
     * proceed. This result will send the provided {@link Message} to the {@link CommandSource} if {@code send} is
     * {@code true}.
     *
     * @param reason The reason
     * @param send Whether to send the reason to the {@link CommandSource}
     *
     * @return The {@link ExtensionResult}
     */
    public static ExtensionResult reject(Message reason, boolean send) {
        return new ExtensionResult(false, reason, send);
    }

    /**
     * Gets whether the {@link org.dockbox.hartshorn.commands.CommandExecutor} requesting
     * the extension should proceed to perform the command directly.
     *
     * @return {@code true} if the executor should proceed, or {@code false}
     */
    @Override
    public boolean proceed() {
        return this.proceed;
    }

    /**
     * Gets the reason an extension has been performed. This will only be sent to the
     * {@link CommandSource} if {@link #send()} is
     * {@code true}.
     *
     * @return The reason
     */
    @Override
    public Message reason() {
        return this.reason;
    }

    /**
     * Gets whether the {@link #reason()} should be sent to the {@link CommandSource}
     * of the executor.
     *
     * @return {@code true} if the {@link #reason()} should be sent, or {@code false}
     */
    @Override
    public boolean send() {
        return this.send;
    }
}
