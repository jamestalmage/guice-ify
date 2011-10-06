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
import com.google.inject.internal.UniqueAnnotations;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.impl.conv.Conversions;
import com.googlecode.objectify.impl.conv.Converter;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import static java.util.logging.Level.FINEST;

/**
* User: jamestalmage
* Date: 10/5/11
* Time: 4:58 AM
* Will listen for
*/
public final class ObjectifyFactoryListenerModule implements Module {

    static Logger logger = Logger.getLogger(ObjectifyFactoryListenerModule.class.getName());
    static {
        //logger.setLevel(FINEST);
    }


    static final Class<? extends Annotation> javaxEntity;
    static {
        javaxEntity = ClassNameUtils.loadClass("javax.persistence.Entity");
    }


    static boolean hasEntityAnnotation(Class c){
        return c.isAnnotationPresent(javaxEntity) || c.isAnnotationPresent(Entity.class);
    }

    public static void bindEntities(Binder binder,Class ... classes){
        binder
                .bind(ObjectifyEntities.class)
                .annotatedWith(UniqueAnnotations.create())
                .toInstance(new ObjectifyEntitiesImpl(classes));
    }

    public static void bindConverters(Binder binder, Class<? extends Converter> ... converters){
        for (Class<? extends Converter> converter : converters) {
            binder.bind(Converter.class)
                    .annotatedWith(UniqueAnnotations.create())
                    .to(converter);
        }
    }

    Set<Class> excplicityBound;
    Set<Key<? extends ObjectifyEntities>> ofyEntities;
    Set<Key<? extends Converter>> converters;

    @Override
    public void configure( Binder binder) {
        logger.info("Setting up");
        excplicityBound = new HashSet<Class>();
        ofyEntities = new HashSet<Key<? extends ObjectifyEntities>>();
        converters = new HashSet<Key<? extends Converter>>();
    }

    private void addClass(Key<?> key) {
        Class rawType = key.getTypeLiteral().getRawType();
        if (hasEntityAnnotation(rawType)) {
            if(logger.isLoggable(FINEST)){
                logger.finest("Listener explicitly adding: " + rawType.getName());
            }
            excplicityBound.add(rawType);
        }
        if (ObjectifyEntities.class.isAssignableFrom(rawType)) {
            if(logger.isLoggable(FINEST)){
                logger.finest("Listener adding entities: " + rawType.getName());
            }
            ofyEntities.add((Key<? extends ObjectifyEntities>) key);
        }
        if (Converter.class.isAssignableFrom(rawType)) {
            if(logger.isLoggable(FINEST)){
                logger.finest("Listener converter: " + rawType.getName());
            }
            converters.add((Key<? extends Converter>) key);
        }
    }

    private void collect(Injector injector){
        while (injector != null){
            for(Key<?> key : injector.getBindings().keySet()){
                addClass(key);
            }
            injector = injector.getParent();
        }
    }

    private void register(Injector injector, ObjectifyFactory factory) {
        for (Class c : excplicityBound) {
            if(logger.isLoggable(FINEST)){
                logger.finest("Explicitly Binding: " + c.getName() );
            }
            factory.register(c);
        }
        for (Key<? extends ObjectifyEntities> ofyEntityClass : ofyEntities) {
            final Iterable<? extends Class> entityClasses
                    = injector.getInstance(ofyEntityClass).getEntityClasses();
            for (Class entityClass : entityClasses) {
                if(logger.isLoggable(FINEST)){
                    logger.finest("Binding: " + entityClass);
                }
                factory.register(entityClass);
            }
        }

        final Conversions conversions = factory.getConversions();
        for (Key<? extends Converter> converter : converters) {
            if(logger.isLoggable(FINEST)){
                logger.finest("Binding Converter: " + converter.getTypeLiteral().getRawType().getName());
            }
            conversions.add(injector.getInstance(converter));
        }
    }



    @Provides
    @Singleton
    public ObjectifyFactory factory(Injector injector){
        final ObjectifyFactory factory = new ObjectifyFactory();
        collect(injector);
        register(injector, factory);
        return factory;
    }

    @Provides
    public Objectify ofy(ObjectifyFactory factory){
        return factory.begin();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ObjectifyFactoryListenerModule;
    }

    @Override
    public int hashCode() {
        return ObjectifyFactoryListenerModule.class.hashCode();
    }
}
