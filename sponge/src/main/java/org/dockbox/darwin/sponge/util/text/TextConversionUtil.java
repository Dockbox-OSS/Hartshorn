package org.dockbox.darwin.sponge.util.text;

import org.spongepowered.api.text.Text;

public class TextConversionUtil {

    public static Text toSponge(org.dockbox.darwin.core.text.Text message) {
        // TODO
        message.append("");
        return Text.EMPTY;
    }

    public static org.dockbox.darwin.core.text.Text fromSponge(Text message) {
        // TODO
        return new org.dockbox.darwin.core.text.Text();
    }

}
