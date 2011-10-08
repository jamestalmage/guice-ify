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
public abstract class EntitiesAndConvertersBuilder extends EntityProcessor{
    
    
    @Override
    protected ProcessorChain createChain() {
        PackageProcessor processor = new ProcessorWrapper(createProcessors());
        return ProcessorChain.builder()
                    .addAnnos("com.googlecode.objectify.guice.IsConverter")
                    .addProcessors(new ConverterCollector())
                    .addAnnos("javax.persistence.Entity", "com.googlecode.objectify.annotation.Entity")
                    .addProcessors(processor)
                    .addAnnos()
                    .addProcessors(processor)
                    .withRunner(new UnhandledConvertersRunner())
                    .build();
    }

    protected abstract Collection<? extends Processor> createProcessors();


    static class ConverterCollector implements PackageProcessor {
        @Override
        public void processPackage(Set<Entities.Info> infoSet, String pkg, ProcessorContext fetcher) {
            fetcher.setAttribute(CONVERTER_KEY + ":" + pkg, infoSet);
            fetcher.getAttribute(CONVERTER_KEY,new HashSet<String>()).add(pkg);
        }
    }
    
    static final String CONVERTER_KEY = ConverterCollector.class.getName();
    
    
    static interface Processor {
        public void process(ProcessorInfo info);
    }

    static interface ProcessorInfo {
        public Set<Entities.Info> getEntities();
        public Set<Entities.Info> getConverters();
        public String getPackageName();
        public ProcessorContext getContext();
    }

    private static class ProcessorWrapper implements PackageProcessor {
        Iterable<? extends Processor> processors;

        private ProcessorWrapper(Iterable<? extends Processor> processors) {
            this.processors = processors;
        }

        @Override
        public void processPackage(Set<Entities.Info> entities, String pkg, ProcessorContext fetcher) {

            HashSet<String> converterPackages = fetcher.getAttribute(CONVERTER_KEY, new HashSet<String>());
            converterPackages.remove(pkg);

            Set<Entities.Info> converters = fetcher.getAttribute(CONVERTER_KEY + ":" + pkg,
                    Collections.<Entities.Info>emptySet());


            ProcessorInfo info = new ProcessorInfoImpl( entities,converters,pkg,fetcher);

            for (Processor processor : processors) {
                processor.process(info);
            }
        }
    }

    private static class ProcessorInfoImpl implements  ProcessorInfo{
        Set<Entities.Info> entities;
        Set<Entities.Info> converters;
        String packageName;
        ProcessorContext context;

        private ProcessorInfoImpl(Set<Entities.Info> entities, Set<Entities.Info> converters, String packageName, ProcessorContext context) {
            this.entities = entities;
            this.converters = converters;
            this.packageName = packageName;
            this.context = context;
        }

        public Set<Entities.Info> getEntities() {
            return entities;
        }

        public Set<Entities.Info> getConverters() {
            return converters;
        }

        public String getPackageName() {
            return packageName;
        }

        public ProcessorContext getContext() {
            return context;
        }
    }

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
