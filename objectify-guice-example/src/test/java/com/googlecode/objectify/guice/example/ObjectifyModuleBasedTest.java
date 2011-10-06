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
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.guice.example.pkg.MyEntityWithStringValue;
import com.googlecode.objectify.guice.example.pkg.PkgObjectifyModule;
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
public class ObjectifyModuleBasedTest {

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

    @Test
    public void testConverterIsFound() throws Exception {
        List<MyEntityWithStringValue> values = objectify.get().query(MyEntityWithStringValue.class).list();
        assertEquals(1,values.size());
        assertEquals("Testing",values.get(0).getValue().toString());
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
        injector = Guice.createInjector(
                new ExampleObjectifyModule(),
                new PkgObjectifyModule());
        objectify = injector.getProvider(Objectify.class);
        objectify.get().put(
                new MyEntity("b", "World, "),
                new MyEntity("a", "Hello "),
                new MyEntity("d", "You?"),
                new MyEntity("c", "How Are "),
                new MyEntityWithStringValue(new MyStringValue("Testing"))
        );
    }

    @After
    public void tearDown() throws Exception {
        helper.tearDown();
    }
}
