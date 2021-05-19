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

package org.dockbox.selene.server.minecraft.item;

import org.dockbox.selene.api.domain.Exceptional;
import org.dockbox.selene.api.i18n.common.Language;
import org.dockbox.selene.api.i18n.text.Text;
import org.dockbox.selene.server.minecraft.item.persistence.PersistentItemModel;
import org.dockbox.selene.util.ReferencedWrapper;
import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.Setter;

public abstract class ReferencedItem<T> extends ReferencedWrapper<T> implements Item {

    public static final int DEFAULT_STACK_SIZE = 64;
    @Getter @Setter
    private String id;

    protected ReferencedItem(@NotNull T reference) {
        this.id = this.getId();
        this.setReference(Exceptional.of(reference));
    }

    @Override
    public Text getDisplayName() {
        return this.getDisplayName(Language.EN_US);
    }

    @Override
    public Item stack() {
        this.setAmount(this.getStackSize());
        return this;
    }

    protected ReferencedItem(String id, int meta) {
        this.id = id;
        T type = this.getById(id, meta);
        super.setReference(Exceptional.of(type));
    }

    protected abstract T getById(String id, int meta);

    @Override
    public Exceptional<T> constructInitialReference() {
        return Exceptional.none(); // Handled by constructors
    }

    @Override
    public Class<? extends PersistentItemModel> getModelClass() {
        return SimplePersistentItemModel.class;
    }

    @Override
    public PersistentItemModel toPersistentModel() {
        return new SimplePersistentItemModel(this);
    }
}
