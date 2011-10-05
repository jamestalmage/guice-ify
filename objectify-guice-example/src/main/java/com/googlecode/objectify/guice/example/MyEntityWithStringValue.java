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

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * User: jamestalmage
 * Date: 10/5/11
 * Time: 1:10 AM
 */
@Entity
public class MyEntityWithStringValue {

    public MyEntityWithStringValue() {
    }

    public MyEntityWithStringValue(Long id, MyStringValue value) {
        this.id = id;
        this.value = value;
    }

    public MyEntityWithStringValue(MyStringValue value) {
        this.value = value;
    }

    @Id
    Long id;


    MyStringValue value;

    public MyStringValue getValue() {
        return value;
    }

    public void setValue(MyStringValue value) {
        this.value = value;
    }
}
