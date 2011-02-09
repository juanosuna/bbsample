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
package com.brownbag.sample.domain.query;

import com.brownbag.sample.domain.entity.State;

/**
 * User: Juan
 * Date: 2/8/11
 * Time: 12:01 AM
 */
public class EntityQuery {
    private Integer pageSize = 10;
    private Integer firstResult = 0;
    private Long resultCount = 0L;

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getFirstResult() {
        return firstResult;
    }

    public void setFirstResult(Integer firstResult) {
        this.firstResult = firstResult;
    }

    public Long getResultCount() {
        return resultCount;
    }

    public void setResultCount(Long resultCount) {
        this.resultCount = resultCount;
    }

    public void nextPage() {
        firstResult = Math.min(firstResult + pageSize, resultCount.intValue());
    }

    public void previousPage() {
        firstResult = Math.max(firstResult - pageSize, 0);
    }

    @Override
    public String toString() {
        return "EntityQuery{" +
                "pageSize=" + pageSize +
                ", firstResult=" + firstResult +
                '}';
    }
}
