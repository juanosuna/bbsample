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

package com.brownbag.sample.domain.entity;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

@MappedSuperclass
@EntityListeners({WritableEntity.WritableEntityListener.class})
public abstract class WritableEntity {

    private static final long serialVersionUID = 1L;

    public static final String SCHEMA = "SAMPLE";
    public static final String SYSTEM_USER = "System";

    private static final ThreadLocal<String> currentUser = new ThreadLocal<String>();

    public static String getCurrentUser() {
        if (currentUser.get() == null) {
            return SYSTEM_USER;
        } else {
            return currentUser.get();
        }
    }

    public static void setCurrentUser(String user) {
        currentUser.set(user);
    }

    @Id
    @GeneratedValue
    private Long id;

    @NaturalId
    @Column(unique = true, nullable = false, updatable = false)
    private String uuid;

    @Version
    private Integer version;

    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date lastModified;

    @NotNull
    private String lastModifiedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date created;

    @NotNull
    private String createdBy;

    protected WritableEntity() {
        uuid = UUID.randomUUID().toString();
    }

    public Long getId() {
        return id;
    }

    public String getUuid() {
        return uuid;
    }

    public Integer getVersion() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WritableEntity)) return false;

        WritableEntity that = (WritableEntity) o;

        if (!getUuid().equals(that.getUuid())) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return getUuid().hashCode();
    }

    @Override
    public String toString() {
        return "WritableEntity{" +
                "uuid=" + getUuid() +
                '}';
    }

    public static class WritableEntityListener {
        @PrePersist
        public void onPrePersist(WritableEntity writableEntity) {
            writableEntity.created = new Date();
            writableEntity.lastModified = writableEntity.created;

            writableEntity.createdBy = getCurrentUser();
            writableEntity.lastModifiedBy = writableEntity.createdBy;
        }

        @PreUpdate
        public void onPreUpdate(WritableEntity writableEntity) {
            writableEntity.lastModified = new Date();
            writableEntity.lastModifiedBy = getCurrentUser();
        }
    }
}
