package org.dockbox.hartshorn.i18n;

import org.dockbox.hartshorn.core.HartshornUtils;
import org.dockbox.hartshorn.core.annotations.inject.Binds;
import org.dockbox.hartshorn.core.domain.Exceptional;

import java.util.Map;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Binds(TranslationBundle.class)
public class DefaultTranslationBundle implements TranslationBundle {

    @Getter @Setter
    private Language primaryLanguage = Languages.EN_US;
    private final Map<String, Message> messages = HartshornUtils.emptyConcurrentMap();

    @Override
    public Set<Message> messages() {
        return HartshornUtils.asUnmodifiableSet(this.messages.values());
    }

    @Override
    public Exceptional<Message> message(final String key) {
        return this.message(key, this.primaryLanguage());
    }

    @Override
    public Exceptional<Message> message(final String key, final Language language) {
        return Exceptional.of(this.messages.get(key))
                .map(message -> message.translate(language).detach());
    }

    @Override
    public Message register(final String key, final String value, final Language language) {
        return this.register(new MessageTemplate(value, key, language));
    }

    @Override
    public Message register(final Message message) {
        this.messages.put(message.key(), this.mergeMessages(message, this.messages));
        return message;
    }

    @Override
    public Message register(final String key, final String value) {
        return this.register(key, value, this.primaryLanguage());
    }

    protected void add(final Message message) {
        message.translate(this.primaryLanguage());
    }

    @Override
    public TranslationBundle merge(final TranslationBundle bundle) {
        final DefaultTranslationBundle translationBundle = new DefaultTranslationBundle().primaryLanguage(this.primaryLanguage());
        final Map<String, Message> messageDict = translationBundle.messages;
        for (final Message message : this.messages()) this.mergeMessages(message, messageDict);
        for (final Message message : bundle.messages()) this.mergeMessages(message, messageDict);
        return translationBundle;
    }

    private Message mergeMessages(final Message message, final Map<String, Message> messageDict) {
        if (messageDict.containsKey(message.key())) {
            return messageDict.get(message.key()).merge(this.primaryLanguage(), message);
        }
        else return message;
    }
}
