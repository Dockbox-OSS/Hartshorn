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

package org.dockbox.hartshorn.playersettings;

import org.dockbox.hartshorn.api.keys.PersistentDataHolder;
import org.dockbox.hartshorn.commands.annotations.Command;
import org.dockbox.hartshorn.di.annotations.service.Service;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.i18n.MessageReceiver;
import org.dockbox.hartshorn.i18n.common.Language;
import org.dockbox.hartshorn.i18n.common.ResourceEntry;
import org.dockbox.hartshorn.i18n.entry.FakeResource;
import org.dockbox.hartshorn.i18n.entry.Resource;
import org.dockbox.hartshorn.i18n.text.Text;
import org.dockbox.hartshorn.playersettings.service.SettingsContext;
import org.dockbox.hartshorn.server.minecraft.inventory.Element;
import org.dockbox.hartshorn.server.minecraft.inventory.InventoryLayout;
import org.dockbox.hartshorn.server.minecraft.inventory.InventoryType;
import org.dockbox.hartshorn.server.minecraft.inventory.builder.PaginatedPaneBuilder;
import org.dockbox.hartshorn.server.minecraft.inventory.pane.PaginatedPane;
import org.dockbox.hartshorn.server.minecraft.inventory.pane.Pane;
import org.dockbox.hartshorn.server.minecraft.item.Item;
import org.dockbox.hartshorn.server.minecraft.item.ItemTypes;
import org.dockbox.hartshorn.server.minecraft.players.Player;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.util.List;

@Service
public class PlayerSettings {

    public static final Setting<Integer> LANGUAGE = Setting.of(Integer.class)
            .id("settings.language")
            .from(Player.LANGUAGE)
            .resource(ctx -> new Resource(ctx, "Language", "settings.language"))
            .description(ctx -> new Resource(ctx, "The language in which messages are displayed to you.", "settings.language.description"))
            .owner(PlayerSettings.class)
            .converter((ctx, ordinal) -> {
                Language language = Language.values()[ordinal];
                return new FakeResource(language.nameLocalized());
            })
            .defaultValue(Language.EN_US::ordinal)
            .display(ctx -> Item.of(ctx, ItemTypes.BOOK_AND_QUILL))
            .ok();

    public static final Setting<Boolean> RECEIVING_NOTIFICATIONS = Setting.of(Boolean.class)
            .id("settings.notifications")
            .resource(ctx -> new Resource(ctx, "Receive notifications", "settings.notifications"))
            .description(ctx -> new Resource(ctx, "Whether to receive server notifications, for example when you change your client-side language preferences.", "settings.notifications.description"))
            .owner(PlayerSettings.class)
            .converter((ctx, value) -> value ? new Resource(ctx, "Yes", "yes") : new Resource(ctx, "No", "no"))
            .defaultValue(() -> true)
            .display(ctx -> Item.of(ctx, ItemTypes.INK_SAC))
            .action(PlayerSettings::toggleNotifications)
            .ok();

    private static void toggleNotifications(final PersistentDataHolder holder) {
        final Boolean receiving = RECEIVING_NOTIFICATIONS.get(holder);
        holder.set(RECEIVING_NOTIFICATIONS, !receiving);
        if (holder instanceof MessageReceiver) {
            ((MessageReceiver) holder).send(Text.of("Receiving notifications: " + !receiving));
        }
    }

    @Command("settings")
    public void settings(final Player source) {
        final PaginatedPaneBuilder builder = InventoryLayout.builder(source.applicationContext(), InventoryType.DOUBLE_CHEST)
                .toPaginatedPaneBuilder();
        builder.title(new Resource(source.applicationContext(), "$1Settings", "settings").translate(source).asText());
        final PaginatedPane pane = builder.build();

        final List<Element> content = source.applicationContext().first(SettingsContext.class).get().settings().stream()
                .map(setting -> this.fromSetting(source.applicationContext(), setting, source, pane))
                .toList();
        pane.elements(content);

        pane.open(source);
    }

    private Element fromSetting(final ApplicationContext context, final Setting<?> setting, final Player source, final Pane build) {
        final Item item = setting.item(context);
        this.modify(item, setting, source);
        return Element.of(context, item, click -> {
            final Player player = click.player();
            setting.action().accept(player);
            this.settings(player);
            return false;
        });
    }

    private void modify(final Item item, final Setting<?> setting, final Player source) {
        item.displayName(Text.of("$1", setting.resource(source.applicationContext()).translate(source), " $3(", this.value(setting, source), "$3)"));
        item.lore(HartshornUtils.singletonList(Text.of("$2", setting.description(source.applicationContext()).translate(source))));
    }

    private <T> ResourceEntry value(final Setting<T> setting, final Player source) {
        final T o = setting.get(source);
        return setting.convert(source.applicationContext(), o).translate(source);
    }


}
