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

    @Override
    void postprocessEntities() {
        postprocessEntities(mergeAll(),getProcessors());
    }

    protected Entities mergeAll() {
        return Entities.merge(entitiesMap.values());
    }

    void postprocessEntities(Entities entities,Iterable<? extends PackageProcessor> processors) {

        for (PackageProcessor processor : processors) {
            for(String pkg:entities.packages()){
                if(!getTracker().isProcessed(processor,pkg)){
                    processor.processPackage(entities, pkg, this);
                    getTracker().markAsProcessed(processor,pkg);
                }
            }
        }
    }

    ProcessedTracker tracker = null;
    public ProcessedTracker getTracker(){
        if(tracker == null){
            tracker = createTracker();
        }
        return tracker;
    }

    private ProcessedTracker createTracker() {
        return new IdentityProcessedTracker();
    }

    protected abstract Iterable<? extends PackageProcessor> getProcessors();
    //protected abstract void processPackage(Entities entities,String pkg);
}
