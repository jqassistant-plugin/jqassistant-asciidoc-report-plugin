package org.jqassistant.plugin.asciidocreport.parser;

import java.util.Properties;
import java.util.Set;

import com.buschmais.jqassistant.core.rule.api.model.ExecutableRule;
import com.buschmais.jqassistant.core.rule.api.model.Report;
import com.buschmais.jqassistant.core.rule.api.model.RuleSet;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsCollectionContaining.hasItem;

/**
 * Verifies reading the report part of rules.
 */
class ReportTest extends AbstractRuleParserTest {

    @Test
    void asciidocReport() throws Exception {
        RuleSet ruleSet = readRuleSet("/parser/report.adoc");
        verifyRule(ruleSet.getConceptBucket()
            .getById("test:Concept"));
        verifyRule(ruleSet.getConstraintBucket()
            .getById("test:Constraint"));
    }

    private void verifyRule(ExecutableRule rule) {
        assertThat("Expecting a rule", rule, notNullValue());
        Report report = rule.getReport();
        Set<String> selection = report.getSelectedTypes();
        assertThat("Expecting a selection of one report.", selection.size(), equalTo(1));
        assertThat("Expecting a custom report type.", selection, hasItem("custom"));
        assertThat("Expecting a primary column.", report.getPrimaryColumn(), equalTo("n"));
        Properties properties = report.getProperties();
        assertThat("Expecting two properties.", properties.size(), equalTo(2));
        assertThat("Expecting value1 for key1.", properties.getProperty("key1"), equalTo("value1"));
        assertThat("Expecting value2 for key2.", properties.getProperty("key2"), equalTo("value2"));
    }

}
