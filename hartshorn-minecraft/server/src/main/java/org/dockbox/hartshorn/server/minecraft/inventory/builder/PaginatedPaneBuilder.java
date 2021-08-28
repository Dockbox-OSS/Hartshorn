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

package org.dockbox.hartshorn.server.minecraft.inventory.builder;

import org.dockbox.hartshorn.api.domain.tuple.Tuple;
import org.dockbox.hartshorn.api.exceptions.ApplicationException;
import org.dockbox.hartshorn.di.properties.Attribute;
import org.dockbox.hartshorn.i18n.text.Text;
import org.dockbox.hartshorn.server.minecraft.inventory.Element;
import org.dockbox.hartshorn.server.minecraft.inventory.InventoryLayout;
import org.dockbox.hartshorn.server.minecraft.inventory.InventoryType;
import org.dockbox.hartshorn.server.minecraft.inventory.context.ClickContext;
import org.dockbox.hartshorn.server.minecraft.inventory.pane.PaginatedPane;
import org.dockbox.hartshorn.server.minecraft.inventory.properties.LayoutAttribute;
import org.dockbox.hartshorn.server.minecraft.item.Item;
import org.dockbox.hartshorn.server.minecraft.item.ItemTypes;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public abstract class PaginatedPaneBuilder extends DefaultPaneBuilder<PaginatedPane, PaginatedPaneBuilder> {

    @Getter(AccessLevel.PROTECTED)
    private InventoryLayout layout;

    @Setter
    @Getter(AccessLevel.PROTECTED)
    private Text title;

    @Getter(AccessLevel.PROTECTED)
    private final Collection<Element> elements = HartshornUtils.emptyList();

    public static final Function<PaginatedPane, Element> FIRST = paginated -> Element.of(
            paginated.applicationContext(),
            Item.of(paginated.applicationContext(), ItemTypes.PAPER).displayName(Text.of("First")).amount(1),
            ctx -> handle(ctx, pane -> 0)
    );
    public static final Function<PaginatedPane, Element> PREVIOUS = paginated -> {
        final int previous = Math.max(paginated.page() - 1, 0);
        return Element.of(
                paginated.applicationContext(),
                Item.of(paginated.applicationContext(), ItemTypes.PAPER).displayName(Text.of("Previous")).amount(previous + 1),
                ctx -> handle(ctx, pane -> previous)
        );
    };
    public static final Function<PaginatedPane, Element> CURRENT = paginated -> Element.of(
            paginated.applicationContext(),
            Item.of(paginated.applicationContext(), ItemTypes.PAPER).displayName(Text.of("Current")).amount(paginated.page() + 1),
            ctx -> handle(ctx, PaginatedPane::page)
    );
    public static final Function<PaginatedPane, Element> NEXT = paginated -> {
        final int next = Math.min(paginated.page() + 1, paginated.pages() - 1);
        return Element.of(
                paginated.applicationContext(),
                Item.of(paginated.applicationContext(), ItemTypes.PAPER).displayName(Text.of("Next")).amount(next + 1),
                ctx -> handle(ctx, pane -> next)
        );
    };
    public static final Function<PaginatedPane, Element> LAST = paginated -> Element.of(
            paginated.applicationContext(),
            Item.of(paginated.applicationContext(), ItemTypes.PAPER).displayName(Text.of("Last")).amount(paginated.pages()),
            ctx -> handle(ctx, pane -> pane.pages() - 1)
    );

    @Getter(AccessLevel.PROTECTED)
    private final Map<Integer, Function<PaginatedPane, Element>> actions = HartshornUtils.ofEntries(
            Tuple.of(2, FIRST),
            Tuple.of(3, PREVIOUS),
            Tuple.of(4, CURRENT),
            Tuple.of(5, NEXT),
            Tuple.of(6, LAST)
    );

    public PaginatedPaneBuilder() {

    }

    private static boolean handle(final ClickContext context, final Function<PaginatedPane, Integer> action) {
        if (context.pane() instanceof PaginatedPane paginated) {
            paginated.open(context.player(), action.apply(paginated));
            return false;
        } else {
            throw new IllegalStateException("Pagination action used in non-paginated pane");
        }
    }

    @Override
    public void apply(final Attribute<?> property) {
        if (property instanceof LayoutAttribute layoutAttribute) {
            final InventoryLayout layout = layoutAttribute.value();
            final InventoryType type = layout.inventoryType();

            // One row is reserved for pagination actions, and should be a full-sized inventory (9 columns).
            if (type.rows() < 2) throw new IllegalArgumentException("Paginated panes should contain at least 2 rows");
            if (type.columns() < 9) throw new IllegalArgumentException("Paginated panes should contain at least 9 columns");

            this.layout = layout;
        }
    }

    @Override
    public void enable() throws ApplicationException {
        if (this.layout == null) throw new ApplicationException("Missing attribute for InventoryLayout");
    }

    public PaginatedPaneBuilder actions(final Map<Integer, Function<PaginatedPane, Element>> elements) {
        this.actions.clear();
        this.actions.putAll(elements);
        return this;
    }

    public PaginatedPaneBuilder action(final int index, final Function<PaginatedPane, Element> element) {
        this.actions.put(index, element);
        return this;
    }

    public PaginatedPaneBuilder elements(final Collection<Element> elements) {
        this.elements.clear();
        this.elements.addAll(elements);
        return this;
    }

    @Override
    public PaginatedPaneBuilder onClickOutput(final Function<ClickContext, Boolean> onClick) {
        // Pagination has no output types
        return this;
    }

    @Override
    protected PaginatedPaneBuilder self() {
        return this;
    }
}
