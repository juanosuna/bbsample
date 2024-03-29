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

package com.brownbag.sample.domain.dao;

import com.brownbag.sample.domain.entity.Country;
import com.brownbag.sample.domain.entity.State;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import java.util.List;

@Repository
@Transactional
public class StateDao extends GenericDao<State, String> {
    public List<State> findByCountry(Country country) {
        Query query = getEntityManager().createQuery("SELECT s FROM State s JOIN FETCH s.country WHERE s.country = :country");
        query.setParameter("country", country);
        query.setHint("org.hibernate.cacheable", true);

        return query.getResultList();
    }

}
