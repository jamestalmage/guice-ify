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

import com.googlecode.objectify.guice.ClassNameUtils;

import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import static javax.lang.model.SourceVersion.RELEASE_6;

/**
 * User: jamestalmage
 * Date: 6/2/11
 * Time: 10:06 PM
 */
@SupportedAnnotationTypes({"com.googlecode.objectify.annotation.Entity","javax.persistence.Entity"})
@SupportedSourceVersion(RELEASE_6)
public class ObjectifyRegistryProcessor extends ProcessPerPackageProcessor {


    private List<MyPackageProcessor> processors = Arrays.asList(new MyPackageProcessor());

    @Override
    protected Iterable<? extends PackageProcessor> getProcessors() {
        return processors;
    }

    static class MyPackageProcessor implements PackageProcessor {
        @Override
        public void processPackage(final Entities entities,final String pkg,final PrintWriterFetcher fetcher) {

            final String className = ClassNameUtils.uniqueNameFromPackage(pkg, "ObjectifyRegistry");
            String fullName = pkg + "." + className;
            fetcher.getPrintWriter(fullName, null, new Callback<PrintWriter>() {
                @Override
                public void call(PrintWriter out) throws Exception {

                    WriterUtils.printClassHeader(out, pkg, className, null);

                    out.println("  public static void registerToFactory(com.googlecode.objectify.ObjectifyFactory fact){");
                    final List<String> list = Entities.stripNames(entities.entitiesInPackage(pkg), false);
                    for (String entity : list) {
                        out.println("    fact.register(" + entity + ".class);");
                    }
                    out.println("  }");
                    out.println();
                    out.println("  public static void register(){");
                    out.println("    registerToFactory(com.googlecode.objectify.ObjectifyService.factory());");
                    out.println("  }");
                    out.println();
                    out.println("}");

                }
            });
        }

    }
}
