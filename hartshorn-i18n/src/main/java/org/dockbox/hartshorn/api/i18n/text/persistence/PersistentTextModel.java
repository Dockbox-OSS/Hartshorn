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

package org.dockbox.hartshorn.api.i18n.text.persistence;

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.entity.annotations.Entity;
import org.dockbox.hartshorn.api.i18n.text.Text;
import org.dockbox.hartshorn.api.i18n.text.actions.ClickAction;
import org.dockbox.hartshorn.api.i18n.text.actions.CommandAction;
import org.dockbox.hartshorn.api.i18n.text.actions.HoverAction;
import org.dockbox.hartshorn.api.i18n.text.actions.ShiftClickAction;
import org.dockbox.hartshorn.persistence.PersistentModel;
import org.dockbox.hartshorn.util.Reflect;

import lombok.Getter;

@SuppressWarnings("FieldMayBeFinal")
@Entity(value = "text")
@Getter
public class PersistentTextModel implements PersistentModel<Text> {

    // Legacy formatted
    private String content;

    private ActionTypes clickAction;
    private String clickActionResult;

    private ActionTypes shiftClickAction;
    private String shiftClickActionResult;

    private ActionTypes hoverAction;
    private String hoverActionResult;

    public PersistentTextModel(Text text) {
        this.content = text.toLegacy();
        this.extractClickAction(text);
        this.extractShiftClickAction(text);
        this.extractHoverAction(text);
    }

    protected void extractClickAction(Text text) {
        ClickAction<?> clickAction = text.onClick();
        this.clickActionResult = String.valueOf(clickAction.result());
        if (clickAction instanceof ClickAction.ChangePage) this.clickAction = ActionTypes.CHANGE_PAGE;
        else if (Reflect.lookup("org.dockbox.hartshorn.commands.CommandAction").isInstance(clickAction)) this.clickAction = ActionTypes.RUN_COMMAND;
        else if (clickAction instanceof ClickAction.OpenUrl) this.clickAction = ActionTypes.OPEN_URL;
        else if (clickAction instanceof ClickAction.SuggestCommand) this.clickAction = ActionTypes.SUGGEST_COMMAND;
        else if (clickAction instanceof ClickAction.ExecuteCallback) {
            this.clickAction = ActionTypes.EXECUTE_CALLBACK;
            this.clickActionResult = "[cannot serialize consumers (yet)]";
        } else this.clickAction = ActionTypes.NONE;
    }

    protected void extractShiftClickAction(Text text) {
        ShiftClickAction<?> shiftClickAction = text.onShiftClick();
        this.shiftClickActionResult = String.valueOf(shiftClickAction.result());
        if (shiftClickAction instanceof ShiftClickAction.InsertText) this.shiftClickAction = ActionTypes.INSERT_TEXT;
        else this.shiftClickAction = ActionTypes.NONE;
    }

    protected void extractHoverAction(Text text) {
        HoverAction<?> hoverAction = text.onHover();
        this.hoverActionResult = String.valueOf(hoverAction.result());
        if (hoverAction instanceof HoverAction.ShowText) this.hoverAction = ActionTypes.SHOW_TEXT;
        else this.hoverAction = ActionTypes.NONE;
    }

    @Override
    public Class<? extends Text> capableType() {
        return Text.class;
    }

    @Override
    public Text toPersistentCapable() {
        Text text = Text.of(this.content);
        switch (this.clickAction) {
            case CHANGE_PAGE:
                text.onClick(ClickAction.changePage(Integer.parseInt(this.clickActionResult)));
                break;
            case EXECUTE_CALLBACK:
                Hartshorn.log().warn("Attempted to deserialize callback for Text object. This is currently not supported.");
                break;
            case RUN_COMMAND:
                // Commands are optional actions if the Command module is present
                text.onClick(Hartshorn.context().get(CommandAction.class, this.clickActionResult));
                break;
            case OPEN_URL:
                text.onClick(ClickAction.openUrl(this.clickActionResult));
                break;
            case SUGGEST_COMMAND:
                text.onClick(ClickAction.suggestCommand(this.clickActionResult));
                break;
            case NONE:
            default:
                break;
        }

        switch (this.shiftClickAction) {
            case INSERT_TEXT:
                text.onShiftClick(ShiftClickAction.insertText(Text.of(this.shiftClickActionResult)));
            case NONE:
            default:
                break;
        }

        switch (this.hoverAction) {
            case SHOW_TEXT:
                text.onHover(HoverAction.showText(Text.of(this.hoverActionResult)));
            case NONE:
            default:
                break;
        }
        return text;
    }

}
