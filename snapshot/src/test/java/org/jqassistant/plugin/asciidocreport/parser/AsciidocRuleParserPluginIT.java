package org.jqassistant.plugin.asciidocreport.parser;

import java.io.File;
import java.util.Map;

import com.buschmais.jqassistant.core.report.api.model.Result;
import com.buschmais.jqassistant.core.rule.api.model.Concept;
import com.buschmais.jqassistant.core.rule.api.model.RuleException;
import com.buschmais.jqassistant.core.test.plugin.AbstractPluginIT;

import org.junit.jupiter.api.Test;

import static com.buschmais.jqassistant.core.report.api.model.Result.Status.SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;

public class AsciidocRuleParserPluginIT extends AbstractPluginIT {

    @Test
    void concept() throws RuleException {
        Result<Concept> result = applyConcept("test:concept");
        assertThat(result.getStatus()).isEqualTo(SUCCESS);
        assertThat(result.getRows()).hasSize(1);
        File expectedIndexHtml = new File("target/jqassistant/report/asciidoc/index.html");
        assertThat(expectedIndexHtml).exists();
    }

    @Test
    void ruleFromPlugin() throws RuleException {
        executeGroup("custom");
        Map<String, Result<Concept>> conceptResults = reportPlugin.getConceptResults();
        assertThat(conceptResults).containsKey("custom:CSVReport");
        assertThat(conceptResults).containsKey("custom:CSVReportRelative");
        assertThat(new File("target/jqassistant/report/asciidoc/custom/index.html")).exists();
        assertThat(new File("target/jqassistant/report/csv/custom_CSVReport.csv")).exists();
        assertThat(new File("target/jqassistant/report/csv/custom_CSVReportRelative.csv")).exists();
    }
}
