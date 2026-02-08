package brique.rules;

public class RulesFactory {
    public static GameRules createRules(RuleType type) {
        switch (type) {
            case STANDARD:
                return new StandardBriqueRules();
            // Future: Add more rule variants
            default:
                throw new IllegalArgumentException("Unknown rule type: " + type);
        }
    }
}