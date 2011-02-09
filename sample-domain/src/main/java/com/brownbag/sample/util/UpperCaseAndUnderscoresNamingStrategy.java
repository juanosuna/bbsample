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
package com.brownbag.sample.util;


import org.hibernate.cfg.ImprovedNamingStrategy;

public class UpperCaseAndUnderscoresNamingStrategy extends ImprovedNamingStrategy {

    public static final String TABLE_PREFIX = "";

    public UpperCaseAndUnderscoresNamingStrategy() {
        super();
    }

    @Override
    public String propertyToColumnName(String propertyName) {
        return super.propertyToColumnName(propertyName).toUpperCase();
    }

    @Override
    public String collectionTableName(String ownerEntity, String ownerEntityTable, String associatedEntity, String associatedEntityTable, String propertyName) {
        return super.collectionTableName(ownerEntity, ownerEntityTable, associatedEntity, associatedEntityTable, propertyName).toUpperCase();
    }

    @Override
    public String foreignKeyColumnName(String propertyName, String propertyEntityName, String propertyTableName, String referencedColumnName) {
        return super.foreignKeyColumnName(propertyName, propertyEntityName, propertyTableName, referencedColumnName).toUpperCase();
    }

    @Override
    public String logicalColumnName(String columnName, String propertyName) {
        return super.logicalColumnName(columnName, propertyName).toUpperCase();
    }

    @Override
    public String logicalCollectionTableName(String tableName, String ownerEntityTable, String associatedEntityTable, String propertyName) {
        return TABLE_PREFIX + super.logicalCollectionTableName(tableName, ownerEntityTable, associatedEntityTable, propertyName).toUpperCase();
    }

    @Override
    public String logicalCollectionColumnName(String columnName, String propertyName, String referencedColumn) {
        return super.logicalCollectionColumnName(columnName, propertyName, referencedColumn).toUpperCase();
    }

    @Override
    public String classToTableName(String className) {
        return TABLE_PREFIX + super.classToTableName(className).toUpperCase();
    }

    @Override
    public String tableName(String tableName) {
        return TABLE_PREFIX + super.tableName(tableName).toUpperCase();
    }

    @Override
    public String columnName(String columnName) {
        return super.columnName(columnName).toUpperCase();
    }

    @Override
    public String joinKeyColumnName(String joinedColumn, String joinedTable) {
        return super.joinKeyColumnName(joinedColumn, joinedTable).toUpperCase();
    }
}
