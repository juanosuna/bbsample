package com.brownbag.sample.domain.dao;

import com.brownbag.sample.domain.entity.Country;
import com.brownbag.sample.domain.entity.Person;
import com.brownbag.sample.domain.query.PersonQuery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class PersonDaoTest extends AbstractDomainTest {

    @Autowired
    private PersonDao personDao;

    @Autowired
    private GenericDao genericDao;

    @Before
    public void setup() {
        Person person = new Person();
        person.setFirstName("Juan");
        person.setLastName("Osuna");
        person.setStreet("100 Main St.");
        person.setCity("Madrid");
        Country country = new Country("ES", "Spain");
        genericDao.persist(country);
        person.setCountry(country);

        personDao.persist(person);
    }

    @Test
    public void findByName() {
        PersonQuery personQuery = new PersonQuery();
        personQuery.setLastName("Osuna");
        List<Person> persons = personDao.find(personQuery);
        Assert.assertNotNull(persons);
        Assert.assertTrue(persons.size() > 0);
        Assert.assertEquals("Osuna", persons.get(0).getLastName());
    }
}
