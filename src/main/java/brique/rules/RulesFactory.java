package brique.rules;

public class RulesFactory {
    public static GameRules createRules(RuleType type) {
        switch (type) {
            case STANDARD:
                return new StandardBriqueRules();
            // Future: Add more rule variants
            // case VARIANT_1:
            //     return new VariantRules();
            default:
                throw new IllegalArgumentException("Unknown rule type: " + type);
        }
    }
}