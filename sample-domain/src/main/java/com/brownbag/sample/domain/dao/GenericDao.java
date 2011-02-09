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

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

@Repository
@Transactional
public class GenericDao<T, ID extends Serializable> {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    private Class<T> persistentClass;

    public GenericDao() {
        Type type = getClass().getGenericSuperclass();

        if (type != null && type instanceof ParameterizedType) {
            persistentClass = (Class<T>) ((ParameterizedType) type).getActualTypeArguments()[0];
        }
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    protected Class<T> getPersistentClass() {
        if (persistentClass == null) {
            throw new UnsupportedOperationException();
        }

        return persistentClass;
    }

    public void remove(T entity) {
        getEntityManager().remove(entity);
    }

    @Transactional(readOnly = true)
    public List<T> findAll() {
        return executeQuery("select c from " + getPersistentClass().getSimpleName() + " c");
    }

    @Transactional(readOnly = true)
    public List<T> findAll(Class<T> t) {
        return executeQuery("select c from " + t.getSimpleName() + " c");
    }

    protected Query createQuery(String query) {
        return getEntityManager().createQuery(query);
    }

    protected org.hibernate.Query createHibernateQuery(String query) {
        Session session = (Session) getEntityManager().getDelegate();
        return session.createQuery(query);
    }

    @Transactional(readOnly = true)
    public List<T> executeQuery(String query) {
        return getEntityManager().createQuery(query).getResultList();
    }

    @Transactional(readOnly = true)
    public List<T> executeNativeQuery(String query) {
        return getEntityManager().createNativeQuery(query).getResultList();
    }

    @Transactional(readOnly = true)
    public List<T> executeQuery(String query, int firstResult, int maxResults) {
        Query q = getEntityManager().createQuery(query);
        q.setFirstResult(firstResult);
        q.setMaxResults(maxResults);

        return q.getResultList();
    }

    @Transactional(readOnly = true)
    public T find(ID id) {
        return getEntityManager().find(getPersistentClass(), id);
    }

    @Transactional(readOnly = true)
    public T find(Class<T> t, ID id) {
        return getEntityManager().find(t, id);
    }

    @Transactional(readOnly = true)
    public T findByBusinessKey(String propertyName, Object propertyValue) {
        Session session = (Session) getEntityManager().getDelegate();

        Criteria criteria = session.createCriteria(getPersistentClass());
        criteria.add(Restrictions.naturalId().set(propertyName, propertyValue));
        criteria.setCacheable(true);

        return (T) criteria.uniqueResult();
    }

    public void delete(T entity) {
        Query query = getEntityManager().createQuery("delete from " + getPersistentClass().getSimpleName() + " c"
                + " where c = :entity)");

        query.setParameter("entity", entity);

        query.executeUpdate();
    }

    public T merge(T entity) {
        return getEntityManager().merge(entity);
    }

    public void persist(T entity) {
        getEntityManager().persist(entity);
    }

    public void persist(Collection<T> entities) {
        for (T entity : entities) {
            persist(entity);
        }
    }

    public void refresh(T entity) {
        getEntityManager().refresh(entity);
    }
}
