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