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

import javax.lang.model.element.Element;
import java.io.PrintWriter;
import java.util.Set;

/**
 * User: jamestalmage
 * Date: 10/5/11
 * Time: 9:11 PM
 */
interface ProcessorContext {
    void getPrintWriter(CharSequence src, Element element, Callback<? super PrintWriter> callback);

    public <T> T getAttribute(Object key);

    public <T> T setAttribute(Object key, T value);

    public <T> T getAttribute(Object key, T defaultValue);

    Entities mergeAll();

    Set<String> annotations();

    Entities createMerged(Iterable<String> key);

    ProcessedTracker getTracker();
}
