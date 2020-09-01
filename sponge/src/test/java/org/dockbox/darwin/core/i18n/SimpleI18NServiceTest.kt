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

package org.dockbox.darwin.core.i18n

import org.junit.jupiter.api.Test

internal class SimpleI18NServiceTest {

    private val language: Languages = Languages.EN_US
    private val defaultKey: String = "test.custom"
    private val defaultValue: String = "Sample line"

    @Test
    fun addTranslation() {
        // No need to mock KServer here, as long as .inject() is not called on the service
        val service = SimpleI18NService();
        val custom = SimpleI18NRegistry(defaultValue)
        service.addTranslation(defaultKey, language, custom)

        val entry = service.getEntry(defaultKey, language)
        assert(entry != null)
        assert(entry!!.getValue() == defaultValue)
    }
}
