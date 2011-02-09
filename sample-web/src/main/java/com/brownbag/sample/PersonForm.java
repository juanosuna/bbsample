package com.brownbag.sample;

import com.brownbag.sample.domain.dao.CountryDao;
import com.brownbag.sample.domain.entity.Country;
import com.brownbag.sample.domain.entity.Person;
import com.brownbag.sample.util.SpringApplicationContext;
import com.vaadin.data.Item;
import com.vaadin.data.util.AbstractBeanContainer;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.BaseTheme;

import java.util.Arrays;
import java.util.List;

/**
 * User: Juan
 * Date: 2/8/11
 * Time: 7:52 PM
 */
public class PersonForm extends Form {

    private GridLayout gridLayout;
    private PersonFieldFactory personFieldFactory;

    public PersonForm(BeanItem<Person> personItem) {
        super();
        setCaption("Personal Details");

        gridLayout = new GridLayout(3, 3);

        gridLayout.setMargin(true, false, false, true);
        gridLayout.setSpacing(true);

        setLayout(gridLayout);

        setWriteThrough(false); // we want explicit 'apply'
        setInvalidCommitted(false); // no invalid values in datamodel

        // FieldFactory for customizing the fields and adding validators
        personFieldFactory = new PersonFieldFactory();
        setFormFieldFactory(personFieldFactory);
        setItemDataSource(personItem); // bind to POJO via BeanItem

        // Determines which properties are shown, and in which order:
        setVisibleItemProperties(Arrays.asList(new String[]{"firstName",
                "lastName", "city", "country", "socialSecurityNumber", "birthDate"}));

        initButtons();
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
            gridLayout.addComponent(field, 1, 0, 2, 0);
        } else if (propertyId.equals("city")) {
            gridLayout.addComponent(field, 0, 1, 2, 1);
        } else if (propertyId.equals("country")) {
            gridLayout.addComponent(field, 0, 2);
        } else if (propertyId.equals("socialSecurityNumber")) {
            gridLayout.addComponent(field, 1, 2);
        } else if (propertyId.equals("birthDate")) {
            gridLayout.addComponent(field, 2, 2);
        }
    }

    private class PersonFieldFactory extends DefaultFieldFactory {

        final ComboBox countries = new ComboBox("Country");

        public PersonFieldFactory() {
            BeanContainer<String, Country> countryContainer = new BeanContainer<String, Country>(Country.class);
            AbstractBeanContainer.BeanIdResolver<String, Country> countryIdResolver = new AbstractBeanContainer.BeanIdResolver<String, Country>() {
                @Override
                public String getIdForBean(Country bean) {
                    return bean.getId();
                }
            };
            countryContainer.setBeanIdResolver(countryIdResolver);

            CountryDao countryDao = (CountryDao) SpringApplicationContext.getBean("countryDao");
            List<Country> countryList = countryDao.findAll();
            countryContainer.addAll(countryList);

            countries.setContainerDataSource(countryContainer);
            countries.setFilteringMode(ComboBox.FILTERINGMODE_STARTSWITH);
            countries.setNullSelectionAllowed(false);
            countries.setItemCaptionMode(Select.ITEM_CAPTION_MODE_PROPERTY);
            countries.setItemCaptionPropertyId("name");
        }

        @Override
        public Field createField(Item item, Object propertyId,
                                 Component uiContext) {
            Field field;
            if ("country".equals(propertyId)) {
                return countries;
            } else {
                // Use the super class to create a suitable field base on the
                // property type.
                field = super.createField(item, propertyId, uiContext);
            }

            return field;
        }
    }
}
