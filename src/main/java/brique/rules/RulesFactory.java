package brique.rules;

// Factory responsible for creating GameRules instances.
// Encapsulates the selection logic for different rule variants.
public class RulesFactory {

    public static GameRules createRules(RuleType type) {

        // Select the appropriate rule set based on the enum value
        switch (type) {

            case STANDARD:
                // Default Brique rules implementation
                return new StandardBriqueRules();

            // Additional rule variants can be added here
            default:
                // Defensive programming: fail fast on unknown rule types
                throw new IllegalArgumentException("Unknown rule type: " + type);
        }
    }
}
