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

package org.dockbox.hartshorn.i18n.text;

import org.dockbox.hartshorn.i18n.MessageReceiver;
import org.dockbox.hartshorn.i18n.common.Message;
import org.dockbox.hartshorn.i18n.text.actions.ClickAction;
import org.dockbox.hartshorn.i18n.text.actions.HoverAction;
import org.dockbox.hartshorn.i18n.text.actions.ShiftClickAction;
import org.dockbox.hartshorn.i18n.text.persistence.PersistentTextModel;
import org.dockbox.hartshorn.persistence.PersistentCapable;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.util.Arrays;
import java.util.List;

public class Text implements PersistentCapable<PersistentTextModel> {

    public static final char legacySectionSymbol = '&';
    public static final char sectionSymbol = '\u00A7';
    private static final String legacyRegexFormat = "[\\$|&][0-9a-fklmnor]";
    private static final String styleChars = "01234567890abcdefklmnor";
    private final List<Text> extra = HartshornUtils.emptyConcurrentList();
    private String text;
    private ClickAction<?> clickAction;
    private HoverAction<?> hoverAction;
    private ShiftClickAction<?> shiftClickAction;

    public Text(Object... objects) {
        if (0 < objects.length) {
            final Object prim = objects[0];

            if (prim instanceof Text) this.text = ((Text) prim).toStringValue();
            else if (prim instanceof Message) this.text = ((Message) prim).asString();
            else this.text = prim.toString();

            for (final char styleChar : styleChars.toCharArray()) {
                this.text = this.text.replaceAll(legacySectionSymbol + "" + styleChar, sectionSymbol + "" + styleChar);
            }
            objects = Arrays.copyOfRange(objects, 1, objects.length);

            for (final Object obj : objects) {
                if (obj instanceof Text) this.extra.add((Text) obj);
                else if (obj instanceof Message) this.extra.add(of(((Message) obj).asString()));
                else this.extra.add(of(obj));
            }
        }
    }

    public String toStringValue() {
        final StringBuilder stringValue = new StringBuilder(this.text);
        for (final Text extraText : this.extra) stringValue.append(' ').append(extraText.text);
        return stringValue.toString();
    }

    public static Text of(final Object... objects) {
        if (0 == objects.length) return new Text("");
        return new Text(objects);
    }

    public String toLegacy() {
        final StringBuilder legacyText = new StringBuilder(this.text.replaceAll(sectionSymbol + "", legacySectionSymbol + ""));
        for (final Text extraText : this.extra) legacyText.append(' ').append(extraText.toLegacy());
        return legacyText.toString();
    }

    public String toPlain() {
        return this.toLegacy().replaceAll(legacyRegexFormat, "");
    }

    public Text append(final Text text) {
        this.extra.add(text);
        return this;
    }

    public Text append(final CharSequence text) {
        this.extra.add(of(text));
        return this;
    }

    public Text append(final char text) {
        this.extra.add(of(text));
        return this;
    }

    public void send(final MessageReceiver... receivers) {
        for (final MessageReceiver receiver : receivers) receiver.send(this);
    }

    public void sendWithPrefix(final MessageReceiver... receivers) {
        for (final MessageReceiver receiver : receivers) receiver.sendWithPrefix(this);
    }

    public ClickAction<?> onClick() {
        return this.clickAction;
    }

    public HoverAction<?> onHover() {
        return this.hoverAction;
    }

    public ShiftClickAction<?> onShiftClick() {
        return this.shiftClickAction;
    }

    public List<Text> parts() {
        final List<Text> parts = HartshornUtils.emptyList();
        // Do not add 'this' directly, as it'd wrap the extra parts as well and cause duplicates
        parts.add(Text.of(this.text)
                .onClick(this.clickAction)
                .onHover(this.hoverAction)
                .onShiftClick(this.shiftClickAction));
        parts.addAll(this.extra());
        return parts;
    }

    public Text onShiftClick(final ShiftClickAction<?> action) {
        this.shiftClickAction = action;
        return this;
    }

    public Text onHover(final HoverAction<?> action) {
        this.hoverAction = action;
        return this;
    }

    public Text onClick(final ClickAction<?> action) {
        this.clickAction = action;
        return this;
    }

    public List<Text> extra() {
        // To prevent stack overflows
        return this.extra.stream().filter(e -> e != this).toList();
    }

    @Override
    public String toString() {
        return this.toLegacy();
    }

    @Override
    public Class<? extends PersistentTextModel> type() {
        return PersistentTextModel.class;
    }

    @Override
    public PersistentTextModel model() {
        return new PersistentTextModel(this);
    }

    public enum HashMethod {
        MD5("MD5"),
        SHA1("SHA-1"),
        SHA256("SHA-256");

        private final String alg;

        HashMethod(final String alg) {
            this.alg = alg;
        }

        @Override
        public String toString() {
            return this.alg;
        }
    }
}
