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

package com.googlecode.objectify.guice;

import com.google.inject.*;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import static java.util.logging.Level.FINEST;

/**
 * User: jamestalmage
 * Date: 10/4/11
 * Time: 8:48 PM
 */
public abstract class ObjectifyListenerModule extends AbstractModule{

    private static Logger logger = Logger.getLogger(ObjectifyListenerModule.class.getName());
    static {
        logger.setLevel(FINEST);
    }

    @Override
    protected final void configure() {
        install(new ObjectifyFactoryListenerModule());
        configurePackages();
    }

    protected abstract void configurePackages();

    protected void bindPackages(String ... packages){
        for (String pkg : packages) {
            bindPackage(pkg);
        }
    }

    protected List<String> getSuffixes(){
        return Arrays.asList(ObjectifyEntities.SUFFIX,AbstractQueryModule.SUFFIX);
    }

    protected void bindPackage(String pkg) {
        for(String suffix: getSuffixes()){
            bindOrInstallUniquelyNamed(pkg,suffix);
        }
    }

    protected void bindOrInstallUniquelyNamed(String pkg,String suffix){
        Class<?> clzz = ClassNameUtils.loadUniquelyNamedClass(pkg,suffix);
        if(clzz != null){
            if(Module.class.isAssignableFrom(clzz)){
                try {
                    logger.info("INSTALLING: " + clzz.getName());
                    install((Module) clzz.newInstance());
                } catch (Exception e) {
                    addError("Trouble instantiating: " + clzz.getName(),e);
                }
            }
            else {
                logger.info("BINDING: " + clzz.getName());
                bind(clzz);
            }
        }
        else {
            System.out.println(ClassNameUtils.uniqeFullNameFromPackage(pkg,suffix) + " NOT FOUND");
        }
    }
}
