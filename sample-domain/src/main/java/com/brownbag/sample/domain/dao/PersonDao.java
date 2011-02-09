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

import com.brownbag.sample.domain.entity.Person;
import com.brownbag.sample.domain.query.PersonQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import java.util.List;

@Repository
@Transactional
public class PersonDao extends GenericDao<Person, Integer> {

    private static Logger logger = LoggerFactory.getLogger(PersonDao.class);

    public List<Person> find(PersonQuery personQuery) {
        Query query = getEntityManager().createQuery("SELECT COUNT(p) FROM Person p where lastName like :lastName");
        query.setParameter("lastName", "%" + personQuery.getLastName() + "%");
        Long count = (Long) query.getSingleResult();
        personQuery.setResultCount(count);

        query = getEntityManager().createQuery("SELECT p FROM Person p where lastName like :lastName");
        query.setParameter("lastName", "%" + personQuery.getLastName() + "%");
        query.setFirstResult(personQuery.getFirstResult());
        query.setMaxResults(personQuery.getPageSize());

        logger.debug("Executing personQuery: " + personQuery);

        return query.getResultList();
    }
}
