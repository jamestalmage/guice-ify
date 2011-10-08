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

import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

import static com.googlecode.objectify.guice.processor.EntitiesAndConvertersBuilder.CONVERTER_KEY;
import static com.googlecode.objectify.guice.processor.WriterUtils.printClassHeader;
import static com.googlecode.objectify.guice.processor.WriterUtils.uniqueNameFromPackage;

/**
 * User: jamestalmage
 * Date: 6/2/11
 * Time: 10:06 PM
 */
public class ObjectifyRegistryProcessor implements EntitiesAndConvertersBuilder.Processor {

    @Override
    public void process(final EntitiesAndConvertersBuilder.ProcessorInfo info) {
        final String pkg = info.getPackageName();
        ProcessorContext fetcher = info.getContext();
        final String className = uniqueNameFromPackage(pkg, "ObjectifyRegistry");
        String fullName = pkg + "." + className;
        fetcher.getPrintWriter(fullName, null, new Callback<PrintWriter>() {
            @Override
            public void call(PrintWriter out) throws Exception {

                printClassHeader(out, pkg, className, null);

                out.println("  public static void registerEntities(com.googlecode.objectify.ObjectifyFactory fact){");
                final List<String> list = Entities.stripNames(info.getEntities(), false);
                for (String entity : list) {
                    out.println("    fact.register(" + entity + ".class);");
                }
                out.println("  }");
                out.println();

                out.println("  public static void registerConverters(com.googlecode.objectify.ObjectifyFactory fact){");
                out.println("    com.googlecode.objectify.impl.conv.Conversions conversions = fact.getConversions();");

                for (String converter : Entities.stripNames(info.getConverters(), false)) {
                    out.println("    conversions.add(new " +  converter+ "());");
                }
                out.println("  }");
                out.println();


                out.println("  public static void registerEntities(){");
                out.println("    registerEntities(com.googlecode.objectify.ObjectifyService.factory());");
                out.println("  }");
                out.println();


                out.println("  public static void registerConverters(){");
                out.println("    registerConverters(com.googlecode.objectify.ObjectifyService.factory());");
                out.println("  }");
                out.println();

                out.println("  public static void register(com.googlecode.objectify.ObjectifyFactory fact){");
                out.println("    registerEntities(fact);");
                out.println("    registerConverters(fact);");
                out.println("  }");
                out.println();


                out.println("  public static void register(){");
                out.println("    register(com.googlecode.objectify.ObjectifyService.factory());");
                out.println("  }");
                out.println();

                out.println("}");

            }
        });
    }
}
