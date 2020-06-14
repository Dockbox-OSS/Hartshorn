package org.dockbox.darwin.core.text

interface TextElement {
    /**
     * Applies this element to the end of the specified builder.
     *
     * @param text Text to apply to
     */
    fun applyTo(text: Text)
}
