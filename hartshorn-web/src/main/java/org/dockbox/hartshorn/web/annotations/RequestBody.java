package org.dockbox.hartshorn.web.annotations;

import org.dockbox.hartshorn.persistence.FileType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface RequestBody {
    FileType value() default FileType.JSON;
}
