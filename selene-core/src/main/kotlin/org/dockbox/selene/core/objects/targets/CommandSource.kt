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

package org.dockbox.selene.core.objects.targets

import org.dockbox.selene.core.i18n.common.ResourceEntry
import org.dockbox.selene.core.text.Text
import org.dockbox.selene.core.text.pagination.Pagination

interface CommandSource : MessageReceiver {

    fun execute(command: String)

    object None : CommandSource {
        override fun execute(command: String) {
            throw UnsupportedOperationException("Attempted to execute command without source")
        }

        override fun send(text: ResourceEntry) {
            throw UnsupportedOperationException("Attempted to send message without source")
        }

        override fun send(text: Text) {
            throw UnsupportedOperationException("Attempted to send message without source")
        }

        override fun send(text: CharSequence) {
            throw UnsupportedOperationException("Attempted to send message without source")
        }

        override fun sendWithPrefix(text: ResourceEntry) {
            throw UnsupportedOperationException("Attempted to send message without source")
        }

        override fun sendWithPrefix(text: Text) {
            throw UnsupportedOperationException("Attempted to send message without source")
        }

        override fun sendWithPrefix(text: CharSequence) {
            throw UnsupportedOperationException("Attempted to send message without source")
        }

        override fun sendPagination(pagination: Pagination) {
            throw UnsupportedOperationException("Attempted to send message without source")
        }
    }

}

