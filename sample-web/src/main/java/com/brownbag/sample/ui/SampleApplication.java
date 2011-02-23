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

package com.brownbag.sample.ui;

import com.brownbag.sample.domain.dao.CountryDao;
import com.brownbag.sample.domain.dao.PersonDao;
import com.brownbag.sample.domain.dao.StateDao;
import com.brownbag.sample.domain.entity.Country;
import com.brownbag.sample.domain.entity.Person;
import com.brownbag.sample.domain.entity.State;
import com.brownbag.sample.domain.query.PersonQuery;
import com.brownbag.sample.ui.view.PersonForm;
import com.brownbag.sample.ui.view.PersonTable;
import com.brownbag.sample.util.SampleFieldFactory;
import com.vaadin.Application;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.data.util.POJOItem;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Runo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;

import java.util.ArrayList;
import java.util.List;

public class SampleApplication extends Application {
    private static final long serialVersionUID = 1L;

    @Autowired
    private PersonQuery personQuery;

    @Autowired
    private PersonTable personTable;

    @Autowired
    private PersonForm personForm;

    @Autowired
    private PersonDao personDao;

    @Autowired
    private CountryDao countryDao;

    @Autowired
    private StateDao stateDao;

    private ComboBox stateCombo;

    private Window mainWindow;
    private Panel queryPanel;
    private Panel buttonPanel;
    private Panel tableDetailPanel;
    private Panel tablePanel;
    private Panel detailPanel;

    @Override
    public void init() {

        setTheme("mytheme");

        initMainWindow();
        initPanels();
        initButtonsAndFields();

        tablePanel.addComponent(personTable);
        detailPanel.addComponent(personForm);

        personTable.addListener(Property.ValueChangeEvent.class, this, "rowSelected");

        personTable.search();
    }

    private void initMainWindow() {
        mainWindow = new Window("Sample Application");

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setMargin(true);
        mainLayout.setSpacing(true);
        mainWindow.setContent(mainLayout);

        setMainWindow(mainWindow);
    }

    private void initPanels() {

        queryPanel = createPanel(new HorizontalLayout());
        mainWindow.addComponent(queryPanel);

        buttonPanel = createPanel(new HorizontalLayout());
        mainWindow.addComponent(buttonPanel);

        tableDetailPanel = createPanel(new HorizontalLayout());
        mainWindow.addComponent(tableDetailPanel);

        tablePanel = createPanel(new VerticalLayout());
        tableDetailPanel.addComponent(tablePanel);

        detailPanel = createPanel(new VerticalLayout());
        tableDetailPanel.addComponent(detailPanel);
    }

    private static Panel createPanel(AbstractOrderedLayout layout) {
        Panel panel = new Panel();
        panel.addStyleName(Runo.PANEL_LIGHT);
        layout.setMargin(false);
        layout.setSpacing(true);
        panel.setContent(layout);

        return panel;
    }

    private void initButtonsAndFields() {
        GridLayout gridLayout = new GridLayout(2, 2);
        queryPanel.setContent(gridLayout);

        MethodProperty lastNameProperty = new MethodProperty(personQuery, "lastName");
        TextField lastNameField = new TextField("Last Name", lastNameProperty);
        lastNameField.setNullSettingAllowed(true);
        lastNameField.setNullRepresentation("");
        gridLayout.addComponent(lastNameField);

        MethodProperty pageProperty = new MethodProperty(personQuery, "pageSize");
        TextField pageSizeField = new TextField("Page Size", pageProperty);
        pageSizeField.setNullRepresentation("0");
        gridLayout.addComponent(pageSizeField);

        stateCombo = new SampleFieldFactory().createReferenceCombo("State", State.class, new ArrayList());
        MethodProperty stateProperty = new MethodProperty(personQuery, "state");
        stateCombo.setPropertyDataSource(stateProperty);
        gridLayout.addComponent(stateCombo);

        ComboBox countryCombo = new SampleFieldFactory().createReferenceCombo("Country", Country.class, countryDao.findAll());
        countryCombo.addListener(Field.ValueChangeEvent.class, this, "countryChanged");
        MethodProperty countryProperty = new MethodProperty(personQuery, "country");
        countryCombo.setPropertyDataSource(countryProperty);
        gridLayout.addComponent(countryCombo);

        Button searchButton = new Button("Search", personTable, "search");
        buttonPanel.addComponent(searchButton);

        Button firstButton = new Button("First", personTable, "firstPage");
        buttonPanel.addComponent(firstButton);

        Button previousButton = new Button("Previous", personTable, "previousPage");
        buttonPanel.addComponent(previousButton);

        Button nextButton = new Button("Next", personTable, "nextPage");
        buttonPanel.addComponent(nextButton);

        Button lastButton = new Button("Last", personTable, "lastPage");
        buttonPanel.addComponent(lastButton);

        Button newButton = new Button("New", this, "create");
        buttonPanel.addComponent(newButton);

        Button deleteButton = new Button("Delete", this, "delete");
        buttonPanel.addComponent(deleteButton);
    }

    // Listener Methods

    public void countryChanged(Field.ValueChangeEvent event) {
        Country country = (Country) event.getProperty().getValue();
        BeanItemContainer<State> container = (BeanItemContainer<State>) stateCombo.getContainerDataSource();
        container.removeAllItems();

        stateCombo.select(stateCombo.getNullSelectionItemId());

        if (country != null) {
            country = countryDao.find(country.getId());
            List<State> states = stateDao.findByCountry(country);
            for (State state : states) {
                container.addBean(state);
            }
        }
    }

    public void rowSelected(Property.ValueChangeEvent event) {
        if (event.getProperty().getValue() == null) {
            personForm.setVisible(false);
        } else {
            Object itemId = personTable.getValue();
            POJOItem<Person> personItem = personTable.getContainerDataSource().getItem(itemId);
            personForm.load(personItem.getBean());
            personForm.setVisible(true);
        }
    }

    public void delete() {
        Object itemId = personTable.getValue();
        if (itemId != null) {
            personForm.clear();
            POJOItem<Person> personItem = personTable.getContainerDataSource().getItem(itemId);
            Person person = personItem.getBean();
            personDao.delete(person);
            personTable.search();
        }
    }

    public void create() {
        personForm.create();
        personForm.setVisible(true);
    }

    @Override
    public void terminalError(com.vaadin.terminal.Terminal.ErrorEvent event) {
        if (event.getThrowable().getCause() instanceof AccessDeniedException) {
            getMainWindow().showNotification("Access Denied", Window.Notification.TYPE_ERROR_MESSAGE);
        }
    }

}

