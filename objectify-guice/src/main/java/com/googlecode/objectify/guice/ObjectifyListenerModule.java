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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import static java.util.logging.Level.FINEST;

/**
 * User: jamestalmage
 * Date: 10/4/11
 * Time: 8:48 PM
 */
public abstract class ObjectifyListenerModule extends AbstractModule{

    static Logger logger = Logger.getLogger(ObjectifyListenerModule.class.getName());
    static {
        logger.setLevel(FINEST);
    }

    static class InternalModule implements Module {
        List<Class> excplicityBound;
        List<Class<? extends ObjectifyEntities>> ofyEntities;
        List<Class<? extends Converter>> converters;



        @Override
        public void configure( Binder binder) {
            excplicityBound = new ArrayList<Class>();
            ofyEntities = new ArrayList<Class<? extends ObjectifyEntities>>();
            converters = new ArrayList<Class<? extends Converter>>();



            binder.bindListener(Matchers.any(), new TypeListener() {
                @Override
                public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter) {
                    final Class<? super I> rawType = type.getRawType();
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
                        ofyEntities.add((Class<? extends ObjectifyEntities>) rawType);
                    }
                    if (Converter.class.isAssignableFrom(rawType)) {
                        if(logger.isLoggable(FINEST)){
                            logger.finest("Listener converter: " + rawType.getName());
                        }
                        converters.add((Class<? extends Converter>) rawType);
                    }
                }
            });
        }



        @Provides
        @Singleton
        public ObjectifyFactory factory(Injector injector){
            System.out.println("Building Factory");
            final ObjectifyFactory factory = new ObjectifyFactory();
            final Conversions conversions = factory.getConversions();

            for (Class c : excplicityBound) {
                if(logger.isLoggable(FINEST)){
                    logger.finest("Explicitly Binding: " + c.getName() );
                }
                factory.register(c);
            }
            for (Class<? extends ObjectifyEntities> ofyEntityClass : ofyEntities) {
                final Iterable<? extends Class> entityClasses
                        = injector.getInstance(ofyEntityClass).getEntityClasses();
                for (Class entityClass : entityClasses) {
                    if(logger.isLoggable(FINEST)){
                        logger.finest("Binding: " + entityClass);
                    }
                    factory.register(entityClass);
                }
            }
            for (Class<? extends Converter> converter : converters) {
                if(logger.isLoggable(FINEST)){
                    logger.finest("Binding Converter: " + converter.getName());
                }
                conversions.add(injector.getInstance(converter));
            }
            return factory;
        }

        @Provides
        public Objectify ofy(ObjectifyFactory factory){
            return factory.begin();
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof InternalModule;
        }

        @Override
        public int hashCode() {
            return InternalModule.class.hashCode();
        }
    }

    static final Class<? extends Annotation> javaxEntity;
    static {
        javaxEntity = ClassNameUtils.loadClass("javax.persistence.Entity");
    }


    static boolean hasEntityAnnotation(Class c){
        return c.isAnnotationPresent(javaxEntity) || c.isAnnotationPresent(Entity.class);
    }

    @Override
    protected final void configure() {
        install(new InternalModule());
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
                    logger.info("INSTALLING: " + clzz.getName());
                    install((Module) clzz.newInstance());
                } catch (Exception e) {
                    addError("Trouble instantiating: " + clzz.getName(),e);
                }
            }
            else {
                logger.info("BINDING: " + clzz.getName());
                bind(clzz);
            }
        }
        else {
            System.out.println(ClassNameUtils.uniqeFullNameFromPackage(pkg,suffix) + " NOT FOUND");
        }
    }
}
