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

/**
 * User: jamestalmage
 * Date: 10/6/11
 * Time: 2:22 AM
 */
class WriterUtils {
    static void printModuleEqualsAndHashCode(PrintWriter out, String className) {
        out.println("  @Override");
        out.println("  public boolean equals(Object obj){");
        out.print("    return obj instanceof ");
        out.print(className);
        out.println(";");
        out.println("  }");

        out.println("  @Override");
        out.println("  public int hashCode(){");
        out.print("    return ");
        out.print(className);
        out.println(".class.hashCode();");
        out.println("  }");
    }

    static void printProvidesQueryMethod(PrintWriter out, String name) {
       out.println("  @com.google.inject.Provides");
       out.print("  public com.googlecode.objectify.Query<");
       out.print(name);
       out.print("> get");
       out.print(name);
       out.println("Query(com.googlecode.objectify.Objectify ofy){");
       out.print("    return ofy.query(");
       out.print(name);
       out.println(".class);");
       out.println("  }");
       out.println();
   }
}
