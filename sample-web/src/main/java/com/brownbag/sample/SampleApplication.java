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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SampleApplication extends Application {
    private static final long serialVersionUID = 1L;

    @Autowired
    private PersonDao personDao;

    private Window mainWindow;
    private Panel filterPanel;
    private Panel buttonPanel;
    private Panel tablePanel;
    private Panel detailPanel;

    private PersonQuery personQuery = new PersonQuery();

    private Table table;
    private BeanContainer<Long, Person> personContainer;

    private ArrayList<Object> visibleColumnIds = new ArrayList<Object>();
    private ArrayList<String> visibleColumnLabels = new ArrayList<String>();

    private PersonForm personForm;

    @Override
    public void init() {

        setTheme("mytheme");

        initLayout();
        initButtonsAndFields();
        initTable();

        detailPanel = new Panel();
        detailPanel.addStyleName(Runo.PANEL_LIGHT);
        tablePanel.addComponent(detailPanel);

        personContainer = new BeanContainer<Long, Person>(Person.class);

        AbstractBeanContainer.BeanIdResolver<Long, Person> personIdResolver = new AbstractBeanContainer.BeanIdResolver<Long, Person>() {
            @Override
            public Long getIdForBean(Person bean) {
                return bean.getId();
            }
        };
        personContainer.setBeanIdResolver(personIdResolver);

        table.setContainerDataSource(personContainer);
        table.setVisibleColumns(visibleColumnIds.toArray());
        table.setColumnHeaders(visibleColumnLabels.toArray(new String[0]));

        BeanItem<Person> personItem = new BeanItem<Person>(new Person());
        personForm = new PersonForm(personItem);
        detailPanel.addComponent(personForm);

        search();
    }

    private void initLayout() {
        mainWindow = new Window("Sample Application");
        setMainWindow(mainWindow);

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setMargin(true);
        mainLayout.setSpacing(true);
        mainWindow.setContent(mainLayout);

        filterPanel = addHorizontalPanel();
        buttonPanel = addHorizontalPanel();
        tablePanel = addHorizontalPanel();
    }

    private Panel addHorizontalPanel() {
        Panel panel = new Panel();
        panel.addStyleName(Runo.PANEL_LIGHT);
        HorizontalLayout layout = new HorizontalLayout();
        layout.setMargin(false);
        layout.setSpacing(true);
        panel.setContent(layout);
        mainWindow.addComponent(panel);

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

    private void initTable() {
        String[] columnIds = {"id", "firstName", "lastName", "city", "country"};
        visibleColumnIds.addAll(Arrays.asList(columnIds));

        String[] columnLabels = {"Person Id", "First Name", "Last Name", "City", "Country"};
        visibleColumnLabels.addAll(Arrays.asList(columnLabels));

        table = new Table();
        tablePanel.addComponent(table);

        table.setCaption("Results");
        table.setPageLength(10);
        table.setSelectable(true);

        table.setImmediate(true);
        table.addListener(new Property.ValueChangeListener() {
            public void valueChange(Property.ValueChangeEvent event) {
                rowSelected();
            }
        });
        table.addListener(Property.ValueChangeListener.class, this, "rowSelected");
    }

    public void rowSelected() {
        Object itemId = table.getValue();
        BeanItem<Person> personItem = personContainer.getItem(itemId);
        personForm.setItemDataSource(personItem);
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

