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

package com.brownbag.sample.validation;

import org.apache.commons.beanutils.PropertyUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: Juan
 * Date: 2/13/11
 * Time: 3:12 PM
 */
public class PatternDependenciesValidator implements ConstraintValidator<PatternDependencies, Object> {
    @Override
    public void initialize(PatternDependencies constraintAnnotation) {
    }

    @Override
    public boolean isValid(Object bean, ConstraintValidatorContext context) {
        if (bean == null) return true;

        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            PatternIf patternIf = field.getAnnotation(PatternIf.class);
            if (patternIf != null) {
                String otherProperty = patternIf.otherProperty();
                String otherPropertyRegexp = patternIf.otherPropertyRegexp();
                if (match(bean, otherProperty, otherPropertyRegexp)) {
                    String thisProperty = field.getName();
                    String regexp = patternIf.regexp();
                    boolean isValid = match(bean, thisProperty, regexp);
                    if (!isValid) {
                        context.disableDefaultConstraintViolation();
                        context.buildConstraintViolationWithTemplate(patternIf.message()).addConstraintViolation();
                    }

                    return isValid;
                }
            }
        }

        return true;
    }

    private static boolean match(Object bean, String property, String regexp) {
        String propertyValue = null;
        try {
            propertyValue = convertToString(PropertyUtils.getProperty(bean, property));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(propertyValue);
        return matcher.find();
    }

    private static String convertToString(Object value) {
        if (value == null) {
            return "";
        } else {
            return value.toString();
        }
    }

    private static boolean isEmpty(Object object) {
        String value = convertToString(object);
        return value.length() == 0;
    }
}
