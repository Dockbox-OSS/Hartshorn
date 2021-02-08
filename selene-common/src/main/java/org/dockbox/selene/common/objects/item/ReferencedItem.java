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

package org.dockbox.selene.common.objects.item;

import org.dockbox.selene.api.objects.Exceptional;
import org.dockbox.selene.api.objects.ReferencedWrapper;
import org.dockbox.selene.api.objects.item.Item;
import org.dockbox.selene.api.server.Selene;
import org.dockbox.selene.api.text.Text;
import org.jetbrains.annotations.NotNull;

public abstract class ReferencedItem<T> extends ReferencedWrapper<T> implements Item
{

    private String id;

    protected ReferencedItem(@NotNull T reference)
    {
        this.id = this.getId();
        this.setReference(Exceptional.of(reference));
    }

    @Override
    public String getId()
    {
        return this.id;
    }

    protected void setId(String id)
    {
        this.id = id;
    }

    @Override
    public Text getDisplayName()
    {
        return this.getDisplayName(Selene.getServer().getGlobalConfig().getDefaultLanguage());
    }

    @Override
    public Item stack()
    {
        this.setAmount(this.getStackSize());
        return this;
    }

    protected ReferencedItem(String id, int meta)
    {
        this.id = id;
        T type = this.getById(id, meta);
        super.setReference(Exceptional.of(type));
    }

    protected abstract T getById(String id, int meta);

    @Override
    public Exceptional<T> constructInitialReference()
    {
        return Exceptional.empty(); // Handled by constructors
    }
}
