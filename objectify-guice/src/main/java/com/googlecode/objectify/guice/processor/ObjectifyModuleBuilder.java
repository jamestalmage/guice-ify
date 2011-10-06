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

import com.googlecode.objectify.guice.AbstractQueryModule;
import com.googlecode.objectify.guice.ClassNameUtils;

import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
public class ObjectifyModuleBuilder extends ProcessPerPackageProcessor{
    @Override
    protected Iterable<? extends PackageProcessor> getProcessors() {
        return null;
    }
    Set<String> converterPackages = new HashSet<String>();

    PackageProcessor converterCollector = new PackageProcessor() {
        @Override
        public void processPackage(Entities dontUse, String pkg, PrintWriterFetcher fetcher) {
            converterPackages.add(pkg);
        }
    };

    PackageProcessor outputProcessor = new PackageProcessor() {
        @Override
        public void processPackage(final Entities entities, final String pkg, PrintWriterFetcher fetcher) {

            final String className = ClassNameUtils.uniqueNameFromPackage(pkg, "ObjectifyModule");
            fetcher.getPrintWriter(pkg + "." + className, null, new Callback<PrintWriter>() {
                @Override
                public void call(PrintWriter out) throws Exception {
                    out.println("package " + pkg + ";");
                    out.println();
                    out.println("public class " + className + " extends com.googlecode.objectify.guice.AbstractQueryModule{");
                    out.println();
                    out.println("  @Override");
                    out.println("  protected void config() {");
                    out.println("    install(new com.googlecode.objectify.guice.ObjectifyFactoryListenerModule());");
                    out.print("    com.googlecode.objectify.guice.ObjectifyFactoryListenerModule.bindEntities(binder()");
                    for(String s: Entities.stripNames(entities.entitiesInPackage(pkg),true)){
                        out.println(",");
                        out.print("      ");
                        out.print(s);
                        out.print(".class");
                    }
                    out.println();
                    out.println("    );");

                    for (String s : Entities.stripNames(entities.entitiesInPackage(pkg), true)) {
                        out.println("    bindQuery(new com.google.inject.TypeLiteral<com.googlecode.objectify.Query<" + s + ">>(){}," + s + ".class);");
                    }

                    final Set<Entities.Info> converterInfo = converters.entitiesInPackage(pkg);
                    if(!converterInfo.isEmpty())   {
                        out.print("    com.googlecode.objectify.guice.ObjectifyFactoryListenerModule.bindConverters(binder()");
                        for(String s : Entities.stripNames(converterInfo,true)){
                            out.println(",");
                            out.print("      ");
                            out.print(s);
                            out.print(".class");
                        }
                    out.println();
                    out.println("    );");
                    }



                    out.println("  }");
                    out.println();

                    out.println("}");
                }
            });
        }
    };

    Entities converters;
    @Override
    void postprocessEntities() {
        Entities javaxEntities = entitiesMap.get("javax.persistence.Entity");
        Entities ofyEntities = entitiesMap.get("com.googlecode.objectify.annotation.Entity");
        Entities entities = Entities.merge(javaxEntities,ofyEntities);
        converters = entitiesMap.get("com.googlecode.objectify.guice.IsConverter");

        postprocessEntities(converters,Arrays.asList(converterCollector));
        postprocessEntities(entities,Arrays.asList(outputProcessor));
    }
}
