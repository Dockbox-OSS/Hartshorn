/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.web.mvc.freemarker;

import org.dockbox.hartshorn.core.HartshornUtils;
import org.dockbox.hartshorn.core.annotations.inject.ComponentBinding;
import org.dockbox.hartshorn.core.boot.Hartshorn;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.exceptions.ApplicationException;
import org.dockbox.hartshorn.web.mvc.ClassPathViewTemplate;
import org.dockbox.hartshorn.web.mvc.FileViewTemplate;
import org.dockbox.hartshorn.web.mvc.MVCInitializer;
import org.dockbox.hartshorn.web.mvc.StringViewTemplate;
import org.dockbox.hartshorn.web.mvc.ViewModel;
import org.dockbox.hartshorn.web.mvc.ViewTemplate;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateModelException;

@ComponentBinding(value = MVCInitializer.class, singleton = true)
public class FreeMarkerMVCInitializer implements MVCInitializer {

    private Configuration configuration;

    @Override
    public final void initialize(final ApplicationContext applicationContext) throws ApplicationException {
        if (this.configuration == null) {
            this.configuration = this.configuration(applicationContext);
        }
        else {
            throw new ApplicationException("FreeMarker configuration was already initialized!");
        }
    }

    @Override
    public String transform(final ViewTemplate template, final ViewModel model) throws ApplicationException {
        final String name = TypeContext.of(template).name() + System.nanoTime();
        final Template freeMarkerTemplate = this.transformTemplate(name, template);
        try {
            final Writer writer = new StringWriter();
            freeMarkerTemplate.process(model.attributes(), writer);
            return writer.toString();
        }
        catch (final TemplateException | IOException e) {
            throw new ApplicationException(e);
        }
    }

    @Override
    public Set<ViewTemplate> templates() {
        return null;
    }

    protected Template transformTemplate(final String name, final ViewTemplate template) throws ApplicationException {
        try {
            if (template instanceof StringViewTemplate stringView) {
                return new Template(name, stringView.template(), this.configuration);
            }
            else if (template instanceof FileViewTemplate fileView) {
                final String content = HartshornUtils.contentOrEmpty(fileView.path());
                return new Template(name, content, this.configuration);
            }
            else if (template instanceof ClassPathViewTemplate classPathView) {
                return this.configuration.getTemplate(classPathView.location());
            }
            else {
                throw new ApplicationException("Unsupported view template type: " + template.getClass().getName());
            }
        } catch (final IOException e) {
            throw new ApplicationException(e);
        }
    }

    protected Configuration configuration(final ApplicationContext applicationContext) throws ApplicationException {
        try {
            final Configuration configuration = new Configuration(Configuration.VERSION_2_3_31);
            configuration.setClassForTemplateLoading(Hartshorn.class, this.webRoot());
            configuration.setDefaultEncoding(StandardCharsets.UTF_8.name());
            configuration.setTemplateExceptionHandler(this.exceptionHandler(applicationContext));
            configuration.setLogTemplateExceptions(this.logExceptions());
            configuration.setWrapUncheckedExceptions(false);
            configuration.setFallbackOnNullLoopVariable(false);
            configuration.setSharedVariable("applicationContext", applicationContext);

            return configuration;
        }
        catch (final TemplateModelException e) {
            throw new ApplicationException(e);
        }
    }

    protected String webRoot() {
        return MVCInitializer.TEMPLATE_ROOT;
    }

    protected boolean logExceptions() {
        return true;
    }

    protected TemplateExceptionHandler exceptionHandler(final ApplicationContext applicationContext) {
        if (applicationContext.environment().isCI()) return TemplateExceptionHandler.RETHROW_HANDLER;
        else return TemplateExceptionHandler.HTML_DEBUG_HANDLER;
    }
}
