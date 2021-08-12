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

package org.dockbox.hartshorn.server.minecraft.item;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.i18n.common.Language;
import org.dockbox.hartshorn.i18n.text.Text;
import org.dockbox.hartshorn.server.minecraft.item.persistence.PersistentItemModel;
import org.dockbox.hartshorn.util.ReferencedWrapper;
import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.Setter;

public abstract class ReferencedItem<T> extends ReferencedWrapper<T> implements Item {

    public static final int DEFAULT_STACK_SIZE = 64;
    @Getter @Setter private String id;

    protected ReferencedItem(@NotNull T reference) {
        this.id = this.id();
        this.reference(Exceptional.of(reference));
    }

    protected ReferencedItem(String id) {
        this.id = id;
        T type = this.from(id);
        super.reference(Exceptional.of(type));
    }

    protected abstract T from(String id);

    @Override
    public Text displayName() {
        return this.displayName(Language.EN_US);
    }

    @Override
    public Item stack() {
        this.amount(this.stackSize());
        return this;
    }

    @Override
    public Exceptional<T> constructInitialReference() {
        return Exceptional.empty(); // Handled by constructors
    }

    @Override
    public Class<? extends PersistentItemModel> type() {
        return PersistentItemModelImpl.class;
    }

    @Override
    public PersistentItemModel model() {
        return new PersistentItemModelImpl(this);
    }
}
