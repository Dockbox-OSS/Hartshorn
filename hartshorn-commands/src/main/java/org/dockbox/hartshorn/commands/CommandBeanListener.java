package org.dockbox.hartshorn.commands;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.beans.BeanContext;
import org.dockbox.hartshorn.beans.BeanObserver;
import org.dockbox.hartshorn.beans.BeanProvider;
import org.dockbox.hartshorn.commands.annotations.UseCommands;
import org.dockbox.hartshorn.commands.context.ArgumentConverterContext;
import org.dockbox.hartshorn.commands.definition.ArgumentConverter;
import org.dockbox.hartshorn.commands.extension.CommandExecutorExtension;
import org.dockbox.hartshorn.commands.extension.CommandExtensionContext;
import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.component.condition.RequiresActivator;
import org.dockbox.hartshorn.util.reflect.TypeContext;

import java.util.List;

@Service
@RequiresActivator(UseCommands.class)
public class CommandBeanListener implements BeanObserver {

    @Override
    public void onBeansCollected(final ApplicationContext applicationContext, final BeanContext beanContext) {
        final BeanProvider provider = beanContext.provider();

        final List<ArgumentConverter> argumentConverters = provider.all(ArgumentConverter.class);
        final ArgumentConverterContext converterContext = applicationContext.first(ArgumentConverterContext.class).get();
        argumentConverters.forEach(converterContext::register);

        final CommandExtensionContext extensionContext = applicationContext.first(CommandExtensionContext.class).get(); // This will fail, as bindings aren't available here yet..
        for (final CommandExecutorExtension extension : provider.all(CommandExecutorExtension.class)) {
            applicationContext.log().debug("Adding extension " + TypeContext.of(extension).name() + " to command gateway");
            extensionContext.add(extension);
        }
    }
}
