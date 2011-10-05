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

import java.lang.annotation.Annotation;

/**
 * User: jamestalmage
 * Date: 10/4/11
 * Time: 10:07 PM
 */
public class ClassNameUtils {

    public static String capitalize(String s){
        StringBuilder sb = new StringBuilder();
        sb.append(Character.toUpperCase(s.charAt(0)));
        sb.append(s.substring(1));
        return sb.toString();
    }

    public static String uniqueNameFromPackage(String pkg,String suffix){
        return capitalize(pkg.substring(pkg.lastIndexOf(".") + 1))+suffix;
    }

    public static String uniqeFullNameFromPackage(String pkg, String suffix){
        return pkg + "." + uniqueNameFromPackage(pkg,suffix);
    }

    public static Class loadUniquelyNamedClass(String pkg, String suffix){
        final String className = uniqeFullNameFromPackage(pkg, suffix);
        return loadClass(className);
    }

    public static Class loadClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
