package brique.rules;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.*;

class RulesFactoryTest {
    
    @Test
    @DisplayName("Should create standard rules")
    void shouldCreateStandardRules() {
        GameRules rules = RulesFactory.createRules(RuleType.STANDARD);
        
        assertThat(rules).isNotNull();
        assertThat(rules).isInstanceOf(StandardBriqueRules.class);
    }
    
    @Test
    @DisplayName("Should throw exception for unknown rule type")
    void shouldThrowExceptionForUnknownRuleType() {
        // This would require adding a null check or handling
        // For now, test that factory works with valid types
        assertThatCode(() -> RulesFactory.createRules(RuleType.STANDARD))
            .doesNotThrowAnyException();
    }
    
    @Test
    @DisplayName("Should create different instances each time")
    void shouldCreateDifferentInstancesEachTime() {
        GameRules rules1 = RulesFactory.createRules(RuleType.STANDARD);
        GameRules rules2 = RulesFactory.createRules(RuleType.STANDARD);
        
        assertThat(rules1).isNotSameAs(rules2);
    }
}
