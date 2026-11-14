package com.example.transittrackerlite;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import javafx.stage.Stage;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import org.controlsfx.control.SearchableComboBox;
import com.fasterxml.jackson.databind.ObjectMapper;


public class MainPageController implements Initializable {
    HashMap<String, String> savedStations = new HashMap<>();
    public ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    
    @FXML
    private SearchableComboBox<String> StationList = new  SearchableComboBox<>();

    @FXML
    private ScrollPane routeScrollPane;

    @FXML
    private void onStationEnter() {
        String name = null;
        if (!StationList.getSelectionModel().isEmpty()) {
            name = StationList.getSelectionModel().getSelectedItem();
        }
        if ((name == null || name.isBlank()) && StationList.getEditor() != null) {
            String editorText = StationList.getEditor().getText();
            if (editorText != null && !editorText.isBlank()) {
                String trimmed = editorText.trim();
                if (StationList.getItems().contains(trimmed)) {
                    name = trimmed;
                }
            }
        }
        if (name != null && !name.isBlank()) {
            Stations station = StationView.getStopInfo(name);
            if (station != null) {
                Stage stage = (Stage) StationList.getScene().getWindow();
                MainPage.changeToStationView(station, stage);
            }
        }
        shutdown();
    }

    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }
    
    public void startArrivalTimeUpdates() {
        final Runnable updateTask = () -> {
            VBox favorites = new VBox();
            for (String stationId : savedStations.keySet()) {
            HashMap<String, Long> arrivals = StationView.getArrivalTimes(stationId);
            VBox oneFavorite = StationView.vBoxFactory(arrivals, StationView.getStopInfo(savedStations.get(stationId)), true);  
            oneFavorite.setMinHeight(0);
            try {
                FXMLLoader loader = new FXMLLoader(MainPage.class.getResource("BigStop.fxml"));
                loader.load();
                BigStopController controller = loader.getController();
                controller.setStopName(savedStations.get(stationId));
                controller.setStopPane(oneFavorite);
                favorites.getChildren().add(loader.getRoot());
            } catch (IOException e) {
                System.out.println("Error loading FXML file: " + e.getMessage());
            }
        }
            Platform.runLater(() -> {
                routeScrollPane.setContent(favorites);
                Stage stage = (Stage) StationList.getScene().getWindow();
                stage.setOnCloseRequest(e -> shutdown());
            });
        };
        scheduler.scheduleAtFixedRate(updateTask, 0, 10, TimeUnit.SECONDS);
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<String> stationObservableList = (Stations.setComboBox());
        StationList.setItems(stationObservableList);
        System.out.println("MainPageController initialized");
        ObjectMapper mapper = new ObjectMapper();
        try {
            savedStations = mapper.readValue(Path.of("savedStations.json").toFile(), HashMap.class);
        } 
        catch (IOException e) {
            System.out.println("Error reading savedStations.json: " + e.getMessage());
        }
    }

}
