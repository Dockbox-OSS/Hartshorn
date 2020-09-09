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

package org.dockbox.darwin.core.text;

import org.dockbox.darwin.core.objects.targets.MessageReceiver;
import org.dockbox.darwin.core.server.Server;
import org.dockbox.darwin.core.text.actions.ClickAction;
import org.dockbox.darwin.core.text.actions.HoverAction;
import org.dockbox.darwin.core.text.actions.ShiftClickAction;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.xml.bind.DatatypeConverter;

public class Text {

    private static final String legacyRegexFormat = "[$|&][0-9a-fklmnor]";

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

    private static final char legacySectionSymbol = '&';
    private static final char sectionSymbol = '\u00A7';
    private static final String styleChars = "01234567890abcdefklmnor";

    private String text;
    private ClickAction<?> clickAction;
    private HoverAction<?> hoverAction;
    private ShiftClickAction<?> shiftClickAction;
    private final List<Text> extra = new CopyOnWriteArrayList<>();

    public Text(Object... objects) {
        if (0 < objects.length) {
            Object prim = objects[0];
            if (prim instanceof Text) this.text = ((Text) prim).toStringValue();
            else this.text = prim.toString();

            for (char styleChar : styleChars.toCharArray()) {
                this.text = this.text.replaceAll(legacySectionSymbol + "" + styleChar, sectionSymbol + "" + styleChar);
            }
            objects = Arrays.copyOfRange(objects, 1, objects.length);

            for (Object obj : objects) {
                if (obj instanceof Text) this.extra.add((Text) obj);
                else this.extra.add(of(obj));
            }
        }
    }

    public String toStringValue() {
        return this.text; // TODO: Include extra
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

    public void onClick(ClickAction<?> action) {
        this.clickAction = action;
    }

    public void onHover(HoverAction<?> action) {
        this.hoverAction = action;
    }

    public void onShiftClick(ShiftClickAction<?> action) {
        this.shiftClickAction = action;
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
        List<Text> parts = new ArrayList<>();
        parts.add(this);
        parts.addAll(this.extra);
        return parts;
    }

    public static Text of(Object... objects) {
        return new Text(objects);
    }

    public Optional<String> generateHash(HashMethod method) {
        try {
            MessageDigest md = MessageDigest.getInstance(method.toString());
            md.update(this.toStringValue().getBytes());
            return Optional.of(DatatypeConverter.printHexBinary(md.digest()).toUpperCase());
        } catch (NoSuchAlgorithmException e) {
            Server.getServer().except("No algorithm implementation present for " + method.toString() + ". " +
                            "This algorithm should be implemented by every implementation of the Java platform! " +
                            "See https://docs.oracle.com/javase/7/docs/api/java/security/MessageDigest.html",
                    e);
        }
        return Optional.empty();
    }

}
