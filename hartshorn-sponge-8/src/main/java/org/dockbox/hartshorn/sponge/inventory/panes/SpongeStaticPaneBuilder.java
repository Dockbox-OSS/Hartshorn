package org.dockbox.hartshorn.sponge.inventory.panes;

import org.dockbox.hartshorn.api.exceptions.ApplicationException;
import org.dockbox.hartshorn.di.properties.Attribute;
import org.dockbox.hartshorn.i18n.text.Text;
import org.dockbox.hartshorn.server.minecraft.inventory.InventoryLayout;
import org.dockbox.hartshorn.server.minecraft.inventory.builder.StaticPaneBuilder;
import org.dockbox.hartshorn.server.minecraft.inventory.pane.StaticPane;
import org.dockbox.hartshorn.server.minecraft.inventory.properties.LayoutAttribute;
import org.dockbox.hartshorn.sponge.util.SpongeConvert;
import org.spongepowered.api.item.inventory.menu.InventoryMenu;
import org.spongepowered.api.item.inventory.type.ViewableInventory;

import lombok.Setter;

public class SpongeStaticPaneBuilder extends StaticPaneBuilder {

    private InventoryLayout layout;
    @Setter private Text title;

    @Override
    public void apply(final Attribute<?> property) {
        if (property instanceof LayoutAttribute layoutAttribute) {
            this.layout = layoutAttribute.value();
        }
    }

    @Override
    public void enable() throws ApplicationException {
        if (this.layout == null) throw new ApplicationException("Missing attribute for InventoryLayout");
    }

    @Override
    public StaticPane build() {
        final InventoryMenu menu = ViewableInventory.builder()
                .type(SpongeConvert.toSponge(this.layout.inventoryType()))
                .completeStructure()
                .build().asMenu();
        menu.setTitle(SpongeConvert.toSponge(this.title));

        final StaticPane pane = new SpongeStaticPane(menu);
        pane.update(this.layout);
        return pane;
    }
}
