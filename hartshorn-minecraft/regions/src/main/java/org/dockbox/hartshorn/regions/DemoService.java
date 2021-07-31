package org.dockbox.hartshorn.regions;

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.domain.tuple.Vector3N;
import org.dockbox.hartshorn.api.i18n.entry.Resource;
import org.dockbox.hartshorn.api.i18n.text.Text;
import org.dockbox.hartshorn.commands.annotations.Command;
import org.dockbox.hartshorn.commands.context.CommandContext;
import org.dockbox.hartshorn.di.annotations.service.Service;
import org.dockbox.hartshorn.regions.flags.BooleanFlag;
import org.dockbox.hartshorn.regions.flags.IntegerFlag;

import java.util.UUID;

@Service
public class DemoService {

    @Command("regions")
    public void regions(CommandContext context) {
        final DefaultRegionService regions = Hartshorn.context().get(DefaultRegionService.class);
        CustomRegion region = new CustomRegion(Text.of("$1Demo"),
                Vector3N.of(1, 2, 3),
                Vector3N.of(4,5,6),
                UUID.randomUUID(),
                UUID.randomUUID());
        region.add(new IntegerFlag("int_flag", new Resource("Integer flag", "iflag")), 12);
        region.add(new BooleanFlag("bool_flag", new Resource("Boolean flag", "bflag")), true);
        regions.add(region);
    }
}
