package org.jqassistant.plugin.asciidocreport.parser;

import java.util.Map;

import com.buschmais.jqassistant.core.rule.api.model.*;

import org.junit.jupiter.api.Test;

import static com.buschmais.jqassistant.core.rule.api.model.Severity.BLOCKER;
import static java.util.Optional.of;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.mockito.Mockito.doReturn;

class SeverityTest extends AbstractRuleParserTest {

    @Test
    void asciidocSeverity() throws Exception {
        RuleSet ruleSet = readRuleSet("/parser/severity.adoc");
        verifySeverities(ruleSet, "test:GroupWithoutSeverity", null, "test:Concept", null, "test:Constraint", null);
        verifySeverities(ruleSet, "test:GroupWithSeverity", BLOCKER, "test:Concept", null, "test:Constraint", null);
        verifySeverities(ruleSet, "test:GroupWithOverridenSeverities", BLOCKER, "test:Concept", Severity.CRITICAL, "test:Constraint",
                Severity.CRITICAL);
    }

    private void verifySeverities(RuleSet ruleSet, String groupId, Severity expectedGroupSeverity, String conceptId, Severity expectedIncludedConceptSeverity,
            String constraintId, Severity expectedIncludedConstraintSeverity) throws RuleException {
        assertThat(ruleSet.getConceptBucket().getIds(), hasItems(conceptId));
        assertThat(ruleSet.getConstraintBucket().getIds(), hasItems(constraintId));
        GroupsBucket groups = ruleSet.getGroupsBucket();
        // Group without any severity definition
        Group group = groups.getById(groupId);
        assertThat(group, notNullValue());
        assertThat(group.getSeverity(), equalTo(expectedGroupSeverity));
        Map<String, Severity> includedConcepts = group.getConcepts();
        assertThat(includedConcepts.containsKey(conceptId), equalTo(true));
        assertThat(includedConcepts.get(conceptId), equalTo(expectedIncludedConceptSeverity));
        Map<String, Severity> includedConstraints = group.getConstraints();
        assertThat(includedConstraints.containsKey(constraintId), equalTo(true));
        assertThat(includedConstraints.get(constraintId), equalTo(expectedIncludedConstraintSeverity));
    }

    @Test
    void defaultSeverity() throws RuleException {
        doReturn(of(Severity.CRITICAL)).when(rule).defaultConceptSeverity();
        doReturn(of(Severity.CRITICAL)).when(rule).defaultConstraintSeverity();
        doReturn(of(Severity.CRITICAL)).when(rule).defaultGroupSeverity();
        RuleSet ruleSet = readRuleSet("/parser/severity.adoc");
        verifyDefaultSeverities(ruleSet, Severity.CRITICAL);
    }

    private void verifyDefaultSeverities(RuleSet ruleSet, Severity defaultSeverity) throws RuleException {
        Group groupWithoutSeverity = ruleSet.getGroupsBucket().getById("test:GroupWithoutSeverity");
        assertThat(groupWithoutSeverity.getSeverity(), equalTo(defaultSeverity));
        Group groupWithSeverity = ruleSet.getGroupsBucket().getById("test:GroupWithSeverity");
        assertThat(groupWithSeverity.getSeverity(), equalTo(BLOCKER));
        Concept concept = ruleSet.getConceptBucket().getById("test:Concept");
        assertThat(concept.getSeverity(), equalTo(defaultSeverity));
        Constraint constraint = ruleSet.getConstraintBucket().getById("test:Constraint");
        assertThat(constraint.getSeverity(), equalTo(defaultSeverity));
    }

    @Test
    void asciidocRuleDefaultSeverity() throws RuleException {
        RuleSet ruleSet = readRuleSet("/parser/severity.adoc");
        verifyRuleDefaultSeverity(ruleSet);
    }

    private void verifyRuleDefaultSeverity(RuleSet ruleSet) throws RuleException {
        Group groupWithoutSeverity = ruleSet.getGroupsBucket().getById("test:GroupWithoutSeverity");
        assertThat(groupWithoutSeverity.getSeverity(), nullValue());
        Concept concept = ruleSet.getConceptBucket().getById("test:Concept");
        assertThat(concept.getSeverity(), equalTo(Severity.MINOR));
        Constraint constraint = ruleSet.getConstraintBucket().getById("test:Constraint");
        assertThat(constraint.getSeverity(), equalTo(Severity.MAJOR));
    }

}
