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

import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import static com.googlecode.objectify.guice.processor.WriterUtils.*;
import static javax.lang.model.SourceVersion.RELEASE_6;

/**
 * User: jamestalmage
 * Date: 10/7/11
 * Time: 2:27 AM
 */
@SupportedAnnotationTypes({
        "com.googlecode.objectify.annotation.Entity",
        "javax.persistence.Entity",
        "com.googlecode.objectify.guice.IsConverter"})
@SupportedSourceVersion(RELEASE_6)
public class NoDepModuleBuilder extends EntitiesAndConvertersBuilder {

    @Override
    protected Collection<? extends Processor> createProcessors() {
        return Arrays.asList(new ObjectifyRegistryProcessor(), new MyPackageProcessor());
    }

    static class MyPackageProcessor implements Processor {
        @Override
        public void process(final ProcessorInfo info) {
            final String pkg = info.getPackageName();
            ProcessorContext fetcher = info.getContext();

            final String className = uniqueNameFromPackage(pkg, "NoDepModule");

            final String registryName = uniqueNameFromPackage(pkg, "ObjectifyRegistry");

            fetcher.getPrintWriter(pkg + "." + className, null, new Callback<PrintWriter>() {
                @Override
                public void call(PrintWriter out) throws Exception {
                    final Set<Entities.Info> entities = info.getEntities();
                    //final Set<Entities.Info> converters = info.getConverters();
                    printClassHeader(out, pkg, className, "com.google.inject.AbstractModule");

                    out.println("  @Override");
                    out.println("  public void configure() {");
                    out.println("    requestInjection(new Object(){");
                    out.println("      @com.google.inject.Inject");
                    out.println("      public void setFactory(com.googlecode.objectify.ObjectifyFactory fact){");
                    out.print("        ");
                    out.print(registryName);
                    out.println(".register(fact);");

                    /*for (String entity : Entities.stripNames(entities,false)) {
                        out.println("        fact.register(" + entity + ".class);");
                    }

                    if(!converters.isEmpty()){
                        out.println();
                        out.println("        com.googlecode.objectify.impl.conv.Conversions conversions = fact.getConversions();");
                        out.println();
                        for (String converter : Entities.stripNames(converters, false)) {
                            out.println("        conversions.add(new " +  converter+ "());");
                        }
                    }   */

                    out.println("      }");
                    out.println("    });");
                    out.println("  }");
                    out.println();

                    for (String name : Entities.stripNames(entities, false)) {
                        printProvidesQueryMethod(out, name);
                    }

                    printModuleEqualsAndHashCode(out, className);

                    out.println("}");
                }
            });
        }
    }

    @Override
    protected ProcessedTracker createTracker() {
        return ProcessedTrackerImpl.identityTracker();
    }
}
