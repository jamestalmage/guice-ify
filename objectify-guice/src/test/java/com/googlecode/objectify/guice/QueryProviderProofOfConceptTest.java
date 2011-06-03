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

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.inject.*;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.Query;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.Id;

import static org.junit.Assert.assertEquals;

/**
 * User: jamestalmage
 * Date: 6/1/11
 * Time: 4:28 PM
 */
public class QueryProviderProofOfConceptTest {

    @Test
    public void runTest(){
        ofy.get().put(
                new MyEntity(1l,"1-1"),
                new MyEntity(2l,"1-2"),
                new MyEntity2(1l,"2-1"),
                new MyEntity2(2l,"2-2")
        );

        assertEquals("e1-1","1-1", instance().query.filter("id", 1l).get().value);
        assertEquals("e1-1","1-2", instance().query.filter("id", 2l).get().value);
        assertEquals("e1-1","2-1", instance().query2.filter("id", 1l).get().value);
        assertEquals("e1-1","2-2", instance().query2.filter("id", 2l).get().value);

    }

    Injected instance(){
        return injector.getInstance(Injected.class);
    }
    public static class Injected {
        @Inject Query<MyEntity> query;
        @Inject Query<MyEntity2> query2;
    }
    public static class MyEntity {
        public MyEntity() {
        }

        public MyEntity(Long id, String value) {
            this.id = id;
            this.value = value;
        }

        @Id Long id;
        String value;
    }

    public static class MyEntity2 {
        public MyEntity2() {
        }

        public MyEntity2(Long id, String value) {
            this.id = id;
            this.value = value;
        }

        @Id Long id;
        String value;
    }

    public static class DAOProvider implements Provider<Objectify> {
        final ObjectifyFactory fact = new ObjectifyFactory();
        public DAOProvider() {
            fact.register(MyEntity.class);
            fact.register(MyEntity2.class);
        }

        @Override
        public Objectify get() {
            return fact.begin();
        }
    }

    public static class MyModule extends AbstractModule{
        @Override
        protected void configure() {
            bind(Objectify.class).toProvider(new DAOProvider());
        }
    }

    public static class MyQueryModule extends AbstractQueryModule{
        @Override
        protected void config() {
            bindQuery(new TypeLiteral<Query<MyEntity>>(){},MyEntity.class);
            bindQuery(new TypeLiteral<Query<MyEntity2>>(){},MyEntity2.class);
        }
    }


    Injector injector;
    Provider<Objectify> ofy;

    LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    @Before
    public void setUp() throws Exception {
        helper.setUp();
        injector = Guice.createInjector(new MyModule(),new MyQueryModule());
        ofy = injector.getProvider(Objectify.class);
    }

    @After
    public void tearDown() throws Exception {
        helper.tearDown();
    }
}
