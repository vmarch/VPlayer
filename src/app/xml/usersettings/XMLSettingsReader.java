package app.xml.usersettings;

import app.model.AutoPlay;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class XMLSettingsReader {
    private static final Logger log = LogManager.getLogger();

    public XMLSettingsReader() {
        getUserSettings();
    }

    private boolean fileExist = false;
    private double baseVolumeLevel = 0.0;
    private AutoPlay autoPlay = null;
    private int jumpTime = 0;

    public boolean isFileExist() {
        return fileExist;
    }

    public double getBaseVolumeLevel() {
        return baseVolumeLevel;
    }

    public AutoPlay getAutoPlay() {
        return autoPlay;
    }

    public int getJumpTime() {
        return jumpTime;
    }

    private void getUserSettings() {
        log.info("getUserSettings()...");

        try {
            File f = new File("mysettings.xml");
            if (f.exists() && !f.isDirectory()) {
                log.info("File Exist...");


                Document doc = new SAXBuilder().build(new File("mysettings.xml"));
                Element rootElement = doc.getRootElement(); //Root

                List<Element> settingsElements = rootElement.getChildren(); //Settings

                for (Element settingsElement : settingsElements) { // elements of Settings
                    log.info("Element name: " + settingsElement.getName());

                    baseVolumeLevel = Double.parseDouble(settingsElement.getChildText("Volume"));

                    String autoPlayString = settingsElement.getChildText("AutoPlay");
                    if (AutoPlay.NO.toString().equals(autoPlayString)) {
                        autoPlay = AutoPlay.NO;
                    } else if (AutoPlay.ONE.toString().equals(autoPlayString)) {
                        autoPlay = AutoPlay.ONE;

                    } else if (AutoPlay.ALL.toString().equals(autoPlayString)) {
                        autoPlay = AutoPlay.ALL;
                    }

                    jumpTime = Integer.parseInt(settingsElement.getChildText("JumpTime"));

                }
                fileExist = true;
            } else {
                fileExist = false;
            }

        } catch (JDOMException | IOException e) {
            e.printStackTrace();
            fileExist = false;
        }
    }
}
