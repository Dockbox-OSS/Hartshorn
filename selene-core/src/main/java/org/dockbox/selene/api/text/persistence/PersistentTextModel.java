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

package org.dockbox.selene.api.text.persistence;

import com.google.inject.Singleton;

import org.dockbox.selene.api.annotations.entity.Extract;
import org.dockbox.selene.api.annotations.entity.Extract.Behavior;
import org.dockbox.selene.api.annotations.entity.Metadata;
import org.dockbox.selene.api.objects.persistence.PersistentModel;
import org.dockbox.selene.api.server.Selene;
import org.dockbox.selene.api.text.Text;
import org.dockbox.selene.api.text.actions.ClickAction;
import org.dockbox.selene.api.text.actions.HoverAction;
import org.dockbox.selene.api.text.actions.ShiftClickAction;

@SuppressWarnings("FieldMayBeFinal")
@Singleton
@Extract(Behavior.KEEP)
@Metadata(alias = "text")
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
        extractClickAction(text);
        extractShiftClickAction(text);
        extractHoverAction(text);
    }

    protected void extractClickAction(Text text) {
        ClickAction<?> clickAction = text.getClickAction();
        this.clickActionResult = String.valueOf(clickAction.getResult());
        if (clickAction instanceof ClickAction.ChangePage) this.clickAction = ActionTypes.CHANGE_PAGE;
        else if (clickAction instanceof ClickAction.RunCommand) this.clickAction = ActionTypes.RUN_COMMAND;
        else if (clickAction instanceof ClickAction.OpenUrl) this.clickAction = ActionTypes.OPEN_URL;
        else if (clickAction instanceof ClickAction.SuggestCommand) this.clickAction = ActionTypes.SUGGEST_COMMAND;
        else if (clickAction instanceof ClickAction.ExecuteCallback) {
            this.clickAction = ActionTypes.EXECUTE_CALLBACK;
            this.clickActionResult = "[cannot serialize consumers (yet)]";
        } else this.clickAction = ActionTypes.NONE;
    }

    protected void extractShiftClickAction(Text text) {
        ShiftClickAction<?> shiftClickAction = text.getShiftClickAction();
        this.shiftClickActionResult = String.valueOf(shiftClickAction.getResult());
        if (shiftClickAction instanceof ShiftClickAction.InsertText) this.shiftClickAction = ActionTypes.INSERT_TEXT;
        else this.shiftClickAction = ActionTypes.NONE;
    }

    protected void extractHoverAction(Text text) {
        HoverAction<?> hoverAction = text.getHoverAction();
        this.hoverActionResult = String.valueOf(hoverAction.getResult());
        if (hoverAction instanceof HoverAction.ShowText) this.hoverAction = ActionTypes.SHOW_TEXT;
        else this.hoverAction = ActionTypes.NONE;
    }

    public String getContent() {
        return content;
    }

    public ActionTypes getClickAction() {
        return clickAction;
    }

    public String getClickActionResult() {
        return clickActionResult;
    }

    public ActionTypes getShiftClickAction() {
        return shiftClickAction;
    }

    public String getShiftClickActionResult() {
        return shiftClickActionResult;
    }

    public ActionTypes getHoverAction() {
        return hoverAction;
    }

    public String getHoverActionResult() {
        return hoverActionResult;
    }

    @Override
    public Class<? extends Text> getCapableType() {
        return Text.class;
    }

    @Override
    public Text toPersistentCapable() {
        Text text = Text.of(content);
        switch (this.clickAction) {
            case CHANGE_PAGE:
                text.onClick(ClickAction.changePage(Integer.parseInt(this.clickActionResult)));
                break;
            case EXECUTE_CALLBACK:
                Selene.log().warn("Attempted to deserialize callback for Text object. This is currently not supported.");
                break;
            case RUN_COMMAND:
                text.onClick(ClickAction.runCommand(this.clickActionResult));
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

    protected enum ActionTypes {
        // Click
        CHANGE_PAGE, EXECUTE_CALLBACK, RUN_COMMAND, OPEN_URL, SUGGEST_COMMAND,
        // Shift-click
        INSERT_TEXT,
        // Hover
        SHOW_TEXT,
        // Global
        NONE
    }
}
