package app.model;

public enum AutoPlay {
    NO, ONE, ALL;

    public static AutoPlay getDefault() {
        return ALL;
    }
}
