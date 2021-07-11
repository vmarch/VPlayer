package app.dao;

import app.model.AutoPlay;
import app.model.Settings;

public class BaseSettingsDAO implements SettingsDAO {
    Settings settings = new Settings();

    @Override
    public double getVolumeLevel() {
        return settings.getBaseVolumeLevel();
    }

    @Override
    public AutoPlay getAutoPlayState() {
        return settings.getAutoPlay();
    }

    @Override
    public int getJumpTime() {
        return settings.getJumpTime();
    }

    @Override
    public void setSettings(double volumeLevel, AutoPlay autoPlay, int jumpTime) {
        //empty because Setting is not mutable. Use UserSettingsDAO.
    }
}
