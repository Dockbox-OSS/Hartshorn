package org.dockbox.hartshorn.web.mvc;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class StringViewTemplate implements ViewTemplate {
    private final String template;
}
