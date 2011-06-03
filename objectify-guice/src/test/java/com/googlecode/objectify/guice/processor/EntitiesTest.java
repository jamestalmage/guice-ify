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

package com.googlecode.objectify.guice.processor;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * User: jamestalmage
 * Date: 6/2/11
 * Time: 5:27 PM
 */
public class EntitiesTest {

    Entities entities = new Entities();

    @Before
    public void setUp() throws Exception {
        entities.put("my.pkg","Class1");
        entities.put("my.pkg","Class2");
        entities.put("your.pkg","Class3");
    }

    @Test
    public void stripNamesShouldReturnAListOfNamesIncludingPkg() throws Exception {
        assertEquals(
                Arrays.asList(
                        "my.pkg.Class1",
                        "my.pkg.Class2",
                        "your.pkg.Class3"
                ),
                Entities.stripNames(Arrays.asList(
                        new Entities.Info("my.pkg","Class1"),
                        new Entities.Info("my.pkg","Class2"),
                        new Entities.Info("your.pkg","Class3")
                ),true)
        );
    }

    @Test
    public void stripNamesShouldReturnAListOfNamesWithNoPkg() throws Exception {
        assertEquals(
                Arrays.asList(
                        "Class1",
                        "Class2",
                        "Class3"
                ),
                Entities.stripNames(Arrays.asList(
                        new Entities.Info("my.pkg","Class1"),
                        new Entities.Info("my.pkg","Class2"),
                        new Entities.Info("your.pkg","Class3")
                ),false)
        );
    }

    @Test
    public void EntitiesFromTheSamePackageAreMaintained() throws Exception {
        List<String> names = Entities.stripNames(entities.entitiesInPackage("my.pkg"),false);
        assertTrue(
                names.containsAll(Arrays.asList("Class1","Class2"))
        );
    }

    @Test
    public void allEntitiesAreReturned() throws Exception {
        List<String> names = Entities.stripNames(entities.allEntities(),false);
        assertTrue(
                names.containsAll(Arrays.asList("Class1","Class2","Class3"))
        );
    }

    @Test
    public void duplicatesAreTossed() throws Exception {
        entities.put("your.pkg","Class3");
        assertEquals(1,entities.entitiesInPackage("your.pkg").size());
    }
}
