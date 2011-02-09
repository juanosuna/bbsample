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
package com.brownbag.sample.domain.dao;

import com.brownbag.sample.domain.entity.Country;
import com.brownbag.sample.domain.entity.Person;
import com.brownbag.sample.domain.entity.State;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.IfProfileValue;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:applicationContext-data-access.xml",
        "classpath:applicationContext-data-init.xml"
})
@TransactionConfiguration(transactionManager="transactionManager", defaultRollback=false)
@Transactional
public class TestInitializer {

    @Autowired
    private GenericDao genericDao;

    @Autowired
    private PersonDao personDao;

    @IfProfileValue(name="initDB", value="true")
    @Test
    public void initialize() throws Exception {

        initializeCountries();
        initializeStates();

        initializePersons();
    }

    private void initializePersons() {
        for (Integer i = 200; i < 400; i++) {
            Person person = new Person(
                    "firstName" + i,
                    "lastName" + i,
                     i.toString()
            );

            person.setStreet("100 Main St");
            person.setCity("Charlotte");
            person.setState(new State("NC"));
            person.setCountry(new Country("US"));
            person.setZipCode("28202");
            person.setBirthDate(new Date());

            personDao.persist(person);
        }
    }

    private void initializeStates() {
        State state = new State("NC", "North Carolina");
        genericDao.persist(state);

        state = new State("VA", "Virginia");
        genericDao.persist(state);
    }

    private void initializeCountries() {
        Country country = new Country("US", "United States");
        genericDao.persist(country);

        country = new Country("CA", "Canada");
        genericDao.persist(country);
    }
}
