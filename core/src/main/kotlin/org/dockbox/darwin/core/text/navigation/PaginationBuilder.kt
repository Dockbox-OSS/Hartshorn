package org.dockbox.darwin.core.text.navigation

import org.dockbox.darwin.core.text.Text

abstract class PaginationBuilder {

    private var padding: Text? = null
    private var linesPerPage = -1
    private var header: Text? = null
    private var footer: Text? = null
    private var title: Text? = null
    private var contents: List<Text>? = null

    fun padding(padding: Text?): PaginationBuilder? {
        this.padding = padding
        return this
    }

    fun linesPerPage(linesPerPage: Int): PaginationBuilder? {
        this.linesPerPage = linesPerPage
        return this
    }

    fun header(header: Text?): PaginationBuilder? {
        this.header = header
        return this
    }

    fun footer(footer: Text?): PaginationBuilder? {
        this.footer = footer
        return this
    }

    fun title(title: Text?): PaginationBuilder? {
        this.title = title
        return this
    }

    fun contents(contents: List<Text>?): PaginationBuilder? {
        this.contents = contents
        return this
    }

    abstract fun build(): Pagination?

}
