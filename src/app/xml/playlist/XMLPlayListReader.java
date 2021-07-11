package app.xml.playlist;

import app.model.PlayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XMLPlayListReader {
    private static final Logger log = LogManager.getLogger();

    //Path for APP's dir.
    //System.out.println(System.getProperty("user.dir"));
    public XMLPlayListReader() {

    }

    private final List<PlayList> playListList = new ArrayList<>();

    List<String> listOfPath = new ArrayList<>();

    public List<PlayList> getPlayLists() {
        log.info("getPlayLists()...");

        try {
            File f = new File("myplaylist.xml");
            if (f.exists() && !f.isDirectory()) {


                Document doc = new SAXBuilder().build(new File("myplaylist.xml"));
                Element rootElement = doc.getRootElement(); //Root

                List<Element> playListElements = rootElement.getChildren(); //List of PlayList

                for (Element playListElement : playListElements) { // elements of Playlist
                    log.info("Element name: " + playListElement.getName());

                    //get list of path.
                    Element listOfPathElement = playListElement.getChild("ListOfPath");

                    List<Element> pathList = listOfPathElement.getChildren();
//                    List<String> path = new ArrayList<>();
                    for (Element pathElement : pathList) {

                        log.info("Element name child: " + pathElement.getName());
//                        path.add(pathElement.getText());

                        listOfPath.add(pathElement.getText());// Path List
                        log.info("pathElement.getText(): " + pathElement.getText());
                        log.info("listOfPath.size(): " + listOfPath.size());
                    }

                    PlayList pl = new PlayList(
                            playListElement.getChildText("ListName"),
                            Long.parseLong(playListElement.getChildText("OpenDate")),
                            Integer.parseInt(playListElement.getChildText("LastIndex")),
                            Double.parseDouble(playListElement.getChildText("LastTime")),
                            listOfPath
                    );

                    log.info(pl);
                    log.info("playListList.size(): " + playListList.size());
                    playListList.add(pl);
                    log.info("playListList.size(): " + playListList.size());
                }
            }
            return playListList;
        } catch (JDOMException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
