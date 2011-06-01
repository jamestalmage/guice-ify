//Copyright

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
    Provider<Objectify> provider;
    @Override
    protected final void configure() {
        provider = getProvider(Objectify.class);
        config();
    }

    protected <T> void bindQuery(TypeLiteral<Query<T>> typeLiteral, Class<T> clazz){
        bind(typeLiteral).toProvider(new QueryProvider<T>(provider,clazz));
    }

    protected abstract void config();
}
