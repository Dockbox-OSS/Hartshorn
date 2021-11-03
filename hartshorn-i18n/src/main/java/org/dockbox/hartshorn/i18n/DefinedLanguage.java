package org.dockbox.hartshorn.i18n;

import java.util.Locale;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DefinedLanguage implements Language {
    private Locale locale;
    private String code;
    private String nameEnglish;
    private String nameLocalized;
}
