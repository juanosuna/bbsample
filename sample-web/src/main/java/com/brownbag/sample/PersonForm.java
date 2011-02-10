package com.brownbag.sample;

import com.brownbag.sample.domain.dao.CountryDao;
import com.brownbag.sample.domain.entity.Country;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.BaseTheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Juan
 * Date: 2/8/11
 * Time: 7:52 PM
 */
@org.springframework.stereotype.Component
@Scope("session")
public class PersonForm extends Form {

    @Autowired
    private CountryDao countryDao;

    private GridLayout gridLayout;

    public PersonForm() {
        super();
        setVisible(false);
        setCaption("Personal Details");

        gridLayout = new GridLayout(2, 3);
        gridLayout.setMargin(true, false, false, true);
        gridLayout.setSpacing(true);
        setLayout(gridLayout);

        setWriteThrough(false); // we want explicit 'apply'
        setInvalidCommitted(false); // no invalid values in data model

        setFormFieldFactory(new PersonFieldFactory());

        initButtons();
    }

    @Override
    public void setItemDataSource(Item newDataSource) {
        super.setItemDataSource(newDataSource,
                Arrays.asList("firstName", "lastName", "city", "country", "socialSecurityNumber", "birthDate")
        );
    }

    private void initButtons() {
        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setSpacing(true);

        Button discardChanges = new Button("Discard Changes",
                new Button.ClickListener() {
                    public void buttonClick(Button.ClickEvent event) {
                        discard();
                    }
                });
        discardChanges.setStyleName(BaseTheme.BUTTON_LINK);
        buttons.addComponent(discardChanges);
        buttons.setComponentAlignment(discardChanges, Alignment.MIDDLE_LEFT);

        Button apply = new Button("Apply", new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                try {
                    commit();
                } catch (Exception e) {
                    // Ingnored, we'll let the Form handle the errors
                }
            }
        });
        buttons.addComponent(apply);

        getFooter().setMargin(true);
        getFooter().addComponent(buttons);
    }

    @Override
    protected void attachField(Object propertyId, Field field) {
        if (propertyId.equals("firstName")) {
            gridLayout.addComponent(field, 0, 0);
        } else if (propertyId.equals("lastName")) {
            gridLayout.addComponent(field, 1, 0);
        } else if (propertyId.equals("city")) {
            gridLayout.addComponent(field, 0, 1);
        } else if (propertyId.equals("country")) {
            gridLayout.addComponent(field, 1, 1);
        } else if (propertyId.equals("socialSecurityNumber")) {
            gridLayout.addComponent(field, 0, 2);
        } else if (propertyId.equals("birthDate")) {
            gridLayout.addComponent(field, 1, 2);
        }
    }

    private class PersonFieldFactory extends DefaultFieldFactory {

        private Map<Object, Field> fields = new HashMap<Object, Field>();

        @Override
        public Field createField(Item item, Object propertyId, Component uiContext) {

            if (!fields.containsKey(propertyId)) {
                Field field = createFieldImpl(item, propertyId, uiContext);
                fields.put(propertyId, field);
            }

            return fields.get(propertyId);
        }

        private Field createFieldImpl(Item item, Object propertyId,
                                      Component uiContext) {
            Field field;
            if ("country".equals(propertyId)) {
                return createCountriesCombo();
            } else {
                field = super.createField(item, propertyId, uiContext);
            }

            return field;
        }

        private ComboBox createCountriesCombo() {
            ComboBox countries = new ComboBox("Country");

            BeanItemContainer<Country> countryContainer = new BeanItemContainer<Country>(Country.class);
            List<Country> countryList = countryDao.findAll();
            countryContainer.addAll(countryList);

            countries.setContainerDataSource(countryContainer);
            countries.setFilteringMode(ComboBox.FILTERINGMODE_STARTSWITH);
            countries.setNullSelectionAllowed(false);
            countries.setItemCaptionMode(Select.ITEM_CAPTION_MODE_PROPERTY);
            countries.setItemCaptionPropertyId("name");

            return countries;
        }
    }
}
