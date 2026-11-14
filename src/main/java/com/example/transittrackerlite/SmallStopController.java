package com.example.transittrackerlite;
import javafx.fxml.FXML;
import java.util.Map;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;  

public class SmallStopController {
    private Stations station;
    private Map.Entry<String, Long> arrivalTime;
    private boolean predicted;

    @FXML
    private Text RouteText;

    @FXML
    private Text DestinationText;

    @FXML
    private Text ArrivalTime;

    @FXML
    private Rectangle Tracking; 

    @FXML
    private Text TrackingText;

    public void setStation(Stations station) {
        this.station = station;
    }   

    public void setArrivalTime(Map.Entry<String, Long> arrivalTime) {
        this.arrivalTime = arrivalTime;
        String[] keyParts = arrivalTime.getKey().split(",");
        if (keyParts[0].equals("true")) {
            predicted = true;
        } else {
            predicted = false;
        }
        DestinationText.setText(keyParts[2]);
        RouteText.setText(keyParts[3]);
        int arrivalTimeMin = (int) ((arrivalTime.getValue() - System.currentTimeMillis()) / 60000);
        ArrivalTime.setText(arrivalTimeMin + " min");
        if (predicted && arrivalTimeMin <= 5 && arrivalTimeMin >= 0) {
            Tracking.setFill(javafx.scene.paint.Color.YELLOW);
            TrackingText.setText("Arriving");
        } 
        else if (predicted && arrivalTimeMin < 0) {
            Tracking.setFill(javafx.scene.paint.Color.RED);
            TrackingText.setText("Departed");
        }
        else if (predicted) {
            Tracking.setFill(javafx.scene.paint.Color.GREEN);
            TrackingText.setText("Tracking");
        } 
        else {
            Tracking.setFill(javafx.scene.paint.Color.GRAY);
            TrackingText.setText("NO TRACK");
        }
    }

    public void initialize() {
        System.out.println("SmallStopController initialized");
    }
}
