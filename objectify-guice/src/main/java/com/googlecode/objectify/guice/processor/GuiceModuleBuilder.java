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

import static javax.lang.model.SourceVersion.RELEASE_6;

/**
 * User: jamestalmage
 * Date: 6/2/11
 * Time: 10:13 PM
 */
@SupportedAnnotationTypes({"com.googlecode.objectify.annotation.Entity","javax.persistence.Entity"})
@SupportedSourceVersion(RELEASE_6)
public class GuiceModuleBuilder extends ProcessPerPackageProcessor{

    @Override
    protected void processPackage(final String pkg) {
        final String className = uniqueNameFromPackage(pkg,"QueryModule");

        getPrintWriter(pkg+"."+className,null,new Callback<PrintWriter>() {
            @Override
            public void call(PrintWriter out) throws Exception {
                out.println("package " + pkg + ";");
                out.println();
                out.println("public class " + className + " extends com.googlecode.objectify.guice.AbstractQueryModule{");
                out.println();
                out.println("  @Override");
                out.println("  protected void config() {");
                for (String s : Entities.stripNames(entities.entitiesInPackage(pkg),true)) {
                    out.println("    bindQuery(new com.google.inject.TypeLiteral<com.googlecode.objectify.Query<"+s+">>(){},"+s+".class);");
                }
                out.println("  }");
                out.println();

                out.println("}");
            }
        });
    }
}
