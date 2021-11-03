package org.dockbox.hartshorn.i18n;

import org.dockbox.hartshorn.persistence.PersistentCapable;

import java.util.Locale;

public interface Language extends PersistentCapable<PersistentLanguageModel> {

    Locale locale();
    String code();
    String nameEnglish();
    String nameLocalized();

    @Override
    default Class<PersistentLanguageModel> type() {
        return PersistentLanguageModel.class;
    }

    @Override
    default PersistentLanguageModel model() {
        return new PersistentLanguageModel(this);
    }
}
