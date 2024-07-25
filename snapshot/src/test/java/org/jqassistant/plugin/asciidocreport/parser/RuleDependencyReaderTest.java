package org.jqassistant.plugin.asciidocreport.parser;

import com.buschmais.jqassistant.core.rule.api.model.Concept;
import com.buschmais.jqassistant.core.rule.api.model.Constraint;
import com.buschmais.jqassistant.core.rule.api.model.RuleException;
import com.buschmais.jqassistant.core.rule.api.model.RuleSet;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsCollectionContaining.hasItem;

/**
 * Verifies reading dependency information by rule set readers
 */
class RuleDependencyReaderTest extends AbstractRuleParserTest {

    @Test
    void ruleDependencies() throws RuleException {
        RuleSet ruleSet = readRuleSet("/parser/rule-dependencies.adoc");
        // Concepts
        Concept conceptWithOptionalDependency = ruleSet.getConceptBucket()
            .getById("test:ConceptWithOptionalDependency");
        assertThat(conceptWithOptionalDependency, notNullValue());
        assertThat(conceptWithOptionalDependency.getRequiresConcepts()
            .get("test:Concept1"), equalTo(true));
        Concept conceptWithRequiredDependency = ruleSet.getConceptBucket()
            .getById("test:ConceptWithRequiredDependency");
        assertThat(conceptWithRequiredDependency, notNullValue());
        assertThat(conceptWithRequiredDependency.getRequiresConcepts()
            .get("test:Concept1"), equalTo(false));
        Concept conceptWithMixedDependencies = ruleSet.getConceptBucket()
            .getById("test:ConceptWithMixedDependencies");
        assertThat(conceptWithMixedDependencies, notNullValue());
        assertThat(conceptWithMixedDependencies.getRequiresConcepts()
            .get("test:Concept1"), equalTo(true));
        assertThat(conceptWithMixedDependencies.getRequiresConcepts()
            .get("test:Concept2"), equalTo(false));
        assertThat(conceptWithMixedDependencies.getRequiresConcepts()
            .get("test:Concept3"), equalTo(null));
        Concept providingConcept = ruleSet.getConceptBucket()
            .getById("test:ProvidingConcept");
        assertThat(providingConcept.getProvidedConcepts(), hasItem("test:Concept1"));
        assertThat(providingConcept.getProvidedConcepts(), hasItem("test:Concept2"));
        // Constraints
        Constraint constraintWithOptionalDependency = ruleSet.getConstraintBucket()
            .getById("test:ConstraintWithOptionalDependency");
        assertThat(constraintWithOptionalDependency, notNullValue());
        assertThat(constraintWithOptionalDependency.getRequiresConcepts()
            .get("test:Concept1"), equalTo(true));
        Constraint constraintWithRequiredDependency = ruleSet.getConstraintBucket()
            .getById("test:ConstraintWithRequiredDependency");
        assertThat(constraintWithRequiredDependency, notNullValue());
        assertThat(constraintWithRequiredDependency.getRequiresConcepts()
            .get("test:Concept1"), equalTo(false));
        Constraint constraintWithMixedDependencies = ruleSet.getConstraintBucket()
            .getById("test:ConstraintWithMixedDependencies");
        assertThat(constraintWithMixedDependencies, notNullValue());
        assertThat(constraintWithMixedDependencies.getRequiresConcepts()
            .get("test:Concept1"), equalTo(true));
        assertThat(constraintWithMixedDependencies.getRequiresConcepts()
            .get("test:Concept2"), equalTo(false));
        assertThat(constraintWithMixedDependencies.getRequiresConcepts()
            .get("test:Concept3"), equalTo(null));
    }

}
