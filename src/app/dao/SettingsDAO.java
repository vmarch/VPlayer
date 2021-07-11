package app.dao;

import app.model.AutoPlay;

public interface SettingsDAO {

    double getVolumeLevel();
    AutoPlay getAutoPlayState();
    int getJumpTime();

    //for complete updating.
    void setSettings(double volumeLevel, AutoPlay autoPlay, int jumpTime);
}
