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

package com.brownbag.sample.ui.view;

import com.brownbag.sample.domain.dao.CountryDao;
import com.brownbag.sample.domain.dao.PersonDao;
import com.brownbag.sample.domain.dao.StateDao;
import com.brownbag.sample.domain.entity.Address;
import com.brownbag.sample.domain.entity.Country;
import com.brownbag.sample.domain.entity.Person;
import com.brownbag.sample.domain.entity.State;
import com.brownbag.sample.util.BeanValidationUtil;
import com.brownbag.sample.util.SampleFieldFactory;
import com.brownbag.sample.validation.Validation;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.POJOItem;
import com.vaadin.terminal.CompositeErrorMessage;
import com.vaadin.terminal.ErrorMessage;
import com.vaadin.terminal.UserError;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.validation.ConstraintViolation;
import java.util.*;

/**
 * User: Juan
 * Date: 2/8/11
 * Time: 7:52 PM
 */
@org.springframework.stereotype.Component
@Scope("session")
public class PersonForm extends Form {
    public static final LinkedHashMap<String, String> FIELDS = new LinkedHashMap<String, String>() {
        {
            put("firstName", "First Name");
            put("lastName", "Last Name");
            put("socialSecurityNumber", "Social Security Number");
            put("birthDate", "Birth Date");
            put("address.street", "Street");
            put("address.city", "City");
            put("address.zipCode", "Zip Code");
            put("address.state", "State");
            put("address.country", "Country");
        }
    };

    public static final List PROPERTIES = Arrays.asList(FIELDS.keySet().toArray());

    @Autowired
    private PersonDao personDao;

    @Autowired
    private CountryDao countryDao;

    @Autowired
    private StateDao stateDao;

    @Autowired
    private PersonTable personTable;

    @Autowired
    private Validation<Person> validation;

    private GridLayout gridLayout;
    private ComboBox stateCombo;

    public PersonForm() {
        super();
        setVisible(false);
        setCaption("Personal Details");

        gridLayout = new GridLayout(3, 5);
        gridLayout.setMargin(true, false, false, true);
        gridLayout.setSpacing(true);
        setLayout(gridLayout);

        setWriteThrough(true);
        setInvalidCommitted(true);
        setImmediate(true);
        setValidationVisible(true);

        setFormFieldFactory(new PersonFieldFactory());
        initButtons();
    }

    public void load(Person person) {
        setComponentError(null);
        Person loadedPerson = personDao.find(person.getId());
        POJOItem<Person> personItem = new POJOItem<Person>(loadedPerson, PROPERTIES);
        super.setItemDataSource(personItem, PROPERTIES);
    }

    public void clear() {
        setVisible(false);
        setComponentError(null);
        super.setItemDataSource(null, PROPERTIES);

    }

    public void create() {
        setComponentError(null);
        Person newPerson = new Person();
        Address newAddress = new Address();
        newPerson.setAddress(newAddress);
        POJOItem<Person> personItem = new POJOItem<Person>(newPerson, PROPERTIES);
        super.setItemDataSource(personItem, PROPERTIES);
    }

    public void save() {
        try {
            commit();
            // hack, not sure why this is happening , since invalid data is allowed
        } catch (com.vaadin.data.Validator.InvalidValueException e) {
        }
        POJOItem<Person> personItem = (POJOItem<Person>) getItemDataSource();
        Person person = personItem.getBean();

        Set<ConstraintViolation<Person>> constraintViolations = validate(person);
        if (isValid() && constraintViolations.isEmpty()) {
            if (person.getId() != null) {
                person.updateLastModified();
                Person mergedPerson = personDao.merge(person);
                load(mergedPerson);
                personTable.search();
            } else {
                personDao.persist(person);
                load(person);
                personTable.search();
            }
        }
     }

    private Set<ConstraintViolation<Person>> validate(Person person) {
        Set<ConstraintViolation<Person>> constraintViolations = validation.validate(person);
        if (!constraintViolations.isEmpty()) {
            List<ErrorMessage> errorMessages = new ArrayList<ErrorMessage>();
            for (ConstraintViolation<Person> constraintViolation : constraintViolations) {
                String message = constraintViolation.getMessage();
                errorMessages.add(new UserError(message));
            }
            CompositeErrorMessage compositeErrorMessage = new CompositeErrorMessage(errorMessages);
            setComponentError(compositeErrorMessage);
        } else {
            setComponentError(null);
        }

        return constraintViolations;
    }

    public void reload() {
        setComponentError(null);
        discard();
        POJOItem<Person> personItem = (POJOItem<Person>) getItemDataSource();
        Person person = personItem.getBean();
        if (person.getId() == null) {
            clear();
        } else {
            load(person);
        }
    }

    private void initButtons() {
        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setSpacing(true);

        Button discardChanges = new Button("Discard", this, "reload");
        buttons.addComponent(discardChanges);

        Button save = new Button("Save", this, "save");
        buttons.addComponent(save);

        getFooter().setMargin(true);
        getFooter().addComponent(buttons);
    }

    @Override
    protected void attachField(Object propertyId, Field field) {
        if (propertyId.equals("firstName")) {
            gridLayout.addComponent(field, 0, 0);
        } else if (propertyId.equals("lastName")) {
            gridLayout.addComponent(field, 1, 0);
        } else if (propertyId.equals("birthDate")) {
            gridLayout.addComponent(field, 0, 1);
        } else if (propertyId.equals("socialSecurityNumber")) {
            gridLayout.addComponent(field, 1, 1);
        } else if (propertyId.equals("address.street")) {
            gridLayout.addComponent(field, 0, 2);
        } else if (propertyId.equals("address.city")) {
            gridLayout.addComponent(field, 1, 2);
        } else if (propertyId.equals("address.state")) {
            gridLayout.addComponent(field, 0, 3);
        } else if (propertyId.equals("address.zipCode")) {
            gridLayout.addComponent(field, 1, 3);
        } else if (propertyId.equals("address.country")) {
            gridLayout.addComponent(field, 0, 4);
        }
    }

    public class PersonFieldFactory extends SampleFieldFactory {

        @Override
        protected Field createFieldImpl(Item item, Object propertyId, Component uiContext) {
            if ("address.country".equals(propertyId)) {
                ComboBox countryCombo = createReferenceCombo("Country", Country.class, countryDao.findAll());
                countryCombo.addListener(ValueChangeEvent.class, this, "countryChanged");
                return countryCombo;
            } else if ("address.state".equals(propertyId)) {
                stateCombo = new SampleFieldFactory().createReferenceCombo("State", State.class, new ArrayList());
                return stateCombo;
            } else {
                return null;
            }
        }

        public void countryChanged(ValueChangeEvent event) {
            Country newCountry = (Country) event.getProperty().getValue();
            State selectedState = (State) stateCombo.getValue();
            BeanItemContainer<State> stateContainer = (BeanItemContainer<State>) stateCombo.getContainerDataSource();
            stateContainer.removeAllItems();
            List<State> states = stateDao.findByCountry(newCountry);
            for (State state : states) {
                stateContainer.addBean(state);
            }
            if (newCountry != null && selectedState != null && !newCountry.equals(selectedState.getCountry())) {
                stateCombo.select(stateCombo.getNullSelectionItemId());
            }
        }

        @Override
        protected void attachCaption(Field field, Object propertyId) {
            if (FIELDS.containsKey(propertyId)) {
                field.setCaption(FIELDS.get(propertyId));
            }
        }

        @Override
        protected void attachValidator(Field field, Object propertyId) {
            BeanValidationUtil.addValidator(field, propertyId, Person.class);
        }
    }
}
