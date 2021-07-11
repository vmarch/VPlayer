package app.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class PlayList implements Serializable {
    String listName;
    long openDate;
    int lastIndex;
    double timePosition;
    List<String> listPath = new ArrayList<>();

    public PlayList() {
    }

    //Normal creating
    public PlayList(String listName, int lastIndex, double timePosition, List<String> listPath) {
        this.listName = listName;
        this.openDate = getCurrentTime();
        this.lastIndex = lastIndex;
        this.timePosition = timePosition;
        this.listPath = listPath;
    }

    //For update with adding new Path
    public PlayList(String listName, int lastIndex, double timePosition, String path) {
        this.listName = listName;
        this.openDate = getCurrentTime();
        this.lastIndex = lastIndex;
        this.timePosition = timePosition;
        setPath(path);
    }

    //For common creating of PlayList object when we inflate this from DB (XMLReader)
    public PlayList(String listName, long openDate, int lastIndex, double timePosition, List<String> listPath) {
        this.listName = listName;
        this.openDate = openDate;
        this.lastIndex = lastIndex;
        this.timePosition = timePosition;
        this.listPath = listPath;
    }

    public String getListName() {
        return listName;
    }

    public long getOpenDate() {
        return openDate;
    }

    public int getLastIndex() {
        return lastIndex;
    }

    public double getTimePosition() {
        return timePosition;
    }

    public List<String> getListPath() {
        return listPath;
    }

    //Generate current time when user update some info
    private long getCurrentTime() {
        ZonedDateTime startOfToday = LocalDate.now().atStartOfDay(ZoneId.systemDefault());
        return startOfToday.toEpochSecond() * 1000;
    }

    public void setPath(String path) {
        this.openDate = getCurrentTime(); //update last Opening Time automatic without user
        listPath.add(path);
    }

    public void delPath(String path) {
        this.openDate = getCurrentTime(); //update last Opening Time automatic without user
        listPath.remove(path);
    }

    public void setLastInfo(int lastIndex, double timePosition) {
        this.lastIndex = lastIndex;
        this.timePosition = timePosition;
        this.openDate = getCurrentTime();//update last Opening Time automatic without user
    }

    @Override
    public String toString() {
        return "PlayList{" +
                "listName='" + listName + '\'' +
                ", openDate=" + openDate +
                ", lastIndex=" + lastIndex +
                ", timePosition=" + timePosition +
                ", listPath=" + listPath +
                '}';
    }
}
