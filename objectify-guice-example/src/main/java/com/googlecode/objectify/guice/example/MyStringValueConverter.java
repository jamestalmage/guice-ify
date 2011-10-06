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

import com.googlecode.objectify.guice.IsConverter;
import com.googlecode.objectify.impl.conv.Converter;
import com.googlecode.objectify.impl.conv.ConverterLoadContext;
import com.googlecode.objectify.impl.conv.ConverterSaveContext;

/**
 * User: jamestalmage
 * Date: 10/5/11
 * Time: 1:04 AM
 */
@IsConverter
public class MyStringValueConverter implements Converter{
    @Override
    public Object forDatastore(Object value, ConverterSaveContext ctx) {
        if(value instanceof MyStringValue){
            return value.toString();
        }
        return null;
    }

    @Override
    public Object forPojo(Object value, Class<?> fieldType, ConverterLoadContext ctx, Object onPojo) {
        if(MyStringValue.class.equals(fieldType) && (value instanceof String)){
            return new MyStringValue((String) value);
        }
        return null;
    }


}
