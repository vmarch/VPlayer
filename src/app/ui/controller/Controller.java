package app.ui.controller;

import app.dao.BaseSettingsDAO;
import app.dao.SettingsDAO;
import app.dao.UserSettingsDAO;
import app.model.AutoPlay;
import app.model.PlayList;
import app.xml.playlist.XMLPlayListReader;
import app.xml.playlist.XMLPlayListWriter;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static app.model.AutoPlay.*;
import static javafx.scene.media.MediaPlayer.Status.*;

public class Controller {
    private static final Logger log = LogManager.getLogger();
    private static final int DOUBLE_CLICK_TIME = 200;

    private FadeTransition fade;
    private SettingsDAO settingsDAO;
    private MediaPlayer mPlayer;

    private List<PlayList> allPlayLists = new ArrayList<>();
    ObservableList<String> stringObservableList;
    private PlayList curPlayList;
    private String aloneTrackPath;

    private int curIndex = 0;
    private double currVolume;
    private int jumpTime;
    private AutoPlay autoPlay;
    private DoubleProperty width = new SimpleDoubleProperty();
    private DoubleProperty height = new SimpleDoubleProperty();
    private double playTimeValue = 0.0;
    private boolean aloneTrackPlayed = false;
    private boolean startOfPlayer = false;

    //region Setter and Getter

    public int getJumpTime() {
        return jumpTime;
    }

    public void setJumpTime(int jumpTime) {
        this.jumpTime = jumpTime;
    }


    private boolean isStartOfPlayer() {
        log.info("isStartOfPlayer(): " + startOfPlayer);
        return startOfPlayer;
    }

    private void setStartOfPlayer(boolean startOfPlayer) {
        log.info("setStartOfPlayer: old:" + this.startOfPlayer + ", new: " + startOfPlayer);
        this.startOfPlayer = startOfPlayer;
    }

    private MediaPlayer getMP() {
        if (mPlayer != null) {
            return mPlayer;
        } else {
            log.error("mp == NULL");
            return null;
        }
    }

    private void setMP(MediaPlayer mPlayer) {
        this.mPlayer = mPlayer;
    }

    private String getAloneMediaPath() {
        log.info("getAloneMediaPath()...aloneMediaPath: " + aloneTrackPath);
        return aloneTrackPath;
    }

    private void setAloneMediaPath(String aloneMediaPath) {
        log.info("setAloneMediaPath()...aloneMediaPath: " + aloneMediaPath);
        this.aloneTrackPath = aloneMediaPath;
    }

    private boolean isAloneTrackPlayed() {
        return aloneTrackPlayed;
    }

    private void setIsAloneTrack(boolean aloneTrack) {
        this.aloneTrackPlayed = aloneTrack;
    }

    private int getCurIndex() {
        return curIndex;
    }

    private void setCurIndex(int curIndex) {
        this.curIndex = curIndex;
    }

    private AutoPlay getAutoPlay() {
        return autoPlay;
    }

    private void setAutoPlay(AutoPlay autoPlay) {
        this.autoPlay = autoPlay;
    }

    private double getCurrVolume() {
        return currVolume;
    }

    private void setCurrVolume(double currVolume) {
        this.currVolume = currVolume;
    }
    //endregion

    //region FXML elements
    @FXML
    public StackPane stackPane;

    @FXML
    private MediaView mediaView;

    @FXML
    public AnchorPane bottomMenuAnchorPane;

    @FXML
    public Label labelCurrentPlayTime;

    @FXML
    private Slider sliderTime;

    @FXML
    private Slider sliderVolume;

    @FXML
    public Button btnFullScreen;

    @FXML
    public Button btnLoop;

    @FXML
    private AnchorPane listViewAnchorPane;

    @FXML
    private ListView<String> listViewPlayList;

    //endregion

