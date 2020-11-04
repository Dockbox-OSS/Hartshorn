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

package org.dockbox.selene.sponge.text.navigation;

import org.dockbox.selene.core.objects.targets.MessageReceiver;
import org.dockbox.selene.core.text.Text;
import org.dockbox.selene.core.text.navigation.Pagination;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SpongePagination implements Pagination {

    private Text padding;
    private Number linesPerPage;
    private Text header;
    private Text footer;
    private Text title;
    private List<Text> content;

    SpongePagination(Text padding, Number linesPerPage, Text header, Text footer, Text title, List<Text> content) {

        this.padding = padding;
        this.linesPerPage = linesPerPage;
        this.header = header;
        this.footer = footer;
        this.title = title;
        this.content = content;
    }

    @Override
    public void send(@NotNull MessageReceiver receiver) {
        receiver.sendPagination(this);
    }

    @NotNull
    @Override
    public Text getPadding() {
        return this.padding;
    }

    @Override
    public void setPadding(@NotNull Text padding) {
        this.padding = padding;
    }

    @NotNull
    @Override
    public Number getLinesPerPage() {
        return this.linesPerPage;
    }

    @Override
    public void setLinesPerPage(@NotNull Number linesPerPage) {
        this.linesPerPage = linesPerPage;
    }

    @NotNull
    @Override
    public Text getHeader() {
        return this.header;
    }

    @Override
    public void setHeader(@NotNull Text header) {
        this.header = header;
    }

    @NotNull
    @Override
    public Text getFooter() {
        return this.footer;
    }

    @Override
    public void setFooter(@NotNull Text footer) {
        this.footer = footer;
    }

    @NotNull
    @Override
    public Text getTitle() {
        return this.title;
    }

    @Override
    public void setTitle(@NotNull Text title) {
        this.title = title;
    }

    @NotNull
    @Override
    public List<Text> getContent() {
        return this.content;
    }

    @Override
    public void setContent(@NotNull List<? extends Text> content) {
        this.content = (List<Text>) content;
    }
}
