package com.googlecode.objectify.guice.processor;

import javax.lang.model.element.Element;
import java.io.PrintWriter;

/**
 * User: jamestalmage
 * Date: 10/5/11
 * Time: 9:11 PM
 */
public interface PrintWriterFetcher {
    void getPrintWriter(CharSequence src, Element element, Callback<? super PrintWriter> callback);
}