    //region Initialize
    @FXML
    void initialize() {
        log.info("initialize...");
        setStartOfPlayer(true);
        setOnCloseListener();

        settingsDAO = new UserSettingsDAO();

        if (settingsDAO.getJumpTime() == 0) { //Users Settings not exist
            settingsDAO = new BaseSettingsDAO();
            log.info("mysettings.txt NOT exist");
        }

        setJumpTime(settingsDAO.getJumpTime());
        setAutoPlay(settingsDAO.getAutoPlayState());
        setCurrVolume(settingsDAO.getVolumeLevel());

        stringObservableList = FXCollections.observableArrayList();
        listViewPlayList.getItems().setAll(stringObservableList);

        btnLoop.setText(getAutoPlay().toString());

        setupContextMenu();

        hideListView();
        getAllPlayLists();

        initMediaView();
        initMediaPlayer();

    }
    //endregion

    //region Main Buttons Functions
    @FXML
    void onStartPause() {
        log.info("onStartPause...");

        MediaPlayer.Status status = getMP().getStatus();

        if (null == getMP().getMedia()) {
            log.error("Media == NULL");
            return;
        }

        log.info("MediaPlayer.Status : " + status);
        if (status.equals(PAUSED) || status.equals(READY) || status.equals(STOPPED)) {
            onPlay();
        } else if (status.equals(PLAYING)) {
            onPause();
        } else if (status.equals(UNKNOWN) || status.equals(HALTED)) {
            log.error("MediaPlayer.Status : " + status);
        }
    }

    private void onPlay() {
        log.info("onPlay...");
        if (getMP() != null) {
            getMP().play();
        } else {
            log.error("MP==null");
        }
    }

    private void onPause() {
        log.info("onPause...");
        if (getMP() != null) {
            getMP().pause();
        } else {
            log.error("MP==null");
        }
    }

    @FXML
    void onStop() {
        log.info("onStop...");
        if (getMP() != null) {
            getMP().stop();
        } else {
            log.error("MP==null");
        }
    }

    @FXML
    void onPrevious() {
        log.info("onPrevious...");
        startNewTrack(-1);
    }

    @FXML
    void onNext() {
        log.info("onNext...");
        startNewTrack(1);
    }

    @FXML
    public void onActionMute() {
        log.info("onActionMute()...");
        if (getMP() != null) {
            getMP().setMute(!getMP().isMute());
        } else {
            log.error("MP==null");
        }
    }

    public void onActionTimeBack() {
        log.info("seek back: " + getJumpTime() + " sec.");
        Duration currentTime = getMP().getCurrentTime();
        getMP().seek(Duration.seconds(currentTime.toSeconds() - getJumpTime()));
    }

    public void onActionTimeForward() {
        log.info("seek forward: " + getJumpTime() + " sec.");
        Duration currentTime = getMP().getCurrentTime();
        getMP().seek(Duration.seconds(currentTime.toSeconds() + getJumpTime()));
    }
    //endregion

    //region Additional Buttons Functions
    @FXML
    void onActionFullScreen() {
        log.info("onActionFullScreen()...");
        Stage stage = (Stage) btnFullScreen.getScene().getWindow();
        stage.setFullScreen(!stage.isFullScreen());
    }

    @FXML
    public void onActionLoop() {
        log.info("onActionLoop()...");
        switch (getAutoPlay()) {
            case NO -> setAutoPlay(ONE);
            case ONE -> setAutoPlay(ALL);
            case ALL -> setAutoPlay(NO);
        }
        setMediaPlayerAutoPlay();
        btnLoop.setText(getAutoPlay().toString());
    }

    @FXML
    void onActionOpenSettings() {
        log.info("onActionOpenSettings()...");
        //TODO
    }

    @FXML
    void onActionOpenInfo() {
        log.info("onActionOpenInfo()...");
        //TODO
    }
    //endregion

