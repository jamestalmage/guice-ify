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

import javax.annotation.processing.*;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * User: jamestalmage
 * Date: 6/1/11
 * Time: 7:40 PM
 */
abstract class EntityProcessor extends AbstractProcessor implements ProcessorContext {

    ProcessingEnvironment env;
    //Entities entities = new Entities();
    Map<String,Entities> entitiesMap = new HashMap<String, Entities>();


    @Override
    public void init(ProcessingEnvironment env) {
        super.init(env);
        this.env = env;
        final SupportedAnnotationTypes supported = this.getClass().getAnnotation(SupportedAnnotationTypes.class);
        if(supported != null){
            for (String anno : supported.value()) {
                entitiesMap.put(anno,new Entities());
            }
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> typeElements, RoundEnvironment roundEnvironment) {
        if(!roundEnvironment.processingOver()){
            for (TypeElement currAnnotation : typeElements) {
                if(isEntityAnnotation(currAnnotation)) {
                    processEntities(currAnnotation,roundEnvironment.getElementsAnnotatedWith(currAnnotation));
                }
            }
            postprocessEntities();
        }
        return false;
    }


    void postprocessEntities() {
        getProcessors().run(this);
    }

    ProcessedTracker tracker = null;
    public ProcessedTracker getTracker(){
        if(tracker == null){
            tracker = createTracker();
        }
        return tracker;
    }

    protected abstract ProcessedTracker createTracker();

    ProcessorChain lazyChain;
    final ProcessorChain getProcessors(){
        if(lazyChain == null){
            lazyChain = createChain();
        }
        return lazyChain;
    }

    protected abstract ProcessorChain createChain();


    void processEntities(TypeElement currAnnotaion,Iterable<? extends Element> entities){
        for (Element entity : entities) {
            processEntity(currAnnotaion, entity);
        }
    }

    boolean isEntityAnnotation(TypeElement currAnnotation){
        return entitiesMap.containsKey(currAnnotation.getQualifiedName().toString());
    }

    private void processEntity(TypeElement annotation,Element entity) {
        entitiesMap.get(annotation.getQualifiedName().toString()).put(entity, processingEnv);
    }

    public PrintWriter getPrintWriter(CharSequence src,Element element) throws IOException {
        return new PrintWriter(new BufferedWriter(env.getFiler().createSourceFile(src,element).openWriter()));
    }

    @Override
    public void getPrintWriter(CharSequence src, Element element, Callback<? super PrintWriter> callback) {
        PrintWriter writer = null;
        try{
            writer = getPrintWriter(src,element);
            callback.call(writer);
        } catch (Exception e1) {
            e1.printStackTrace();
            env.getMessager().printMessage(Diagnostic.Kind.ERROR,e1.toString());
            throw new RuntimeException(e1);
        } finally {
            if(writer != null) writer.close();
        }
    }

    @Override
    public <T> T getAttribute(Object key) {
        return (T) attributes.get(key);
    }

    @Override
    public <T> T getAttribute(Object key, T defaultValue) {
        T att = getAttribute(key);
        if(att == null){
            att = defaultValue;
            setAttribute(key,att);
        }
        return att;
    }

    @Override
    public <T> T setAttribute(Object key, T value) {
        return (T) attributes.put(key,value);
    }

    Map attributes = new HashMap();

    @Override
    public Entities mergeAll() {
        return Entities.merge(entitiesMap.values());
    }

    @Override
    public Set<String> annotations(){
        return entitiesMap.keySet();
    }

    @Override
    public Entities createMerged(Iterable<String> key) {
        Set<Entities> entities = new HashSet<Entities>();
        for (String s : key) {
            final Entities e = entitiesMap.get(s);

            if(e == null){
                new NullPointerException().fillInStackTrace().printStackTrace();
                throw  new NullPointerException(s + " not found");

            }
            entities.add(e);
        }
        return Entities.merge(entities);
    }
}
