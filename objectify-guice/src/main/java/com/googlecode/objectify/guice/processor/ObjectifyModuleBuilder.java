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


import com.googlecode.objectify.annotation.Entity;

import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;

import java.io.PrintWriter;
import java.util.*;

import static com.googlecode.objectify.guice.processor.WriterUtils.*;
import static javax.lang.model.SourceVersion.RELEASE_6;

/**
 * User: jamestalmage
 * Date: 10/5/11
 * Time: 10:24 PM
 */
@SupportedAnnotationTypes({
        "com.googlecode.objectify.annotation.Entity",
        "javax.persistence.Entity",
        "com.googlecode.objectify.guice.IsConverter"})
@SupportedSourceVersion(RELEASE_6)
public class ObjectifyModuleBuilder extends EntitiesAndConvertersBuilder {

    @Override
    protected Collection<? extends Processor> createProcessors() {
        return Arrays.asList(new EntityPkgProcessor());
    }


    static class EntityPkgProcessor implements Processor {

        @Override
        public void process(final ProcessorInfo info) {
            final String pkg = info.getPackageName();

            final String className = uniqueNameFromPackage(pkg, "ObjectifyModule");
            ProcessorContext fetcher = info.getContext();

            fetcher.getPrintWriter(pkg + "." + className, null, new Callback<PrintWriter>() {
                @Override
                public void call(PrintWriter out) throws Exception {
                    printClassHeader(out, pkg, className, "com.google.inject.AbstractModule");
                    final List<String> names = Entities.stripNames(info.getEntities(), false);

                    out.println("  @Override");
                    out.println("  protected void configure() {");
                    out.println("    install(new com.googlecode.objectify.guice.ObjectifyFactoryListenerModule());");
                    if(!names.isEmpty()){
                        out.println("    com.googlecode.objectify.guice.ObjectifyFactoryListenerModule.bindEntities(binder(),");
                        join(out, "      ", ".class", names);
                        out.println("    );");
                    }

                    Set<Entities.Info> converters = info.getConverters();
                    if(!converters.isEmpty())   {
                        out.println("    com.googlecode.objectify.guice.ObjectifyFactoryListenerModule.bindConverters(binder(),");
                        join(out, "      ", ".class", Entities.stripNames(converters, false));
                        out.println("    );");
                    }
                    out.println("  }");
                    out.println();

                    for (String name : names) {
                        printProvidesQueryMethod(out, name);
                    }
                    out.println("}");
                }
            });
        }
    }

    @Override
    protected ProcessedTracker createTracker() {
        return ProcessedTrackerImpl.perProcessorClass();
    }

}
