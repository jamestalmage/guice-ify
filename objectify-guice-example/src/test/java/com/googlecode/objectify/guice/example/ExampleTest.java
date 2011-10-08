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

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.googlecode.objectify.Objectify;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * User: jamestalmage
 * Date: 6/7/11
 * Time: 1:44 PM
 */
public class ExampleTest {

    Injector injector;
    Provider<Objectify> objectify;

    LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    @Test
    public void testListEntitiesByKey() throws Exception {
        final List<MyEntity> list = injector.getInstance(ListEntitiesByKey.class).get();
        assertEquals("Hello World, How Are You?", concatNames(list));
    }

    @Test
    public void testTypeSafeListEntitiesByKey() throws Exception {
        final List<MyEntity> list = injector.getInstance(TypeSafeListEntitiesByKey.class).get("<", "d");
        assertEquals("Hello World, How Are ",concatNames(list));
    }

    private String concatNames(List<MyEntity> list) {
        StringBuilder sb = new StringBuilder();
        for (MyEntity entity : list) {
            sb.append(entity.getName());
        }
        return sb.toString();
    }

    @Before
    public void setUp() throws Exception {
        helper.setUp();
        injector = Guice.createInjector(new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(Objectify.class).toProvider(new ObjectifyProvider());
                    }
                   /*
                   Adding ExampleQueryModule twice here is intentional
                   we've implemented equals and hash code in such a way that the second instance is ignored
                    */
                }, new ExampleNoDepModule(),new ExampleNoDepModule());
        objectify = injector.getProvider(Objectify.class);
        objectify.get().put(
                new MyEntity("b", "World, "),
                new MyEntity("a", "Hello "),
                new MyEntity("d", "You?"),
                new MyEntity("c", "How Are ")
        );
    }

    @After
    public void tearDown() throws Exception {
        helper.tearDown();
    }
}
