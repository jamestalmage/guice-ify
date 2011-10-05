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

import com.google.inject.*;
import com.google.inject.matcher.Matcher;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.impl.conv.Conversions;
import com.googlecode.objectify.impl.conv.Converter;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: jamestalmage
 * Date: 10/4/11
 * Time: 8:48 PM
 */
public abstract class ObjectifyListenerModule extends AbstractModule{

    static final Class<? extends Annotation> javaxEntity;
    static {
        javaxEntity = ClassNameUtils.loadClass("javax.persistence.Entity");
    }

    Set<Class> explicityBoundEntities = new HashSet<Class>();
    Set<TypeLiteral<? extends ObjectifyEntities>> entitiesSet = new HashSet<TypeLiteral<? extends ObjectifyEntities>>();
    Set<TypeLiteral<? extends Converter>> converters = new HashSet<TypeLiteral<? extends Converter>>();
    @Override
    protected void bindListener(Matcher<? super TypeLiteral<?>> typeMatcher, TypeListener listener) {
        super.bindListener(typeMatcher, listener);
    }

    boolean hasEntityAnnotation(Class c){
        return c.isAnnotationPresent(javaxEntity) || c.isAnnotationPresent(Entity.class);
    }

    @Override
    protected final void configure() {
        bindListener(Matchers.any(),new TypeListener() {
            @Override
            public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter) {
                final Class<? super I> rawType = type.getRawType();

                if(hasEntityAnnotation(rawType)){
                    explicityBoundEntities.add(rawType);
                }
                if(ObjectifyEntities.class.isAssignableFrom(rawType)){
                    entitiesSet.add((TypeLiteral<? extends ObjectifyEntities>) type);
                }
                if(Converter.class.isAssignableFrom(rawType)){
                    converters.add((TypeLiteral<? extends Converter>) type);
                }
            }
        });
        configurePackages();
    }

    protected abstract void configurePackages();

    protected void bindPackages(String ... packages){
        for (String pkg : packages) {
            bindPackage(pkg);
        }
    }

    protected List<String> getSuffixes(){
        return Arrays.asList(ObjectifyEntities.SUFFIX,AbstractQueryModule.SUFFIX);
    }

    protected void bindPackage(String pkg) {
        for(String suffix: getSuffixes()){
            bindOrInstallUniquelyNamed(pkg,suffix);
        }
    }

    protected void bindOrInstallUniquelyNamed(String pkg,String suffix){
        Class<?> clzz = ClassNameUtils.loadUniquelyNamedClass(pkg,suffix);
        if(clzz != null){
            if(Module.class.isAssignableFrom(clzz)){
                try {
                    //System.out.println("INSTALLING: " + clzz.getName());
                    install((Module) clzz.newInstance());
                } catch (Exception e) {
                    addError("Trouble instantiating: " + clzz.getName(),e);
                }
            }
            else {
                //System.out.println("BINDING: " + clzz.getName());
                bind(clzz);
            }
        }
        else {
            //System.out.println(ClassNameUtils.uniqeFullNameFromPackage(pkg,suffix) + " NOT FOUND");
        }
    }

    @Provides
    @Singleton
    public ObjectifyFactory factory(Injector injector){
        final ObjectifyFactory factory = new ObjectifyFactory();
        final Conversions conversions = factory.getConversions();


        for (TypeLiteral<? extends Converter> converter : converters) {
            System.out.println("ADDING CONVERTER:" + converter.getClass());
            final Converter converterInstance = injector.getInstance(Key.get(converter));
            conversions.add(converterInstance);
        }
        for (TypeLiteral<? extends ObjectifyEntities> entities : entitiesSet) {
            Iterable<? extends Class> entityClasses = injector.getInstance(Key.get(entities)).getEntityClasses();
            for (Class<?> entityClass : entityClasses) {
                factory.register(entityClass);
            }
        }
        for (Class entity : explicityBoundEntities) {
            factory.register(entity);
        }
        return factory;
    }

    @Provides
    public Objectify ofy(ObjectifyFactory factory){
        return factory.begin();
    }
}
