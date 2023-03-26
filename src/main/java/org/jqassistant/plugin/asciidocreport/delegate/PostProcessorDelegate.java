package org.jqassistant.plugin.asciidocreport.delegate;

import org.asciidoctor.ast.Document;
import org.asciidoctor.extension.Postprocessor;

class PostProcessorDelegate extends Postprocessor {

    private final Postprocessor delegate;

    PostProcessorDelegate(Postprocessor delegate) {
        this.delegate = delegate;
    }

    @Override
    public String process(Document document, String output) {
        return delegate.process(document, output);
    }

}
