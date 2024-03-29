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

import java.util.Arrays;
import java.util.Collection;

import static javax.lang.model.SourceVersion.RELEASE_6;

/**
 * User: jamestalmage
 * Date: 10/5/11
 * Time: 9:44 PM
 */
@SupportedAnnotationTypes({"com.googlecode.objectify.annotation.Entity","javax.persistence.Entity","com.googlecode.objectify.guice.IsConverter"})
@SupportedSourceVersion(RELEASE_6)
public class CombinedBuilder extends EntitiesAndConvertersBuilder {

    @Override
    protected Collection<? extends Processor> createProcessors() {
        return Arrays.asList(
                new ObjectifyRegistryProcessor(),
                new NoDepModuleBuilder.MyPackageProcessor(),
                new ObjectifyModuleBuilder.EntityPkgProcessor()
        );
    }

    @Override
    protected ProcessedTracker createTracker() {
        return ProcessedTrackerImpl.identityTracker();
    }
}
