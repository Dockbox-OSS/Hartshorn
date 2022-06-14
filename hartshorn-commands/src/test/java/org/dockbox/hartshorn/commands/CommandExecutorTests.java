package org.dockbox.hartshorn.commands;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.commands.annotations.UseCommands;
import org.dockbox.hartshorn.commands.exceptions.ParsingException;
import org.dockbox.hartshorn.i18n.Message;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.testsuite.InjectTest;
import org.junit.jupiter.api.Assertions;

@HartshornTest
@UseCommands
@UseMethodCancelling
public class CommandExecutorTests {

    @InjectTest
    void testMethodCancelling(final ApplicationContext applicationContext) throws ParsingException {
        final CommandGateway gateway = applicationContext.get(CommandGateway.class);
        final JUnitSystemSubject subject = applicationContext.get(JUnitSystemSubject.class);
        Assertions.assertTrue(subject.received().isEmpty());

        gateway.accept(subject, "demo sub 1 --skip 1 2 3 4");
        Assertions.assertFalse(subject.received().isEmpty());

        final Message message = subject.received().get(0);
        Assertions.assertEquals("command.cancelled", message.key());
    }
}
