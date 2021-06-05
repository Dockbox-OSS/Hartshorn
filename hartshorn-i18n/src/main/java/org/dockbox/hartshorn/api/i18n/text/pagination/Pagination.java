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

package org.dockbox.hartshorn.api.i18n.text.pagination;

import org.dockbox.hartshorn.api.i18n.MessageReceiver;
import org.dockbox.hartshorn.api.i18n.text.Text;

import java.util.List;

public interface Pagination {

    void send(MessageReceiver receiver);

    Text getPadding();

    void setPadding(Text padding);

    int getLinesPerPage();

    void setLinesPerPage(int linesPerPage);

    Text getHeader();

    void setHeader(Text header);

    Text getFooter();

    void setFooter(Text footer);

    Text getTitle();

    void setTitle(Text title);

    List<Text> getContent();

    void setContent(List<Text> content);
}
