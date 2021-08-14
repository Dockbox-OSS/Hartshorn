package org.dockbox.hartshorn.server.minecraft.inventory.context;

import org.dockbox.hartshorn.di.context.DefaultContext;
import org.dockbox.hartshorn.i18n.text.Text;

import lombok.Getter;

public class RenameContext extends DefaultContext {

    @Getter private final Text out;

    public RenameContext(final ClickContext context, final Text out) {
        this.add(context);
        this.out = out;
    }

}
