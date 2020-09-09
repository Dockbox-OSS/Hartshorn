/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.darwin.core.util.extension.status

enum class ExtensionStatus(val intValue: Int) {

    LOADED(1), FAILED(2), DISABLED(3), ERRORED(4),

    // Deprecated values are the negative equivalent of the values above, so they can easily be obtained using their intValue
    // Deprecated values are only to be used when comparing status results, and never be assigned manually
    DEPRECATED_LOADED(-1), DEPRECATED_FAILED(-2), DEPRECATED_DISABLED(-3), DEPRECATED_ERRORED(-4)
    ;

    companion object {
        fun of(intValue: Int): ExtensionStatus {
            return try {
                values().first() { it.intValue == intValue }
            } catch (e: NoSuchElementException) {
                FAILED
            }
        }
    }

}
