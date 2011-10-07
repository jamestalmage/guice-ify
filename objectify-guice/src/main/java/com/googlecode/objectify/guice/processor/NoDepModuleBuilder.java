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
import java.util.List;
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
public class NoDepModuleBuilder extends EntitiesAndConvertersProcessor {
    @Override
    protected Collection<? extends PackageProcessor> createConverterProcessors() {
        return Arrays.asList(
                new MyPackageProcessor()
        );
    }

    @Override
    protected Collection<? extends PackageProcessor> createEntityProcessors() {
        return createConverterProcessors();
    }

    static class MyPackageProcessor implements PackageProcessor {
        @Override
        public void processPackage(final Set<Entities.Info> entities, final String pkg, final ProcessorContext fetcher) {
            final String className = uniqueNameFromPackage(pkg, "NoDepModule");

            fetcher.getPrintWriter(pkg + "." + className, null, new Callback<PrintWriter>() {
                @Override
                public void call(PrintWriter out) throws Exception {
                    printClassHeader(out, pkg, className, "com.google.inject.AbstractModule");

                    out.println("  @Override");
                    out.println("  public void configure() {");
                    out.println("    requestInjection(new Object(){");
                    out.println("      @com.google.inject.Inject");
                    out.println("      public void setFactory(com.googlecode.objectify.ObjectifyFactory fact){");

                    for (String entity : Entities.stripNames(entities,false)) {
                        out.println("        fact.register(" + entity + ".class);");
                    }

                    Set<String> converterPackages =  fetcher.getAttribute(CONVERTER_KEY);
                    if(converterPackages.contains(pkg)){
                        Set<Entities.Info> converterInfo = fetcher.getAttribute(CONVERTER_KEY + ":" + pkg);
                        if(converterInfo != null){
                            out.println();
                            out.println("        com.googlecode.objectify.impl.conv.Conversions conversions = fact.getConversions();");
                            out.println();
                            for (String converter : Entities.stripNames(converterInfo, false)) {
                                out.println("        conversions.add(new " +  converter+ "());");
                            }
                        }
                        converterPackages.remove(pkg);
                    }

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
