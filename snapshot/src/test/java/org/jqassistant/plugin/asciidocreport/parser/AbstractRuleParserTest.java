package org.jqassistant.plugin.asciidocreport.parser;

import java.net.URL;

import com.buschmais.jqassistant.core.rule.api.configuration.Rule;
import com.buschmais.jqassistant.core.rule.api.model.RuleException;
import com.buschmais.jqassistant.core.rule.api.model.RuleSet;
import com.buschmais.jqassistant.core.rule.api.source.RuleSource;
import com.buschmais.jqassistant.core.rule.api.source.UrlRuleSource;
import com.buschmais.jqassistant.core.rule.impl.reader.RuleParser;

import org.jqassistant.plugin.asciidocreport.AsciidocRuleParserPlugin;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

@ExtendWith(MockitoExtension.class)
abstract class AbstractRuleParserTest {

    protected static AsciidocRuleParserPlugin asciidocRuleParserPlugin;

    @Mock
    protected Rule rule;

    @BeforeAll
    static void initialize() throws RuleException {
        asciidocRuleParserPlugin = new AsciidocRuleParserPlugin();
        asciidocRuleParserPlugin.initialize();
    }

    @AfterAll
    static void destroy() {
        asciidocRuleParserPlugin.destroy();
    }

    @BeforeEach
    final void setUp() {
        asciidocRuleParserPlugin.configure(rule);
    }

    protected RuleSet readRuleSet(String resource) throws RuleException {
        RuleParser ruleParser = new RuleParser(singletonList(asciidocRuleParserPlugin));
        URL url = AsciidocRuleParserPluginTest.class.getResource(resource);
        assertThat("Cannot read resource URL:" + resource, url, notNullValue());
        RuleSource ruleSource = new UrlRuleSource(url);
        return ruleParser.parse(singletonList(ruleSource));
    }

}
