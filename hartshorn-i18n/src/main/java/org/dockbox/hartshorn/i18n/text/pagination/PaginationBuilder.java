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

package org.dockbox.hartshorn.i18n.text.pagination;

import org.dockbox.hartshorn.i18n.text.Text;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.util.List;

public abstract class PaginationBuilder {

    protected Text padding = Text.of("&m$2");
    protected Text header;
    protected Text footer;
    protected Text title;

    protected int linesPerPage = -1;
    protected List<Text> content = HartshornUtils.emptyConcurrentList();

    public PaginationBuilder padding(Text padding) {
        this.padding = padding;
        return this;
    }

    public PaginationBuilder header(Text header) {
        this.header = header;
        return this;
    }

    public PaginationBuilder footer(Text footer) {
        this.footer = footer;
        return this;
    }

    public PaginationBuilder title(Text title) {
        this.title = title;
        return this;
    }

    public PaginationBuilder linesPerPage(int linesPerPage) {
        this.linesPerPage = linesPerPage;
        return this;
    }

    public PaginationBuilder content(List<Text> content) {
        this.content = content;
        return this;
    }

    public abstract Pagination build();

    protected Text padding() {
        return this.padding;
    }

    protected Text header() {
        return this.header;
    }

    protected Text footer() {
        return this.footer;
    }

    protected Text title() {
        return this.title;
    }

    protected int linesPerPage() {
        return this.linesPerPage;
    }

    protected List<Text> content() {
        return this.content;
    }
}
