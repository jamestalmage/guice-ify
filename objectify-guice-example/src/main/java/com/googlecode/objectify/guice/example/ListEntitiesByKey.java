/*
 * Copyright 2011 James Talmage
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.googlecode.objectify.guice.example;


import com.google.inject.Inject;
import com.google.inject.Provider;
import com.googlecode.objectify.Query;

import java.util.List;


/**
 * User: jamestalmage
 * Date: 6/7/11
 * Time: 1:27 PM
 */
public class ListEntitiesByKey {
    @Inject
    Query<MyEntity> query;


    public List<MyEntity> get(){
        return query.order("key").list();
    }

    public Query<MyEntity> getQuery() {
        return query;
    }
}
