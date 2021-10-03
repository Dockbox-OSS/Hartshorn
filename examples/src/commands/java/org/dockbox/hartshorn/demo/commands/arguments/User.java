package org.dockbox.hartshorn.demo.commands.arguments;

import org.dockbox.hartshorn.commands.annotations.Parameter;
import org.dockbox.hartshorn.commands.context.CommandContext;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * A simple bean type which can be constructed through a {@link org.dockbox.hartshorn.commands.arguments.CustomParameterPattern}.
 *
 * @see org.dockbox.hartshorn.commands.arguments.HashtagParameterPattern
 * @see org.dockbox.hartshorn.demo.commands.services.CommandService#build(CommandContext)
 */
@Parameter("user")
@AllArgsConstructor
@Getter
public class User {
    private String name;
    private int age;
}
