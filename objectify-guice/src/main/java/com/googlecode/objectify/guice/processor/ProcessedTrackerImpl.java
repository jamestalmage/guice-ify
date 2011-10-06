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

import java.util.*;

/**
 * User: jamestalmage
 * Date: 10/5/11
 * Time: 9:27 PM
 */
abstract class ProcessedTrackerImpl implements ProcessedTracker{

    private ProcessedTrackerImpl() {
    }

    public static ProcessedTracker identityTracker(){
        return new ProcessorKeyedTracker(new IdentityHashMap<PackageProcessor, Set<String>>());
    }

    public static ProcessedTracker defaultTracker(){
        return new ProcessorKeyedTracker(new HashMap<PackageProcessor, Set<String>>());
    }

    public static ProcessedTracker perProcessorClass(){
        return new MapBasedTracker<Class>(new HashMap<Class, Set<String>>()) {
            @Override
            protected Class key(PackageProcessor processor) {
                return processor.getClass();
            }
        };
    }

    abstract Set<String> getForProcessor(PackageProcessor processor);
    abstract void setForProcessor(PackageProcessor processor, Set<String> packages);

    static abstract class MapBasedTracker<K> extends ProcessedTrackerImpl {

        final Map<K, Set<String>> map;

        protected abstract K key(PackageProcessor processor);

        public MapBasedTracker(Map<K, Set<String>> map) {
            this.map = map;
        }

        @Override
        Set<String> getForProcessor(PackageProcessor processor) {
            return map.get(key(processor));
        }

        @Override
        void setForProcessor(PackageProcessor processor, Set<String> packages) {
            map.put(key(processor),packages);
        }
    }

    static class ProcessorKeyedTracker extends MapBasedTracker<PackageProcessor>{
        protected ProcessorKeyedTracker(Map<PackageProcessor, Set<String>> packageProcessorSetMap) {
            super(packageProcessorSetMap);
        }

        @Override
        protected PackageProcessor key(PackageProcessor processor) {
            return processor;
        }
    }

    @Override
    public void markAsProcessed(PackageProcessor processor, String pkg) {
        Set<String> packages = getForProcessor(processor);
        if(packages == null){
            packages = new HashSet<String>();
            setForProcessor(processor,packages);
        }
        packages.add(pkg);
    }

    @Override
    public boolean isProcessed(PackageProcessor processor, String pkg) {
        Set<String> packages = getForProcessor(processor);
        return packages != null && packages.contains(pkg);
    }
}
