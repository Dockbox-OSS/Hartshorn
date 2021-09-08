package org.dockbox.hartshorn.boot;

import org.dockbox.hartshorn.commands.SystemSubject;
import org.dockbox.hartshorn.di.annotations.inject.Binds;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.i18n.text.Text;
import org.dockbox.hartshorn.i18n.text.pagination.Pagination;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import lombok.Getter;

@Binds(SystemSubject.class)
public class ApplicationSystemSubject extends SystemSubject {

    @Inject
    @Getter
    private ApplicationContext applicationContext;

    @Override
    public void send(final Text text) {
        this.applicationContext().log().info("-> %s".formatted(text.toPlain()));
    }

    @Override
    public void sendWithPrefix(final Text text) {
        this.send(text);
    }

    @Override
    public void send(final Pagination pagination) {
        final List<Text> message = HartshornUtils.asList(pagination.title());
        message.add(pagination.header());
        message.addAll(pagination.content());
        message.add(pagination.footer());
        final String out = message.stream().map(Text::toPlain).collect(Collectors.joining("\n-> "));
        this.applicationContext().log().info("-> %s".formatted(out));
    }
}
