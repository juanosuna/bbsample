/*
 * BROWN BAG CONFIDENTIAL
 *
 * Brown Bag Consulting LLC
 * Copyright (c) 2011. All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Brown Bag Consulting LLC and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Brown Bag Consulting LLC
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Brown Bag Consulting LLC.
 */

package com.brownbag.sample.util;

import com.vaadin.addon.beanvalidation.BeanValidationValidator;
import com.vaadin.ui.Field;
import org.springframework.beans.BeanUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * User: Juan
 * Date: 2/13/11
 * Time: 2:19 AM
 */
public class BeanValidationUtil {
    public static Set<BeanValidationValidator> addValidators(SampleFieldFactory sampleFieldFactory, Class clazz) {
        Set<BeanValidationValidator> validators = new HashSet<BeanValidationValidator>();
        Set<Object> propertyIds = sampleFieldFactory.getPropertyIds();
        for (Object propertyId : propertyIds) {
            Field field = sampleFieldFactory.getField(propertyId);
            BeanValidationValidator validator = addValidator(field, propertyId, clazz);
            validators.add(validator);
        }

        return validators;
    }

    public static BeanValidationValidator addValidator(Field field, Object propertyId, Class clazz) {
        BeanProperty beanProperty = getType(clazz, propertyId.toString());

        return BeanValidationValidator.addValidator(field, beanProperty.getId(), beanProperty.getType());
    }

    private static BeanProperty getType(Class clazz, String propertyPath) {
        String[] properties = propertyPath.split("\\.");
        Class currentPropertyType = clazz;
        String propertyId = propertyPath;
        for (int i = 0; i < properties.length - 1; i++) {
            String property = properties[i];
            Class propertyType = BeanUtils.findPropertyType(property, new Class[]{currentPropertyType});
            if (propertyType == null || propertyType.equals(Object.class)) {
                throw new RuntimeException("Invalid otherProperty type " + propertyType
                        + " for otherProperty " + property);
            } else {
                currentPropertyType = propertyType;
                propertyId = properties[i + 1];
            }
        }

        return new BeanProperty(propertyId, currentPropertyType);
    }

    private static class BeanProperty {
        private String id;
        private Class type;

        private BeanProperty(String id, Class type) {
            this.id = id;
            this.type = type;
        }

        public String getId() {
            return id;
        }

        public Class getType() {
            return type;
        }
    }
}