    //region Work with Tracks and MediaPlayer
    private void startNewTrack(int currIndexIterator) {
        log.info("startNewTrack with Index iteration: " + currIndexIterator);
        if (!isAloneTrackPlayed()) {
            if (curPlayList != null) {

                int size = curPlayList.getListPath().size();
                log.info("oldCurIndex: " + getCurIndex() + ", curPlaylist.getListPath().size(): " + size);


                if (1 == currIndexIterator) {
                    if (getCurIndex() < (size - 1)) {
                        setCurIndex(getCurIndex() + 1);

                    } else {
                        setCurIndex(0);
                    }
                } else if (-1 == currIndexIterator) {
                    if (getCurIndex() > 0) {
                        setCurIndex(getCurIndex() - 1);
                    } else {
                        setCurIndex(size - 1);
                    }
                }
            } else {
                log.info("curPlayList == null -> openPlaylist()...");
                showListView();
            }
        } else {
            setCurIndex(0);
        }
        //clear resources and close current MediaPlayer
        disposeOldPlayer();
        initMediaPlayer();
        onPlay();
    }

    private void initMediaPlayer() {
        log.info("initMediaPlayer()...");
        Media media = getCurrentMedia();

        if (null != media) {
            createNewMediaPlayer(media);
            setMediaPlayerAutoPlay();
            setTimeSliderListeners();
            setMediaPlayerVolume();
            setVolumeSliderListener();
            inflateMediaPlayerToMediaView();
        } else {
            log.info("Media == null -> open playlist ...");
            showListView();
        }
    }

    private void createNewMediaPlayer(Media media) {
        log.info("creating new MediaPlayer...");
        setMP(new MediaPlayer(media));
        if (playTimeValue > 0) {
            log.info("playTimeValue = " + playTimeValue);
            sliderTime.setValue(playTimeValue);
            getMP().seek(Duration.seconds(sliderTime.getValue()));
        }
    }

    private Media getCurrentMedia() {
        log.info("getCurrentMedia()...");

        if (isStartOfPlayer()) { //when Player start
            log.info("startOfPlayer = true -> false");
            setStartOfPlayer(false);
            if (allPlayLists != null && allPlayLists.size() > 0) {
                log.info("allPlayLists.size() = " + allPlayLists.size());

                getLastPlayList();

                if (curPlayList.getListPath().size() > 0) {

                    setCurIndex(curPlayList.getLastIndex());

                    String path = curPlayList.getListPath().get(getCurIndex());
                    playTimeValue = curPlayList.getTimePosition();

                    return new Media(new File(path).toURI().toString());
                } else {
                    log.info("curPlayList.getListPath().size() == 0");
                    return null;
                }

            } else {
                log.info("allPlayLists == null or allPlayLists.size() == 0 ");
                return null;
            }

        } else {
            log.info("startOfPlayer = false");
            if (isAloneTrackPlayed()) {
                return new Media(new File(getAloneMediaPath()).toURI().toString());
            } else {
                if (curPlayList.getListPath().size() == 0) {
                    log.info("Media 3 = NULL");
                    return null;
                } else {
                    String path = curPlayList.getListPath().get(getCurIndex());
                    return new Media(new File(path).toURI().toString());
                }
            }
        }
    }

    private void getLastPlayList() {
        PlayList lastPlayList = new PlayList();

        long i = 0;
        for (PlayList pl : allPlayLists) {
            long t = pl.getOpenDate();
            if (i < t) {
                lastPlayList = pl;
                i = t;
            }
        }
        curPlayList = lastPlayList;
    }

    private void setMediaPlayerAutoPlay() {
        log.info("init MP AutoPlay...");
        //set AutoPlay and Repeating of Track

        switch (getAutoPlay()) {
            case NO -> {       // stop video playing when this one ends
                log.info("AutoPlayState: " + NO);
                getMP().setAutoPlay(false);
                getMP().setOnEndOfMedia(() -> {
                    onStop();
                });
            }
            case ONE -> {       // play current video again when this one ends
                log.info("AutoPlayState: " + ONE);
                getMP().setAutoPlay(true);
                getMP().setOnEndOfMedia(() -> {
                    onStop();
                    onPlay();
                });
            }
            case ALL -> {       // play next video when this one ends
                log.info("AutoPlayState: " + ALL);
                getMP().setAutoPlay(true);
                getMP().setOnEndOfMedia(() -> {
                    onStop();
                    if (!isAloneTrackPlayed()) {
                        onNext();
                    } else {
                        onPlay();
                    }
                });
            }
            default -> {
                log.error("Wrong AutoPlay State");
                throw new IllegalStateException("Unexpected value: " + settingsDAO.getAutoPlayState());
            }
        }
    }

