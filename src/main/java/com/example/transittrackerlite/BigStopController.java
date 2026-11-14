package com.example.transittrackerlite;

import javafx.fxml.FXML;

import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class BigStopController {
    @FXML
    private Text StopName;

    @FXML
    private StackPane StopPane;
    
    public void setStopName(String name) {
        StopName.setText(name);
    }

    public void setStopPane(VBox content) {
        StopPane.getChildren().clear(); 
        StopPane.getChildren().add(content);
    }

    public void initialize() {
        System.out.println("BigStopController initialized");

    }
}
