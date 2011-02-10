/*
 * Copyright 2011 Brown Bag Consulting LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.brownbag.sample;

import com.brownbag.sample.domain.dao.PersonDao;
import com.brownbag.sample.domain.entity.Person;
import com.brownbag.sample.domain.query.PersonQuery;
import com.vaadin.Application;
import com.vaadin.data.Property;
import com.vaadin.data.util.AbstractBeanContainer;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Runo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class SampleApplication extends Application {
    private static final long serialVersionUID = 1L;

    @Autowired
    private PersonDao personDao;

    @Autowired
    private PersonForm personForm;

    private Window mainWindow;
    private Panel filterPanel;
    private Panel buttonPanel;
    private Panel tableDetailPanel;
    private Panel tablePanel;
    private Panel detailPanel;

    private PersonQuery personQuery = new PersonQuery();
    private Table table;
    private BeanContainer<Long, Person> personContainer;

    @Override
    public void init() {

        setTheme("mytheme");

        initLayout();
        initButtonsAndFields();
        personContainer = createPersonContainer();

        table = new PersonTable(personContainer);
        tablePanel.addComponent(table);
        table.addListener(Property.ValueChangeEvent.class, this, "rowSelected");

        detailPanel.addComponent(personForm);

        search();
    }

    private static BeanContainer<Long, Person> createPersonContainer() {
        BeanContainer<Long, Person> personContainer = new BeanContainer<Long, Person>(Person.class);
        AbstractBeanContainer.BeanIdResolver<Long, Person> personIdResolver = new AbstractBeanContainer.BeanIdResolver<Long, Person>() {
            @Override
            public Long getIdForBean(Person bean) {
                return bean.getId();
            }
        };
        personContainer.setBeanIdResolver(personIdResolver);

        return personContainer;
    }

    private void initLayout() {
        mainWindow = new Window("Sample Application");
        setMainWindow(mainWindow);

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setMargin(true);
        mainLayout.setSpacing(true);
        mainWindow.setContent(mainLayout);

        filterPanel = createPanel(new HorizontalLayout());
        mainWindow.addComponent(filterPanel);

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
        MethodProperty lastNameProperty = new MethodProperty(personQuery, "lastName");
        TextField lastNameField = new TextField("Last Name", lastNameProperty);
        filterPanel.addComponent(lastNameField);

        MethodProperty pageProperty = new MethodProperty(personQuery, "pageSize");
        TextField pageSizeField = new TextField("Page Size", pageProperty);
        filterPanel.addComponent(pageSizeField);

        Button searchButton = new Button("Search", this, "search");
        buttonPanel.addComponent(searchButton);

        Button previousButton = new Button("Previous", this, "previous");
        buttonPanel.addComponent(previousButton);

        Button nextButton = new Button("Next", this, "next");
        buttonPanel.addComponent(nextButton);
    }

    // listener methods

    public void rowSelected(Property.ValueChangeEvent event) {
        if (event.getProperty().getValue() == null) {
            personForm.setVisible(false);
            return;
        }

        Object itemId = table.getValue();
        BeanItem<Person> personItem = personContainer.getItem(itemId);

        personForm.setItemDataSource(personItem);
        personForm.setVisible(true);
    }

    public void search() {
        List<Person> persons = personDao.find(personQuery);
        personContainer.removeAllItems();
        personContainer.addAll(persons);
    }

    public void previous() {
        personQuery.previousPage();
        search();
    }

    public void next() {
        personQuery.nextPage();
        search();
    }
}