    private void setTimeSliderListeners() {
        log.info("setTimeSliderListeners()...");

        getMP().totalDurationProperty().addListener((observable, oldDuration, newDuration) -> {
            sliderTime.setMax(newDuration.toSeconds());
            labelCurrentPlayTime.setText(formatTime(newDuration));
        });

        sliderTime.valueChangingProperty().addListener((observable, wasChanging, isChanging) -> {
            if (!isChanging) {
                getMP().seek(Duration.seconds(sliderTime.getValue()));
            }
        });

        sliderTime.valueProperty().addListener((observable, oldValue, newValue) -> {
            double currentTime = getMP().getCurrentTime().toSeconds();
            if (Math.abs(currentTime - newValue.doubleValue()) > 0.5) {
                getMP().seek(Duration.seconds(newValue.doubleValue()));
            }
            labelCurrentPlayTime.setText(formatTime(Duration.seconds(newValue.doubleValue())));
        });

        getMP().currentTimeProperty().addListener((observable, oldTime, newTime) -> {
            if (!sliderTime.isValueChanging()) {
                sliderTime.setValue(newTime.toSeconds());
            }
            labelCurrentPlayTime.setText(formatTime(newTime));
        });
    }

    private void setMediaPlayerVolume() {
        log.info("setMPVolume()...");
        sliderVolume.adjustValue(getCurrVolume() * 100);
        getMP().setVolume(getCurrVolume());
    }

    private void setVolumeSliderListener() {
        log.info("setVolumeSliderListener()...");

        sliderVolume.valueProperty().addListener((ObservableValue<? extends Number> observable,
                                                  Number old, Number now) -> {
            MediaPlayer.Status status = getMP().getStatus();

            if (status == PLAYING || status == PAUSED || status == STOPPED) {

                getMP().setVolume(sliderVolume.getValue() / 100.0);
                setCurrVolume(sliderVolume.getValue() / 100.0);

                log.info("setVolumeSliderListener(): currVolume = " + getCurrVolume());
            } else {
                log.error("mp.getStatus(): " + status);
            }
        });
    }

    private void inflateMediaPlayerToMediaView() {
        log.info("setMediaPlayerToMediaView()...");
        mediaView.setMediaPlayer(getMP());
    }

    private void disposeOldPlayer() {
        log.info("disposeOldPlayer()...");
        setMP(mediaView.getMediaPlayer());
        if (getMP() != null) {
            getMP().dispose(); // release resources
            if (getMP().getStatus().equals(DISPOSED)) {
                log.info("OldPlayer disposed");
            } else {
                log.info("OldPlayer Status: " + getMP().getStatus());
            }
        } else {
            log.info("mp == null");
        }
    }

    //converting video's Duration time to String for showing in timeLabel
    private static String formatTime(Duration time) {
//        log.info("formatTime()..." + "time.toSeconds(): " + time.toSeconds());

        int hours = (int) time.toHours();
        int minutes = (int) time.toMinutes();
        int seconds = (int) time.toSeconds();

        if (hours > 59) hours = hours % 60;
        if (minutes > 59) minutes = minutes % 60;
        if (seconds > 59) seconds = seconds % 60;

        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }

    //endregion

    //region MediaView

    //OnCloseListener -> Save when close main window (APP)
    private void setOnCloseListener() {
        log.info("setOnCloseListener()...");
        Platform.runLater(() -> {
            stackPane.getScene().getWindow().onCloseRequestProperty().setValue(e -> {
                log.info("onCloseRequestProperty()... ");
                saveUserSettings();
                savePlayListsAsFile();
            });
        });
    }

    private void getAllPlayLists() { //get PlayLists from .txt
        log.info("getAllPlayLists()...");
        allPlayLists.clear();
        allPlayLists = new XMLPlayListReader().getPlayLists();
        inflateListView();
        log.info("allPlayLists: " + allPlayLists.size());
    }

