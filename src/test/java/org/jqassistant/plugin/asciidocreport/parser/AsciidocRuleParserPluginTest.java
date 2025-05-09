package org.jqassistant.plugin.asciidocreport.parser;

import java.util.Collections;
import java.util.Map;

import com.buschmais.jqassistant.core.rule.api.model.*;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.hamcrest.collection.IsEmptyCollection;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

class AsciidocRuleParserPluginTest extends AbstractRuleParserTest {

    @Test
    public void cypherRules() throws Exception {
        RuleSet ruleSet = readRuleSet("/parser/junit-without-assert.adoc");
        ConceptBucket concepts = ruleSet.getConceptBucket();
        assertThat(concepts.size(), equalTo(2));

        Concept concept1 = concepts.getById("junit4:TestClassOrMethod");
        assertThat(concept1.getId(), equalTo("junit4:TestClassOrMethod"));
        assertThat(concept1.getDescription(), CoreMatchers.containsString("labels them and their containing classes with `:Test` and `:Junit4`."));

        Executable<String> executable1 = (Executable<String>) concept1.getExecutable();
        assertThat(executable1, instanceOf(CypherExecutable.class));
        assertThat(executable1.getSource(), containsString("c:Test:Junit4, m:Test:Junit4"));
        assertThat(concept1.getRequiresConcepts()
            .keySet(), IsEmptyCollection.<String>empty());

        Concept concept2 = concepts.getById("junit4:AssertMethod");
        assertThat(concept2.getId(), containsString("junit4:AssertMethod"));
        assertThat(concept2.getDescription(), containsString("Labels all assertion methods declared by `org.junit.Assert` with `:Assert`."));

        Executable<String> executable2 = (Executable<String>) concept2.getExecutable();
        assertThat(executable2, instanceOf(CypherExecutable.class));
        assertThat(executable2.getSource(), containsString("and assertMethod.signature =~ 'void assert.*'"));
        assertThat(concept2.getRequiresConcepts()
            .keySet(), IsEmptyCollection.<String>empty());

        ConstraintBucket constraints = ruleSet.getConstraintBucket();
        assertThat(constraints.size(), equalTo(1));

        Constraint constraint = constraints.getById("junit4:TestMethodWithoutAssertion");
        assertThat(constraint.getId(), containsString("junit4:TestMethodWithoutAssertion"));
        assertThat(constraint.getDescription(), containsString("All test methods must perform assertions."));
        assertEquals("junit4:TestMethodWithoutAssertion", constraint.getId());
        assertEquals("All test methods must perform assertions.", constraint.getDescription());

        Executable<String> constraintExecutable = (Executable<String>) constraint.getExecutable();
        assertThat(constraintExecutable, instanceOf(CypherExecutable.class));
        assertThat(constraintExecutable.getSource(), containsString("not (testMethod)-[:INVOKES*]->(:Method:Assert)"));

        assertThat(ruleSet.getConceptBucket()
            .getIds(), containsInAnyOrder(constraint.getRequiresConcepts()
            .keySet()
            .toArray()));
    }

    @Test
    void scriptRules() throws Exception {
        RuleSet ruleSet = readRuleSet("/parser/javascript-rules.adoc");
        ConceptBucket concepts = ruleSet.getConceptBucket();
        assertThat(concepts.size(), equalTo(1));

        Concept concept1 = concepts.getById("concept:JavaScript");
        assertThat(concept1.getId(), equalTo("concept:JavaScript"));
        assertThat(concept1.getDescription(), containsString("Demonstrates a concept using JavaScript."));
        assertThat(concept1.getRequiresConcepts()
            .keySet(), IsEmptyCollection.<String>empty());

        Executable executable = concept1.getExecutable();
        assertThat(executable, instanceOf(ScriptExecutable.class));

        ScriptExecutable scriptExecutable = (ScriptExecutable) executable;
        assertThat(scriptExecutable, notNullValue());
        assertThat(scriptExecutable.getLanguage(), equalTo("javascript"));
        assertThat(scriptExecutable.getSource(), CoreMatchers.containsString("var row = new java.util.HashMap();"));
        assertEquals(Collections.emptyMap(), concept1.getRequiresConcepts());
    }

