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
import org.jetbrains.annotations.NotNull;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SimplePagination implements Pagination {

    private Text padding;
    private int linesPerPage;
    private Text header;
    private Text footer;
    private Text title;
    private List<Text> content;

    @Override
    public void send(@NotNull MessageReceiver receiver) {
        receiver.send(this);
    }
}
