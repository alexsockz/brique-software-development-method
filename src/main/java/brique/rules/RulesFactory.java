package brique.rules;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

// Factory responsible for creating GameRules instances.
// Uses a registry so new rule variants can be added without modifying this class (OCP).
public class RulesFactory {

    private static final Map<RuleType, Supplier<GameRules>> REGISTRY = new EnumMap<>(RuleType.class);

    static {
        // Register built-in rule sets
        REGISTRY.put(RuleType.STANDARD, StandardBriqueRules::new);
    }

    private RulesFactory() { /* utility class */ }

    // Allows external code to register additional rule variants at startup.
    public static void register(RuleType type, Supplier<GameRules> supplier) {
        REGISTRY.put(type, supplier);
    }

    public static GameRules createRules(RuleType type) {
        Supplier<GameRules> supplier = REGISTRY.get(type);
        if (supplier == null) {
            throw new IllegalArgumentException("Unknown rule type: " + type);
        }
        return supplier.get();
    }
}