    @Test
    void groups() throws Exception {
        RuleSet ruleSet = readRuleSet("/parser/group.adoc");
        assertThat(ruleSet.getConceptBucket()
            .getIds(), hasItems("test:Concept", "test:CriticalConcept"));
        assertThat(ruleSet.getConstraintBucket()
            .getIds(), hasItems("test:Constraint", "test:CriticalConstraint"));
        GroupsBucket groups = ruleSet.getGroupsBucket();
        Group defaultGroup = groups.getById("test:Default");
        assertThat(defaultGroup, notNullValue());
        Map<String, Severity> includedConcepts = defaultGroup.getConcepts();
        assertThat(includedConcepts.containsKey("test:Concept"), equalTo(true));
        assertThat(includedConcepts.get("test:Concept"), nullValue());
        assertThat(includedConcepts.containsKey("test:CriticalConcept"), equalTo(true));
        assertThat(includedConcepts.get("test:CriticalConcept"), equalTo(Severity.CRITICAL));
        Map<String, Severity> includedConstraints = defaultGroup.getConstraints();
        assertThat(includedConstraints.containsKey("test:Constraint"), equalTo(true));
        assertThat(includedConstraints.get("test:Constraint"), nullValue());
        assertThat(includedConstraints.containsKey("test:CriticalConstraint"), equalTo(true));
        assertThat(includedConstraints.get("test:CriticalConstraint"), equalTo(Severity.CRITICAL));
        Map<String, Severity> includedGroups = defaultGroup.getGroups();
        assertThat(includedGroups.keySet(), hasItems("test:Group"));
        Group group = groups.getById("test:Group");
        assertThat(group, notNullValue());
    }

    @Test
    void brokenRules() throws Exception {
        RuleSet ruleSet = readRuleSet("/parser/broken-rules.adoc");
        assertThat(ruleSet.getConceptBucket()
            .getIds(), hasItems("test:MissingDescription"));

        ConceptBucket concepts = ruleSet.getConceptBucket();
        try {
            concepts.getById("test:MissingCodeFragment");
            fail("Concept has no code fragment, should have failed!");
        } catch (RuleException e) {
            // expected
        }
    }

    @Test
    void ruleParameters() throws Exception {
        RuleSet ruleSet = readRuleSet("/parser/parameters.adoc");
        Concept concept = ruleSet.getConceptBucket()
            .getById("test:ConceptWithParameters");
        verifyParameters(concept, false);
        // Concept conceptWithDefaultValues =
        // ruleSet.getConceptBucket().getById("test:ConceptWithParametersAndDefaultValues");
        // verifyParameters(conceptWithDefaultValues, true);
        Constraint constraint = ruleSet.getConstraintBucket()
            .getById("test:ConstraintWithParameters");
        verifyParameters(constraint, false);
        // Constraint constraintWithDefaultValues =
        // ruleSet.getConstraintBucket().getById("test:ConstraintWithParametersAndDefaultValues");
        // verifyParameters(constraintWithDefaultValues, true);
    }

    @Test
    void documentAsGroup() throws RuleException {
        RuleSet ruleSet = readRuleSet("/parser/document-as-group.adoc");
        Group documentGroup = ruleSet.getGroupsBucket()
            .getById("documentGroup");
        assertThat(documentGroup.getId(), equalTo("documentGroup"));
    }

    @Test
    void definitionList() throws RuleException {
        RuleSet ruleSet = readRuleSet("/parser/definition-list.adoc");
        Group testGroup = ruleSet.getGroupsBucket()
            .getById("test:Default");
        assertThat(testGroup.getId(), equalTo("test:Default"));
        Concept testConcept = ruleSet.getConceptBucket()
            .getById("test:Concept");
        assertThat(testConcept.getId(), equalTo("test:Concept"));
    }

    private void verifyParameters(ExecutableRule rule, boolean assertDefaultValue) {
        Map<String, Parameter> parameters = rule.getParameters();
        verifyParameter(parameters, "charParam", Parameter.Type.CHAR, assertDefaultValue ? (byte) 4 : null);
        verifyParameter(parameters, "byteParam", Parameter.Type.BYTE, assertDefaultValue ? (byte) 42 : null);
        verifyParameter(parameters, "shortParam", Parameter.Type.SHORT, assertDefaultValue ? (short) 42 : null);
        verifyParameter(parameters, "intParam", Parameter.Type.INT, assertDefaultValue ? 42 : null);
        verifyParameter(parameters, "longParam", Parameter.Type.LONG, assertDefaultValue ? 42L : null);
        verifyParameter(parameters, "floatParam", Parameter.Type.FLOAT, assertDefaultValue ? (float) 42.0 : null);
        verifyParameter(parameters, "doubleParam", Parameter.Type.DOUBLE, assertDefaultValue ? 42.0 : null);
        verifyParameter(parameters, "booleanParam", Parameter.Type.BOOLEAN, assertDefaultValue ? true : null);
        verifyParameter(parameters, "stringParam", Parameter.Type.STRING, assertDefaultValue ? "FortyTwo" : null);
    }

    private static <T> void verifyParameter(Map<String, Parameter> parameters, String name, Parameter.Type type, T defaultValue) {
        Parameter parameter = parameters.get(name);
        assertThat("Expected a parameter with name " + name, parameter, notNullValue());
        assertThat(parameter.getName(), equalTo(name));
        assertThat(parameter.getType(), equalTo(type));
        assertThat(parameter.getDefaultValue(), Matchers.<Object>equalTo(defaultValue));
    }
}
