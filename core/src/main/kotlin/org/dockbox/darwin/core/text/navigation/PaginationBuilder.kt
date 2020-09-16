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

package org.dockbox.darwin.core.text.navigation

import java.util.concurrent.CopyOnWriteArrayList
import org.dockbox.darwin.core.text.Text

abstract class PaginationBuilder {

    protected var padding: Text? = null
    protected var header: Text? = null
    protected var footer: Text? = null
    protected var title: Text? = null

    protected var linesPerPage = 10
    protected var contents: List<Text> = CopyOnWriteArrayList()

    fun padding(padding: Text): PaginationBuilder {
        this.padding = padding
        return this
    }

    fun linesPerPage(linesPerPage: Int): PaginationBuilder {
        this.linesPerPage = linesPerPage
        return this
    }

    fun header(header: Text): PaginationBuilder {
        this.header = header
        return this
    }

    fun footer(footer: Text): PaginationBuilder {
        this.footer = footer
        return this
    }

    fun title(title: Text): PaginationBuilder {
        this.title = title
        return this
    }

    fun contents(contents: List<Text>): PaginationBuilder {
        this.contents = contents
        return this
    }

    abstract fun build(): Pagination

}
