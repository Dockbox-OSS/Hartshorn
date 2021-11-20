package org.dockbox.hartshorn.web.mvc;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ClassPathViewTemplate implements ViewTemplate {
    private String location;
}
