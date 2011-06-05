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

import java.util.HashSet;
import java.util.Set;

/**
 * User: jamestalmage
 * Date: 6/2/11
 * Time: 10:52 PM
 */
public abstract class ProcessPerPackageProcessor extends EntityProcessor {
    Set<String> processed = new HashSet<String>();

    void postprocessEntities() {
        for(String pkg:entities.packages()){
            if(!processed.contains(pkg)){
                processed.add(pkg);
                processPackage(pkg);
            }
        }
    }

    String uniqueNameFromPackage(String pkg,String suffix){
        return capitalize(pkg.substring(pkg.lastIndexOf(".")+1))+suffix;
    }

    protected abstract void processPackage(String pkg);
}
