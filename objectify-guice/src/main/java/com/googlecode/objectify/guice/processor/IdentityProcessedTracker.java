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
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

/**
 * User: jamestalmage
 * Date: 10/5/11
 * Time: 9:27 PM
 */
class IdentityProcessedTracker implements ProcessedTracker{
    Map<PackageProcessor, Set<String>> map = new IdentityHashMap<PackageProcessor, Set<String>>();

    @Override
    public void markAsProcessed(PackageProcessor processor, String pkg) {
         Set<String> packages = map.get(processor);
        if(packages == null){
            packages = new HashSet<String>();
            map.put(processor,packages);
        }
        packages.add(pkg);
    }

    @Override
    public boolean isProcessed(PackageProcessor processor, String pkg) {
        Set<String> packages = map.get(processor);
        return packages != null && packages.contains(pkg);
    }
}
