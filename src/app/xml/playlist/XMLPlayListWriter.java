package app.xml.playlist;

import app.model.PlayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.FileOutputStream;
import java.util.List;

public class XMLPlayListWriter {

    private static final Logger log = LogManager.getLogger();
    //Path for APP's dir.
    //System.out.println(System.getProperty("user.dir"));
    private final List<PlayList> listOfPlayList;

    public XMLPlayListWriter(List<PlayList> listOfPlayList) {
        this.listOfPlayList = listOfPlayList;
        createFile();
    }

    private void createFile() {
        log.info("createFile...");

        if (listOfPlayList != null) {
            try (FileOutputStream out = new FileOutputStream("myplaylist.xml")) {

                Element rootElement = new Element("Root");
                Document doc = new Document(rootElement);

                for (PlayList playList :
                        listOfPlayList) {


                    Element child = new Element("PlayList");

                    Element childName = new Element("ListName");
                    childName.setText(playList.getListName());
                    child.addContent(childName);

                    Element childDate = new Element("OpenDate");
                    childDate.setText(String.valueOf(playList.getOpenDate()));
                    child.addContent(childDate);

                    Element childIndex = new Element("LastIndex");
                    childIndex.setText(String.valueOf(playList.getLastIndex()));
                    child.addContent(childIndex);

                    Element childTime = new Element("LastTime");
                    childTime.setText(String.valueOf(playList.getTimePosition()));
                    child.addContent(childTime);

                    Element childList = new Element("ListOfPath");

                    List<String> listPath = playList.getListPath();

                    for (int i = 0; i < listPath.size(); i++) {

                        Element childListPath = new Element("Path");

                        childListPath.setText(listPath.get(i));
                        childListPath.setAttribute("id", String.valueOf(i));

                        childList.addContent(childListPath);
                    }

                    child.addContent(childList);

                    rootElement.addContent(child);
                }
                XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat()); // Formatierung
                outputter.output(doc, out);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            log.error("listOfPlayList = null");
        }
    }
}
