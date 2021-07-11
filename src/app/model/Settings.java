package app.model;

public final class Settings {

    private final double baseVolumeLevel = 0.3;
    private final AutoPlay autoPlay = AutoPlay.getDefault(); //as default = ALL
    private final int jumpTime = JumpTime.getDefault(); //as default = 10 seconds

    public double getBaseVolumeLevel() {
        return baseVolumeLevel;
    }

    public AutoPlay getAutoPlay() {
        return autoPlay;
    }

    public int getJumpTime() {
        return jumpTime;
    }
}
