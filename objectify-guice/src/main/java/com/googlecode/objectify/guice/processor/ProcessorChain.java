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

import javax.naming.Context;
import java.util.*;

/**
 * User: jamestalmage
 * Date: 10/6/11
 * Time: 5:12 AM
 */
public class ProcessorChain {
    private ProcessorChain(){}

    public static interface LinkRunner{
        public void run(Link link, ProcessorContext context);
    }
    public static abstract class LinkRunnerImpl implements LinkRunner{
        @Override
        public void run(Link link, ProcessorContext context) {
            Entities merged = context.createMerged(link.getAnnotations());
            for(PackageProcessor processor: link.getProcessors()){
                runProcessor(processor,merged,context);
            }
        }

        protected abstract void runProcessor(PackageProcessor processor, Entities merged, ProcessorContext context);
    }
    static LinkRunner defaultRunner = new LinkRunnerImpl(){
        @Override
        protected void runProcessor(PackageProcessor processor, Entities merged, ProcessorContext context) {

                for(String pkg:merged.packages()){
                    if(!context.getTracker().isProcessed(processor,pkg)){
                        processor.processPackage(merged.entitiesInPackage(pkg), pkg, context);
                        context.getTracker().markAsProcessed(processor,pkg);
                    }
                }

        }
    };
    public static class Link{
        private Link(Set<String> annotations,Set<PackageProcessor> processors,LinkRunner runner) {
            this.annotations = annotations;
            this.processors = processors;
            this.runner = runner;
        }

        private LinkRunner runner;

        private Set<String> annotations;
        private Set<PackageProcessor> processors;

        public Set<String> getAnnotations() {
            return annotations;
        }

        public Set<PackageProcessor> getProcessors() {
            return processors;
        }

        public void run(ProcessorContext context){
            runner.run(this,context);
        }
    }

    List<Link> links = new ArrayList<Link>();

    public List<Link> getLinks() {
        return links;
    }

    public void run(ProcessorContext context){
        for(Link link:links){
            link.run(context);
        }
    }

    private static class Builder implements CompleteBuilder {

        Set<String> annotations;
        Set<PackageProcessor> processors;
        LinkRunner runner = defaultRunner;

        ProcessorChain chain = new ProcessorChain();

        ProcessorChain getChain(){
            if(chain == null) throw new IllegalStateException("this builder is locked");
            return chain;
        }

        @Override
        public AnnoBuilder addAnnos(String... anos) {
            if(processors != null){
                pushLink();
            }
            if(annotations == null){
                annotations = new HashSet<String>();
            }
            annotations.addAll(Arrays.asList(anos));
            return this;
        }

        @Override
        public ProcessorChain build() {
            pushLink();
            ProcessorChain chain =  getChain();
            this.chain = null;
            return chain;
        }

        private void pushLink() {
            if(annotations == null && processors == null)
                return;
            if(annotations == null || processors == null)
                throw new IllegalStateException("must add annos & processors before push");
            getChain().links.add(new Link(annotations,processors,runner));
            annotations = null;
            processors = null;
            runner = defaultRunner;
        }

        @Override
        public CompleteBuilder addProcessors(PackageProcessor... procs) {
            if (annotations == null) throw new IllegalStateException("must add annos before processors");
            if(this.processors == null){
                this.processors = new HashSet<PackageProcessor>();
            }
            this.processors.addAll(Arrays.asList(procs));
            return this;
        }

        @Override
        public CompleteBuilder withRunner(LinkRunner runner) {
            this.runner = runner;
            return this;
        }
    }

    public static NewBuilder builder(){
        return new Builder();
    }

    public static interface NewBuilder {
        public AnnoBuilder addAnnos(String ... anos);
    }

    public static interface AnnoBuilder extends NewBuilder {
        public CompleteBuilder addProcessors(PackageProcessor ... processors);
    }

    public static interface CompleteBuilder extends AnnoBuilder {
        public CompleteBuilder withRunner(LinkRunner runner);
        public ProcessorChain build();
    }
}
