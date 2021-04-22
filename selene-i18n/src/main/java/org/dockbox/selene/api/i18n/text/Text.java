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

package org.dockbox.selene.api.i18n.text;

import org.dockbox.selene.api.Selene;
import org.dockbox.selene.api.domain.Exceptional;
import org.dockbox.selene.api.i18n.MessageReceiver;
import org.dockbox.selene.api.i18n.common.ResourceEntry;
import org.dockbox.selene.api.i18n.text.actions.ClickAction;
import org.dockbox.selene.api.i18n.text.actions.HoverAction;
import org.dockbox.selene.api.i18n.text.actions.ShiftClickAction;
import org.dockbox.selene.api.i18n.text.persistence.PersistentTextModel;
import org.dockbox.selene.persistence.PersistentCapable;
import org.dockbox.selene.util.SeleneUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.DatatypeConverter;

public class Text implements PersistentCapable<PersistentTextModel> {

    public static final char legacySectionSymbol = '&';
    public static final char sectionSymbol = '\u00A7';
    private static final String legacyRegexFormat = "[\\$|&][0-9a-fklmnor]";
    private static final String styleChars = "01234567890abcdefklmnor";
    private final List<Text> extra = SeleneUtils.emptyConcurrentList();
    private String text;
    private ClickAction<?> clickAction;
    private HoverAction<?> hoverAction;
    private ShiftClickAction<?> shiftClickAction;

    public Text(Object... objects) {
        if (0 < objects.length) {
            Object prim = objects[0];

            if (prim instanceof Text) this.text = ((Text) prim).toStringValue();
            else if (prim instanceof ResourceEntry) this.text = ((ResourceEntry) prim).asString();
            else this.text = prim.toString();

            for (char styleChar : styleChars.toCharArray()) {
                this.text = this.text.replaceAll(legacySectionSymbol + "" + styleChar, sectionSymbol + "" + styleChar);
            }
            objects = Arrays.copyOfRange(objects, 1, objects.length);

            for (Object obj : objects) {
                if (obj instanceof Text) this.extra.add((Text) obj);
                if (obj instanceof ResourceEntry) this.extra.add(of(((ResourceEntry) obj).asString()));
                else this.extra.add(of(obj));
            }
        }
    }

    public String toStringValue() {
        StringBuilder stringValue = new StringBuilder(this.text);
        for (Text extraText : this.extra) stringValue.append(' ').append(extraText.text);
        return stringValue.toString();
    }

    public static Text of(Object... objects) {
        if (0 == objects.length) return new Text("");
        return new Text(objects);
    }

    public String toLegacy() {
        StringBuilder legacyText = new StringBuilder(this.text.replaceAll(sectionSymbol + "", legacySectionSymbol + ""));
        for (Text extraText : this.extra) legacyText.append(' ').append(extraText.toLegacy());
        return legacyText.toString();
    }

    public String toPlain() {
        return this.toLegacy().replaceAll(legacyRegexFormat, "");
    }

    public Text append(Text text) {
        this.extra.add(text);
        return this;
    }

    public Text append(CharSequence text) {
        this.extra.add(of(text));
        return this;
    }

    public Text append(char text) {
        this.extra.add(of(text));
        return this;
    }

    public void send(MessageReceiver... receivers) {
        for (MessageReceiver receiver : receivers) receiver.send(this);
    }

    public void sendWithPrefix(MessageReceiver... receivers) {
        for (MessageReceiver receiver : receivers) receiver.sendWithPrefix(this);
    }

    public ClickAction<?> getClickAction() {
        return this.clickAction;
    }

    public HoverAction<?> getHoverAction() {
        return this.hoverAction;
    }

    public ShiftClickAction<?> getShiftClickAction() {
        return this.shiftClickAction;
    }

    public List<Text> getParts() {
        List<Text> parts = SeleneUtils.emptyList();
        // Do not add 'this' directly, as it'd wrap the extra parts as well and cause duplicates
        parts.add(Text.of(this.text)
                .onClick(this.clickAction)
                .onHover(this.hoverAction)
                .onShiftClick(this.shiftClickAction));
        parts.addAll(this.getExtra());
        return parts;
    }

    public Text onShiftClick(ShiftClickAction<?> action) {
        this.shiftClickAction = action;
        return this;
    }

    public Text onHover(HoverAction<?> action) {
        this.hoverAction = action;
        return this;
    }

    public Text onClick(ClickAction<?> action) {
        this.clickAction = action;
        return this;
    }

    public List<Text> getExtra() {
        // To prevent stack overflows
        return this.extra.stream().filter(e -> e != this).collect(Collectors.toList());
    }

    public Exceptional<String> generateHash(HashMethod method) {
        try {
            MessageDigest md = MessageDigest.getInstance(method.toString());
            md.update(this.toStringValue().getBytes());
            return Exceptional.of(DatatypeConverter.printHexBinary(md.digest()).toUpperCase());
        }
        catch (NoSuchAlgorithmException e) {
            Selene.handle("No algorithm implementation present for " + method + ". " + "This algorithm should be implemented by every implementation of the Java platform! " + "See https://docs.oracle.com/javase/7/docs/api/java/security/MessageDigest.html", e);
        }
        return Exceptional.none();
    }

    @Override
    public String toString() {
        return this.toLegacy();
    }

    @Override
    public Class<? extends PersistentTextModel> getModelClass() {
        return PersistentTextModel.class;
    }

    @Override
    public PersistentTextModel toPersistentModel() {
        return new PersistentTextModel(this);
    }

    public enum HashMethod {
        MD5("MD5"),
        SHA1("SHA-1"),
        SHA256("SHA-256");

        private final String alg;

        HashMethod(String alg) {
            this.alg = alg;
        }

        @Override
        public String toString() {
            return this.alg;
        }
    }
}
