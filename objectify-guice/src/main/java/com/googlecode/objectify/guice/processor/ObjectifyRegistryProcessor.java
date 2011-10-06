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
import java.util.List;
import java.util.Set;

import static com.googlecode.objectify.guice.processor.WriterUtils.printClassHeader;
import static com.googlecode.objectify.guice.processor.WriterUtils.uniqueNameFromPackage;
import static javax.lang.model.SourceVersion.RELEASE_6;

/**
 * User: jamestalmage
 * Date: 6/2/11
 * Time: 10:06 PM
 */
@SupportedAnnotationTypes({"com.googlecode.objectify.annotation.Entity","javax.persistence.Entity"})
@SupportedSourceVersion(RELEASE_6)
public class ObjectifyRegistryProcessor extends EntityProcessor {


    @Override
    protected ProcessedTracker createTracker() {
        return ProcessedTrackerImpl.perProcessorClass();
    }

    @Override
    protected ProcessorChain getProcessors() {
        return ProcessorChain.builder()
                .addAnnos("com.googlecode.objectify.annotation.Entity","javax.persistence.Entity")
                .addProcessors(new MyPackageProcessor())
                .build();
    }

    static class MyPackageProcessor implements PackageProcessor {
        @Override
        public void processPackage(final Set<Entities.Info> infoSet,final String pkg,final ProcessorContext fetcher) {

            final String className = uniqueNameFromPackage(pkg, "ObjectifyRegistry");
            String fullName = pkg + "." + className;
            fetcher.getPrintWriter(fullName, null, new Callback<PrintWriter>() {
                @Override
                public void call(PrintWriter out) throws Exception {

                    printClassHeader(out, pkg, className, null);

                    out.println("  public static void registerToFactory(com.googlecode.objectify.ObjectifyFactory fact){");
                    final List<String> list = Entities.stripNames(infoSet, false);
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
