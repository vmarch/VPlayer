package app.dao;

import app.model.AutoPlay;
import app.xml.usersettings.XMLSettingsReader;
import app.xml.usersettings.XMLSettingsWriter;

public class UserSettingsDAO implements SettingsDAO {

    XMLSettingsReader reader;

    public UserSettingsDAO() {
    }

    @Override
    public double getVolumeLevel() {
        reader = new XMLSettingsReader();
        if (reader.isFileExist()) {
            return reader.getBaseVolumeLevel();
        }
        return 0.0;
    }

    @Override
    public AutoPlay getAutoPlayState() {
        reader = new XMLSettingsReader();
        if (reader.isFileExist()) {
            return reader.getAutoPlay();
        }
        return null;
    }

    @Override
    public int getJumpTime() {
        reader = new XMLSettingsReader();
        if (reader.isFileExist()) {
            return reader.getJumpTime();
        }
        return 0;
    }

    @Override
    public void setSettings(double volumeLevel, AutoPlay autoPlay, int jumpTime) {
        new XMLSettingsWriter(volumeLevel, autoPlay, jumpTime);
    }
}
