package app.xml.usersettings;

import app.model.AutoPlay;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.FileOutputStream;

public class XMLSettingsWriter {
    private static final Logger log = LogManager.getLogger();

    public XMLSettingsWriter(double baseVolumeLevel, AutoPlay autoPlay, int jumpTime) {
        saveUserSettings(baseVolumeLevel, autoPlay, jumpTime);
    }

    private void saveUserSettings(double baseVolumeLevel, AutoPlay autoPlay, int jumpTime) {
        log.info("createFile mysettings.txt ...");

        try (FileOutputStream out = new FileOutputStream("mysettings.xml")) {

            Element rootElement = new Element("Root");
            Document doc = new Document(rootElement);

            Element child = new Element("Settings");

            Element childVolume = new Element("Volume");
            childVolume.setText(String.valueOf(baseVolumeLevel));
            child.addContent(childVolume);

            Element childAutoPlay = new Element("AutoPlay");
            childAutoPlay.setText(String.valueOf(autoPlay));
            child.addContent(childAutoPlay);

            Element childJumpTime = new Element("JumpTime");
            childJumpTime.setText(String.valueOf(jumpTime));
            child.addContent(childJumpTime);

            rootElement.addContent(child);

            XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
            outputter.output(doc, out);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
