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

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import java.util.*;

/**
 * User: jamestalmage
 * Date: 6/2/11
 * Time: 4:44 PM
 */
class Entities {
    Map<String,Set<Info>> map = new HashMap<String, Set<Info>>();

    void put(Element entity, ProcessingEnvironment env){
        put(new Info(entity,env));
    }

    void put(Info info) {
        Set<Info> list = map.get(info.getPackageName());
        if(list == null){
            list = new HashSet<Info>();
            map.put(info.getPackageName(),list);
        }
        list.add(info);
    }

    /**
     * For Testing
     */
    void put(String pkg, String name){
        put(new Info(pkg,name));
    }

    Set<String> packages() {
        return map.keySet();
    }

    Set<Info> entitiesInPackage(String packageName){
        return map.get(packageName);
    }

    Set<Info> allEntities(){
        Set<Info> ret = new HashSet<Info>();
        for (Set<Info> pkg : map.values()) {
            ret.addAll(pkg);
        }
        return ret;
    }

    static List<String> stripNames(Iterable<Info> infos,boolean includePackage){
        List<String> list = new ArrayList<String>();
        for(Info info:infos){
            list.add(includePackage ? info.toString() : info.getName());
        }
        return list;
    }

    static String basePackage(Iterable<String> classOrPackageNames){
        Iterator<String> iter = classOrPackageNames.iterator();
        String[] split = iter.next().split("\\.");
        throw new RuntimeException("Unimplemented");
        //String
        //for (St)

    }

    static class Info{
        String pkg;
        String name;

        Info(Element entity, ProcessingEnvironment env) {
            pkg = env.getElementUtils().getPackageOf(entity).getQualifiedName().toString();
            if(name == null) name = entity.getSimpleName().toString();
        }

        /**
         * For Testing
         */
        Info(String pkg, String name) {
            this.pkg = pkg;
            this.name = name;
        }

        String getPackageName(){
            return pkg;
        }

        String getName(){
            return name;
        }

        @Override
        public String toString() {
            return getFullName();
        }

        public String getFullName() {
            if(pkg.isEmpty()) return name;
            return pkg + "." + name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Info info = (Info) o;

            if (!name.equals(info.name)) return false;
            if (!pkg.equals(info.pkg)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = pkg.hashCode();
            result = 31 * result + name.hashCode();
            return result;
        }
    }
}
