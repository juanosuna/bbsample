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

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: Juan
 * Date: 2/10/11
 * Time: 9:18 PM
 */
public class SampleFieldFactory extends DefaultFieldFactory {
    private Map<Object, Field> fields = new HashMap<Object, Field>();

    @Override
    public Field createField(Item item, Object propertyId, Component uiContext) {

        if (!fields.containsKey(propertyId)) {
            Field field = createFieldImpl(item, propertyId, uiContext);
            if (field == null) {
                field = super.createField(item, propertyId, uiContext);
            }
            attachCaption(field, propertyId);
            attachValidator(field, propertyId);
            if (field instanceof AbstractTextField) {
                AbstractTextField textField = (AbstractTextField) field;
                textField.setNullRepresentation("");
                textField.setNullSettingAllowed(false);
                textField.setRequiredError("Value is required");
            }
            field.setInvalidAllowed(true);
            fields.put(propertyId, field);
        }

        return fields.get(propertyId);
    }

    public Set<Object> getPropertyIds() {
        return fields.keySet();
    }

    public Field getField(Object propertyId) {
        return fields.get(propertyId);
    }

    protected void attachCaption(Field field, Object propertyId) {
    }

    protected void attachValidator(Field field, Object propertyId) {
    }

    protected Field createFieldImpl(Item item, Object propertyId, Component uiContext) {
        return null;
    }

    public ComboBox createReferenceCombo(String caption, Class clazz, List list) {
        ComboBox comboBox = new ComboBox(caption);

        BeanItemContainer container = new BeanItemContainer(clazz);
        container.addAll(list);

        comboBox.setContainerDataSource(container);
        comboBox.setFilteringMode(ComboBox.FILTERINGMODE_OFF);
        comboBox.setItemCaptionMode(Select.ITEM_CAPTION_MODE_PROPERTY);
        comboBox.setNullSelectionAllowed(true);
        comboBox.setItemCaptionPropertyId("name");

        return comboBox;
    }

}
