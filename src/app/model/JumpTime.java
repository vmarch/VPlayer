package app.model;

public enum JumpTime {
    FIVE, TEN, FIFTEEN, THIRTY;

    public static int getDefault() {
        return getAsInt(TEN);
    }

    public static int getAsInt(JumpTime jt) {
        return switch (jt) {
            case FIVE -> 5;
            case TEN -> 10;
            case FIFTEEN -> 15;
            case THIRTY -> 30;
        };
    }
}
