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

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;

/**
 * User: jamestalmage
 * Date: 6/1/11
 * Time: 4:06 PM
 */
public abstract class AbstractQueryModule extends AbstractModule {
    public static final String SUFFIX = "QueryModule";
    Provider<Objectify> provider;
    @Override
    protected final void configure() {
        provider = getProvider(Objectify.class);
        config();
    }

    protected <T> void bindQuery(TypeLiteral<Query<T>> typeLiteral, Class<T> clazz){
        bind(typeLiteral).toProvider(new QueryProvider<T>(provider, clazz));
    }

    protected abstract void config();
}
