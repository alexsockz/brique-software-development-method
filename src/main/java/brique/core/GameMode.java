package brique.core;

public enum GameMode {

    LOCAL_1V1("1 vs 1 (Local)"),

    ONLINE("Online"),

    VS_BOT("vs Bot");

    private final String displayName;

    GameMode(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
