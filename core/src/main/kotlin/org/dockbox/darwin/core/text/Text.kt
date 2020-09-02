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

package org.dockbox.darwin.core.text

import org.dockbox.darwin.core.objects.targets.MessageReceiver
import org.dockbox.darwin.core.text.actions.ClickAction
import org.dockbox.darwin.core.text.actions.HoverAction
import org.dockbox.darwin.core.text.actions.ShiftClickAction
import java.util.concurrent.CopyOnWriteArrayList

open class Text(vararg objects: Any) {

    private lateinit var text: String
    lateinit var clickAction: ClickAction<*>
    lateinit var hoverAction: HoverAction<*>
    lateinit var shiftClickAction: ShiftClickAction<*>

    private var extra: MutableList<Text> = CopyOnWriteArrayList()

    fun toStringValue(): String {
        return this.text
    }

    fun toLegacy(): String {
        var legacyText = this.text.replace(sectionSymbol, legacySectionSymbol)
        for (extraText in extra) legacyText += ' ' + extraText.toLegacy()
        return legacyText
    }

    fun toPlain(): String {
        return this.toLegacy().replace("[$|&][0-9a-fklmnor]", "")
    }

    fun append(text: Text): Text {
        this.extra.add(text)
        return this
    }

    fun append(text: CharSequence): Text {
        this.extra.add(of(text))
        return this
    }

    fun append(text: Char): Text {
        this.extra.add(of(text))
        return this
    }

    open fun onClick(action: ClickAction<*>) {
        this.clickAction = action
    }

    open fun onHover(action: HoverAction<*>) {
        this.hoverAction = action
    }

    open fun onShiftClick(action: ShiftClickAction<*>) {
        this.shiftClickAction = action
    }

    open fun send(vararg receivers: MessageReceiver) {
        for (receiver in receivers) receiver.send(this)
    }

    open fun sendWithPrefix(vararg receivers: MessageReceiver) {
        for (receiver in receivers) receiver.sendWithPrefix(this)
    }

    open fun getParts(): List<Text> {
        return ArrayList<Text>().also {
            it.add(this)
            it.addAll(extra)
        }
    }

    companion object {

        private const val styleChars: String = "01234567890abcdefklmnor"
        const val sectionSymbol: Char = '\u00A7'
        const val legacySectionSymbol: Char = '&'

        fun empty(): Text {
            return Text("")
        }

        fun of(vararg objects: Any): Text {
            return Text(objects)
        }
    }

    init {
        if (objects.isNotEmpty()) {
            val prim = objects[0]
            if (prim is Text) this.text = prim.toStringValue()
            else this.text = prim.toString()

            for (styleChar in styleChars) this.text.replace("${legacySectionSymbol}${styleChar}", "${sectionSymbol}${styleChar}")
            objects.drop(1)

            for (obj in objects) {
                if (obj !is Text) this.extra.add(of(obj))
                else this.extra.add(obj)
            }
        }
    }

}
