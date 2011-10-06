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

import java.util.Arrays;

/**
 * User: jamestalmage
 * Date: 10/5/11
 * Time: 5:44 AM
 */
public class ObjectifyEntitiesImpl implements ObjectifyEntities{
    Class[] entities;

    public ObjectifyEntitiesImpl(Class ... entities) {
        this.entities = entities;
    }

    @Override
    public Iterable<? extends Class> getEntityClasses() {
        return Arrays.asList(entities);
    }
}
