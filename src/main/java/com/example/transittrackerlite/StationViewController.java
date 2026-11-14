package com.example.transittrackerlite;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.ObjectMapper;

public class StationViewController {
    private Stations station;
    public ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private HashMap<String, String> savedStations = new HashMap<>();
    private ObjectMapper mapper = new ObjectMapper();

    @FXML
    private Text Name;

    @FXML
    private Text StopID;

    @FXML
    private Text RouteList;

    @FXML
    private ImageView AgencyLogo;

    @FXML
    private ScrollPane routeScrollPane;

    @FXML
    private CheckBox favoriteCheckBox;

    @FXML
    private void handleFavoriteAction(ActionEvent event) {
        boolean isFavorite = favoriteCheckBox.isSelected();
        if (isFavorite) {
            savedStations.put(station.getId(), station.getName());
        } else {
            savedStations.remove(station.getId());
        }
        try {
            mapper.writeValue(Path.of("savedStations.json").toFile(), savedStations);
        } catch (IOException e) {
            System.out.println("Error writing to savedStations.json: " + e.getMessage());
        }
    }

    @FXML
    private void handleBackButtonAction(ActionEvent event) {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        shutdown();
        MainPage.changeToMainPage(currentStage);
    }


    public void startArrivalTimeUpdates() {
        final Runnable updateTask = () -> {
            HashMap<String, Long> arrivalTimes = StationView.getArrivalTimes(station.getId());
            Platform.runLater(() -> {
                routeScrollPane.setContent(StationView.vBoxFactory(arrivalTimes, station, false));
            });
        };
        scheduler.scheduleAtFixedRate(updateTask, 0, 30, TimeUnit.SECONDS);
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

    public void setStationInfo(Stations station) {
        this.station = station;
        Name.setText("Stop: " + station.getName());
        StopID.setText("Stop ID: " + station.getId());
        StringBuilder routes = new StringBuilder("Routes: ");
        for (Map.Entry<String, String> entry : station.getRouteNums().entrySet()) {
            routes.append(entry.getValue()).append(", ");
        }
        RouteList.setText(routes.toString());
        startArrivalTimeUpdates();
        System.out.println(savedStations.containsKey(station.getId()));
        if (station != null && savedStations.containsKey(station.getId())) {
            favoriteCheckBox.setSelected(true);
        }
    }

    public void initialize() {
        System.out.println("StationViewController initialized");
        ImageView logo = new ImageView(new Image(getClass().getResourceAsStream("logo.png")));
        AgencyLogo.setImage(logo.getImage());
        try {
            savedStations = mapper.readValue(Path.of("savedStations.json").toFile(), HashMap.class);
        } catch (IOException e) {
            System.out.println("Error reading savedStations.json: " + e.getMessage());
            savedStations = new HashMap<>();
        }
        Platform.runLater(() -> {
            Stage stage = (Stage) Name.getScene().getWindow();
            stage.setOnCloseRequest(e -> shutdown());
        });
    }
}
