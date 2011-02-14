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

import com.brownbag.sample.domain.dao.PersonDao;
import com.brownbag.sample.domain.entity.Person;
import com.brownbag.sample.domain.query.EntityQuery;
import com.brownbag.sample.domain.query.PersonQuery;
import com.vaadin.data.util.POJOContainer;
import com.vaadin.ui.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import java.util.List;

/**
 * User: Juan
 * Date: 2/10/11
 * Time: 12:50 AM
 */
@org.springframework.stereotype.Component
@Scope("session")
public class PersonTable extends Table {

    public static final String[] FIELDS = new String[]{"id", "firstName", "lastName", "address.state", "address.country"};
    public static final String[] LABELS = new String[]{"Person Id", "First Name", "Last Name", "State", "Country"};

    @Autowired
    private PersonQuery personQuery;

    @Autowired
    private PersonDao personDao;

    public PersonTable() {
        super("Person Results", new POJOContainer<Person>(Person.class, FIELDS));
        setPageLength(10);
        setSelectable(true);
        setImmediate(true);

        setVisibleColumns(FIELDS);
        setColumnHeaders(LABELS);
    }

    @Override
    public POJOContainer getContainerDataSource() {
        return (POJOContainer) super.getContainerDataSource();
    }

    @Override
    public void sort(Object[] propertyId, boolean[] ascending) throws UnsupportedOperationException {
        if (propertyId.length > 1) {
            throw new RuntimeException("Cannot sort on more than one column");
        } else if (propertyId.length == 1) {
            personQuery.setOrderByField(propertyId[0]);
            if (ascending[0]) {
                personQuery.setOrderDirection(EntityQuery.OrderDirection.ASC);
            } else {
                personQuery.setOrderDirection(EntityQuery.OrderDirection.DESC);
            }
            search();
        }
    }

    public void search() {
        personQuery.setFirstResult(0);
        runQuery();
    }

    public void previousPage() {
        personQuery.previousPage();
        runQuery();
    }

    public void nextPage() {
        personQuery.nextPage();
        runQuery();
    }

    private void runQuery() {
        List<Person> persons = personDao.find(personQuery);
        getContainerDataSource().removeAllItems();
        for (Person person : persons) {
            getContainerDataSource().addPOJO(person);
        }
    }
}
