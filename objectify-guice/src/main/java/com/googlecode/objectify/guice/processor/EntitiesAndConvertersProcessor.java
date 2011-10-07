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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * User: jamestalmage
 * Date: 10/6/11
 * Time: 10:22 AM
 */
public abstract class EntitiesAndConvertersProcessor extends EntityProcessor{
    
    
    @Override
    protected ProcessorChain createChain() {
        return ProcessorChain.builder()
                    .addAnnos("com.googlecode.objectify.guice.IsConverter")
                    .addProcessors(new ConverterCollector())
                    .addAnnos("javax.persistence.Entity", "com.googlecode.objectify.annotation.Entity")
                    .addProcessors(toArray(createEntityProcessors()))
                    .addAnnos()
                    .addProcessors(toArray(createConverterProcessors()))
                    .withRunner(new UnhandledConvertersRunner())
                    .build();
    }

    protected abstract Collection<? extends PackageProcessor> createConverterProcessors();


    protected abstract Collection<? extends PackageProcessor> createEntityProcessors();

    static class ConverterCollector implements PackageProcessor {
        @Override
        public void processPackage(Set<Entities.Info> infoSet, String pkg, ProcessorContext fetcher) {
            fetcher.setAttribute(CONVERTER_KEY + ":" + pkg, infoSet);
            fetcher.getAttribute(CONVERTER_KEY,new HashSet<String>()).add(pkg);
        }
    }
    
    static final String CONVERTER_KEY = ConverterCollector.class.getName();
    
    
    
    static class UnhandledConvertersRunner extends ProcessorChain.LinkRunnerImpl {
        @Override
        protected void runProcessor(PackageProcessor processor, Entities merged, ProcessorContext context) {
            HashSet<String> attribute = context.getAttribute(CONVERTER_KEY, new HashSet<String>());
            for (String pkg : new HashSet<String>(attribute)) {
                processor.processPackage(Collections.<Entities.Info>emptySet(),pkg,context);
            }
        }
    }
    
    static PackageProcessor[] toArray(Collection<? extends PackageProcessor> collection){
        return collection.toArray(new PackageProcessor[collection.size()]);
    } 
}
