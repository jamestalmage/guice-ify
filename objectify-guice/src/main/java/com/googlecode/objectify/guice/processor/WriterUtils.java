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
import java.util.Arrays;
import java.util.Iterator;

/**
 * User: jamestalmage
 * Date: 10/6/11
 * Time: 2:22 AM
 */
class WriterUtils {
    static void print(PrintWriter out, String string){
        if(unemptyString(string)) out.print(string);
    }
    static boolean unemptyString(String string){
        return string !=null && !string.isEmpty();
    }
    static void join(PrintWriter out, String prefix, String suffix, Iterable vals){
        join(out,prefix,suffix,",",true,vals);
    }
    static void join(PrintWriter out, String prefix, String suffix, Object ... vals){
        join(out,prefix,suffix,",",true,Arrays.asList(vals));
    }
    static void join(PrintWriter out, String prefix, String suffix, String delim, boolean newLineDelim, Object ... vals){
        join(out,prefix,suffix,delim,newLineDelim, Arrays.asList(vals));
    }
    static void join(PrintWriter out, String prefix, String suffix, String delim, boolean newLineDelim, Iterable vals){
        Iterator iter = vals.iterator();
        while (iter.hasNext()){
            Object val = iter.next();
            print(out,prefix);
            out.print(val);
            print(out,suffix);
            if(iter.hasNext()){
                print(out,delim);
            }
            if(newLineDelim) out.println();
        }
    }

    static void printClassHeader(PrintWriter out, String pkg, String className, String extnds, String ... implmts){
        if(unemptyString(pkg)){
            out.print("package ");
            out.print(pkg);
            out.println(";");
            out.println();
        }
        out.println("/** Generated by guice-ify */");
        out.print("public final class ");
        out.print(className);
        if(unemptyString(extnds)){
            out.print(" extends ");
            out.print(extnds);
        }
        if(implmts != null && implmts.length > 0){
            out.print(" implements");
            for(int i= 0; i < implmts.length; i++){
                if(i != 0) out.print(",");
                out.print(" ");
                out.print(implmts[i]);
            }
        }
        out.println(" {");
        out.println();
    }

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

    static String capitalize(String s){
        StringBuilder sb = new StringBuilder();
        sb.append(Character.toUpperCase(s.charAt(0)));
        sb.append(s.substring(1));
        return sb.toString();
    }

    static String uniqueNameFromPackage(String pkg, String suffix){
        return capitalize(pkg.substring(pkg.lastIndexOf(".") + 1))+suffix;
    }
}
