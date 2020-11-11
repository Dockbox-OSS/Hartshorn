/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

/*
 * This file is part of Neutrino, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package org.dockbox.selene.core.impl.util.files.mapping;

import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;

import org.dockbox.selene.core.impl.util.files.annotations.Default;
import org.dockbox.selene.core.impl.util.files.annotations.DoNotGenerate;
import org.dockbox.selene.core.impl.util.files.annotations.ProcessSetting;
import org.dockbox.selene.core.impl.util.files.annotations.RequiresProperty;
import org.dockbox.selene.core.impl.util.files.process.SettingProcessor;
import org.dockbox.selene.core.impl.util.files.process.SettingProcessorCache;
import org.dockbox.selene.core.impl.util.files.util.ClassConstructor;
import org.dockbox.selene.core.server.Selene;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.SimpleConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMapper;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.Setting;

public class NeutrinoObjectMapper<T> extends ObjectMapper<T> {

    private final Function<Setting, String> commentProcessor;
    private final ClassConstructor<SettingProcessor> classConstructor;
    private Map<String, FieldData> fieldDataMapCache;
    private List<Field> fieldsToProcess;

    /**
     * Create a new object mapper of a given type
     *
     * @param clazz The type this object mapper will work with
     * @param commentProcessor The comment processor to use
     * @param constructor The constructor to use when constructing types
     * @throws ObjectMappingException if the provided class is in someway invalid
     */
    public NeutrinoObjectMapper(Class<T> clazz,
            Function<Setting, String> commentProcessor,
            ClassConstructor<SettingProcessor> constructor) throws ObjectMappingException {
        super(clazz);
        this.commentProcessor = commentProcessor;
        this.classConstructor = constructor;
        this.collectFields();
    }

    // Come back and do our processing later.
    protected void collectFields(Map<String, FieldData> cachedFields, Class<? super T> clazz) throws ObjectMappingException {
        if (this.fieldDataMapCache == null) {
            this.fieldDataMapCache = cachedFields;
            this.fieldsToProcess = Lists.newArrayList();
        }

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Setting.class)) {
                this.fieldsToProcess.add(field);
            }
        }
    }

    protected void collectFields() throws ObjectMappingException {
        for (Field field : this.fieldsToProcess) {
            Setting setting = field.getAnnotation(Setting.class);
            String path = setting.value();
            if (path.isEmpty()) {
                path = field.getName();
            }

            String comment = this.commentProcessor.apply(setting);

            FieldData data;
            if (field.isAnnotationPresent(ProcessSetting.class)) {
                try {
                    data = new PreprocessedFieldData(field, comment, this.classConstructor);
                } catch (IllegalArgumentException e) {
                    data = new FieldData(field, comment);
                }
            } else if (field.isAnnotationPresent(DoNotGenerate.class)) {
                Object defaultValue = null;
                try {
                    field.setAccessible(true);
                    defaultValue = field.get(field.getDeclaringClass().newInstance());
                } catch (IllegalAccessException | InstantiationException e) {
                    Selene.getServer().except(e.getMessage(), e);
                }

                data = new DoNotGenerateFieldData(field, comment, defaultValue);
            } else {
                data = new FieldData(field, comment);
            }

            if (field.isAnnotationPresent(Default.class)) {
                Default de = field.getAnnotation(Default.class);
                data = new DefaultFieldData(field, comment, data, de.value(), de.saveDefaultIfNull(), de.useDefaultIfEmpty(), this.canEdit(field));
            } else if (!this.canEdit(field)) {
                data = new JavaPropertyFieldData(field, comment);
            }

            field.setAccessible(true);
            if (!this.fieldDataMapCache.containsKey(path)) {
                this.fieldDataMapCache.put(path, data);
            }
        }
    }

    private boolean canEdit(Field field) {
        if (!field.isAnnotationPresent(RequiresProperty.class)) {
            return true;
        }

        try {
            RequiresProperty annotation = field.getAnnotation(RequiresProperty.class);
            @Nullable String propertyValue = System.getProperty(annotation.value());
            return null != propertyValue && Pattern.compile(annotation.matchedName()).matcher(propertyValue).matches();
        } catch (Exception e) {
            Selene.log().warn("Field checks for property but the value is invalid. Not loading.");
            return false;
        }
    }

    protected static class DefaultFieldData extends FieldData {

        private final boolean useIfNullWhenSaving;
        private final String defaultValue;
        private final FieldData fieldData;
        private final TypeToken<?> typeToken;
        private final Field field;
        private final boolean useIfEmpty;
        private final boolean set;
        private final String comment;

        protected DefaultFieldData(Field field, String comment, FieldData data, String defaultValue, boolean useIfNullWhenSaving, boolean useIfEmpty, boolean set)
                throws ObjectMappingException {
            super(field, comment);
            this.comment = comment;
            this.field = field;
            this.typeToken = TypeToken.of(field.getGenericType());
            this.defaultValue = defaultValue;
            this.fieldData = data;
            this.useIfNullWhenSaving = useIfNullWhenSaving;
            this.useIfEmpty = useIfEmpty;
            this.set = set;
        }

        @Override public void deserializeFrom(Object instance, ConfigurationNode node) throws ObjectMappingException {
            if (!this.set) {
                try {
                    this.setDefaultOnField(instance, node);
                } catch (IllegalAccessException e) {
                    Selene.getServer().except(e.getMessage(), e);
                }

                return;
            }

            try {
                this.fieldData.deserializeFrom(instance, node);
            } catch (Exception e) {
                Selene.getServer().except(e.getMessage(), e);
            }

            try {
                //noinspection OverlyComplexBooleanExpression
                if (node.isVirtual() || null == node.getValue() || (this.useIfEmpty && node.getString().isEmpty())) {
                    this.setDefaultOnField(instance, node);
                }
            } catch (IllegalAccessException e) {
                Selene.getServer().except(e.getMessage(), e);
            }
        }

        private void setDefaultOnField(Object instance, ConfigurationNode node) throws ObjectMappingException, IllegalAccessException {
            this.field.setAccessible(true);
            this.field.set(instance, node.getOptions().getSerializers().get(this.typeToken)
                    .deserialize(this.typeToken, SimpleConfigurationNode.root(node.getOptions()).setValue(this.defaultValue)));
        }

        @Override public void serializeTo(Object instance, ConfigurationNode node) throws ObjectMappingException {
            @org.jetbrains.annotations.Nullable Object i;
            try {
                i = this.field.get(instance);
            } catch (IllegalAccessException e) {
                i = null;
            }

            if (this.set) {
                if (this.useIfNullWhenSaving && null == i) {
                    node.setValue(this.defaultValue);
                    if (null != this.comment && !this.comment.isEmpty() && node instanceof CommentedConfigurationNode) {
                        ((CommentedConfigurationNode) node).setComment(this.comment);
                    }
                } else {
                    this.fieldData.serializeTo(instance, node);
                }
            }
        }
    }

    protected static class DoNotGenerateFieldData extends FieldData {

        private final Object defaultValue;
        private final Field field;

        protected DoNotGenerateFieldData(Field field, String comment, Object defaultValue) throws ObjectMappingException {
            super(field, comment);
            this.field = field;
            this.defaultValue = defaultValue;
        }

        @Override
        public void serializeTo(Object instance, ConfigurationNode node) throws ObjectMappingException {
            try {
                this.field.setAccessible(true);
                if (!this.defaultValue.equals(this.field.get(instance))) {
                    super.serializeTo(instance, node);
                }
            } catch (IllegalAccessException e) {
                super.serializeTo(instance, node);
            }
        }
    }

    protected static class JavaPropertyFieldData extends FieldData {

        private static final String COMMENT = "This config option is currently ignored.";

        public JavaPropertyFieldData(Field field, String comment) throws ObjectMappingException {
            super(field, comment);
        }

        @Override
        public void deserializeFrom(Object instance, ConfigurationNode node) throws ObjectMappingException {
            // Don't set the field
            // super.deserializeFrom(instance, node);
        }

        @Override
        public void serializeTo(Object instance, ConfigurationNode node) throws ObjectMappingException {
            if (!node.isVirtual() && node instanceof CommentedConfigurationNode) {
                CommentedConfigurationNode ccn = (CommentedConfigurationNode) node;
                String comment = ccn.getComment().orElse("");
                if (!comment.endsWith(COMMENT)) {
                    ccn.setComment(ccn.getComment() + System.lineSeparator() + COMMENT);
                }
            }
            // super.serializeTo(instance, node);
        }
    }

    protected static class PreprocessedFieldData extends FieldData {

        private final Collection<SettingProcessor> processors = new ArrayList<>();

        protected PreprocessedFieldData(Field field, String comment, ClassConstructor<SettingProcessor> processorClassConstructor)
                throws ObjectMappingException, IllegalArgumentException {
            super(field, comment);
            try {
                for (Class<? extends SettingProcessor> pro : field.getAnnotation(ProcessSetting.class).value()) {
                    this.processors.add(SettingProcessorCache.getOrAdd(pro, processorClassConstructor));
                }
            } catch (Throwable e) {
                Selene.getServer().except("No setting processor", e);
                throw new IllegalArgumentException("No setting processor", e);
            }
        }

        @Override
        public void deserializeFrom(Object instance, ConfigurationNode node) throws ObjectMappingException {
            for (SettingProcessor processor : this.processors) {
                processor.onGet(node);
            }

            super.deserializeFrom(instance, node);
        }

        @Override
        public void serializeTo(Object instance, ConfigurationNode node) throws ObjectMappingException {
            super.serializeTo(instance, node);

            for (SettingProcessor processor : this.processors) {
                processor.onSet(node);
            }
        }
    }
}