    private void initMediaView() {
        log.info("initMediaView()...");
        //change size of MediaView window with changing of APP window
        width = mediaView.fitWidthProperty();
        height = mediaView.fitHeightProperty();
        width.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
        height.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));

        mediaView.preserveRatioProperty().setValue(true);
        mediaViewOnMouseClickListener();
    }

    private void mediaViewOnMouseClickListener() {
        final int[] time1 = {0};
        final int[] time2 = {0};
        mediaView.setOnMouseClicked(ev -> {

            log.info("OnMouseClicked()... ");
            if (!listViewAnchorPane.isVisible()) {

                long diff = 0;
                if (time1[0] == 0) {
                    time1[0] = (int) System.currentTimeMillis();
                } else {
                    time2[0] = (int) System.currentTimeMillis();
                }
                if (time2[0] != 0 && time1[0] != 0) {
                    diff = time2[0] - time1[0];
                    time1[0] = 0;
                    time2[0] = 0;
                }

                if (ev.getClickCount() == 1 || ev.getClickCount() == 2) {
                    // pause/continue playing every Single-click
                    onStartPause();

                    if (diff > 0 && diff < DOUBLE_CLICK_TIME) {
                        log.info("OnMouseDoubleClicked()  -> diff: " + diff);
                        // on/off Fullscreen
                        onActionFullScreen();
                    }

                } else if (ev.getClickCount() > 2) {
                    //do nothing, maybe mistake
                    log.info("OnMouseClicked() 3 -> ev.getClickCount(): " + ev.getClickCount());
                }
            } else {
                // close/open PlayList window when Double-click
                hideListView();
            }
        });
    }

    //endregion

    //region Elements Animation

    // Bottom elements show
    @FXML
    void onMouseEntered() {
        log.info("onMouseEntered()...");
        if (fade != null) {
            fade.stop();
        }
        fade = new FadeTransition(Duration.millis(200),
                bottomMenuAnchorPane);
        fade.setToValue(1.0);
        fade.setInterpolator(Interpolator.EASE_OUT);

        fade.play();
    }

    // Bottom elements hide
    @FXML
    void onMouseExited() {
        log.info("onMouseExited()...");
        if (fade != null) {
            fade.stop();
        }
        fade = new FadeTransition(Duration.millis(500),
                bottomMenuAnchorPane);
        fade.setToValue(0.0);
        fade.setInterpolator(Interpolator.EASE_OUT);

        fade.play();
    }
    //endregion

    //region ListView

    private void inflateListView() {
        stringObservableList.clear();
        for (PlayList pl : allPlayLists) {
            if (pl != null && null != pl.getListPath()) {
                for (String filePath : pl.getListPath()) {
                    stringObservableList.add(getNameFromFilePath(filePath));
                }
            }
            listViewPlayList.getItems().setAll(stringObservableList);
        }
    }


    private String getNameFromFilePath(String filePath) {
        String fileName = filePath.substring(filePath.lastIndexOf('/') + 1);
        return fileName.substring(0, fileName.lastIndexOf('.'));
    }

    @FXML
    public void onActionOnlyOpen(ActionEvent actionEvent) {
        log.info("onActionOnlyOpen()...");

        Platform.runLater(new Runnable() {
            public void run() {

                File file = new FileChooser().showOpenDialog(null);
                if (file != null) {
                    setAloneMediaPath(file.toString().replace('\\', '/'));
                } else {
                    log.error("File == null");
                }

                setIsAloneTrack(true);
                setCurIndex(0);
                disposeOldPlayer();
                initMediaPlayer();
                hideListView();
                onPlay();
            }
        });
    }

    @FXML
    void onActionAddToPlayList(ActionEvent event) {
        log.info("onActionAddToPlayList()...");

        Platform.runLater(new Runnable() {
            public void run() {
                File file = new FileChooser().showOpenDialog(null);
                if (file != null) {

                    addNewPathToPlayList(file.toString().replace('\\', '/'));
                    updateListOfPlayLists();
                } else {
                    log.error("File==null");
                }
            }
        });
    }

    private void delPathFromPlayList(String name) {
        log.info("delPathFromPlayList()...");
        log.info("name: " + name);

        for (PlayList pl : allPlayLists) {
            for (int i = 0; i < pl.getListPath().size(); i++) {
                String path = pl.getListPath().get(i);
                if (path.contains(name)) {
                    log.info("path: " + path);

                    curPlayList.delPath(path);
                    playTimeValue = 0;
                    setCurIndex(i);
                    updateListOfPlayLists();
                    onPause();
                }
            }
        }
    }

    private void addNewPathToPlayList(String newPath) {
        log.info("addNewPathToPlayList()...");
        log.info("newPath: " + newPath);
        if (curPlayList != null) {
            log.info("curPlayList != null");
            curPlayList.setPath(newPath);
            log.info("curPlayList1: " + curPlayList.getListName());
        } else {
            log.info("curPlayList == null - > new Playlist");
            curPlayList = new PlayList("AutoList", getCurIndex(), sliderTime.getValue(), newPath);
            log.info("curPlayList2: " + curPlayList.getListName());
        }
    }

    private void updateListOfPlayLists() {
        log.info("updateListOfPlayLists()...");
        if (null == allPlayLists) {
            allPlayLists = new ArrayList<>();
            log.info("allPlayLists.indexOf(curPlayList) 1: " + allPlayLists.indexOf(curPlayList));
            allPlayLists.add(curPlayList);
            log.info("allPlayLists.indexOf(curPlayList) 2: " + allPlayLists.indexOf(curPlayList));
        } else {
            if (!allPlayLists.contains(curPlayList)) {
                allPlayLists.add(curPlayList);
            } else {
                allPlayLists.set(allPlayLists.indexOf(curPlayList), curPlayList);
            }
        }
        inflateListView();
    }

    @FXML
    public void onActionOpenListView() {
        log.info("onActionOpenPlayList()...");

        if (listViewAnchorPane.isVisible()) {
            hideListView();
        } else {
            showListView();
        }
    }

    @FXML
    void onActionCloseListView(ActionEvent event) {
        log.info("onClosePlayList()...");
        hideListView();
    }

    private void hideListView() {
        log.info("closePlayList()...");
        listViewAnchorPane.setVisible(false);
        listViewAnchorPane.setManaged(false);
    }

    private void showListView() {
        log.info("openPlaylist()...");
        listViewAnchorPane.setVisible(true);
        listViewAnchorPane.setManaged(true);
    }

    //endregion

    //region Save user's Settings
    private void saveUserSettings() {
        log.info("saveUserSettings()...");

        settingsDAO = new UserSettingsDAO();
        settingsDAO.setSettings(getCurrVolume(), getAutoPlay(), getJumpTime());

    }

    private void savePlayListsAsFile() {
        log.info("updPlayList()... ");

        curPlayList.setLastInfo(getCurIndex(), getMP().getCurrentTime().toSeconds());
        updateListOfPlayLists();

        new XMLPlayListWriter(allPlayLists);

    }
    //endregion

    //region On List element click

    private void setupContextMenu() {
        //Setup ContextMenu
        ContextMenu cm = new ContextMenu();
        MenuItem deleteItem = new MenuItem("Delete");
        cm.getItems().addAll(deleteItem);

        deleteItem.setOnAction(e -> {
            delPathFromPlayList(listViewPlayList.getSelectionModel().getSelectedItem());
        });

        listViewPlayList.setContextMenu(cm);
    }

    public void onActionListItemClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            log.info("Left Click!");

            setIsAloneTrack(false);
            log.info("actionOnListItemClick()...");
            String s = listViewPlayList.getSelectionModel().getSelectedItem();
            for (PlayList pl : allPlayLists) {
                for (int i = 0; i < pl.getListPath().size(); i++) {
                    String path = pl.getListPath().get(i);
                    if (path.contains(s)) {

                        curPlayList = pl;
                        playTimeValue = 0;
                        setCurIndex(i);
                        startNewTrack(0);

                    }
                }
            }
        }
    }

    //endregion
}