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
package com.googlecode.objectify.guice;

import com.google.inject.Provider;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;

/**
 * User: jamestalmage
 * Date: 6/1/11
 * Time: 4:11 PM
 */
public class QueryProvider<T> implements Provider<Query<T>> {

    Provider<Objectify> ofy;
    Class<T> clazz;

    public QueryProvider(Provider<Objectify> ofy, Class<T> clazz) {
        this.ofy = ofy;
        this.clazz = clazz;
    }

    @Override
    public Query<T> get() {
        return ofy.get().query(clazz);
    }
}