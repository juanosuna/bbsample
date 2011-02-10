package com.brownbag.sample;

import com.vaadin.data.Container;
import com.vaadin.ui.Table;
import org.springframework.context.annotation.Scope;

/**
 * User: Juan
 * Date: 2/10/11
 * Time: 12:50 AM
 */
@org.springframework.stereotype.Component
@Scope("session")
public class PersonTable extends Table {
    public PersonTable(Container newDataSource) {
        super("Person Results", newDataSource);
        setPageLength(10);
        setSelectable(true);
        setImmediate(true);

        setVisibleColumns(new String[]{"id", "firstName", "lastName", "city", "country"});
        setColumnHeaders(new String[]{"Person Id", "First Name", "Last Name", "City", "Country"});
    }
}
