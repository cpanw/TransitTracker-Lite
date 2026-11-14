package com.example.transittrackerlite;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class Stations {
    private String id;
    private String name;
    private HashMap<String, String> routeNums = new HashMap<>();

    public Stations(String id, String name, HashMap<String, String> routeNums) {
        this.id = id;
        this.name = name;
        this.routeNums = routeNums;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public HashMap<String, String> getRouteNums() {
        return routeNums;
    }

    public static ObservableList<String> setComboBox() {
        ObservableList<String> Stations = FXCollections.observableArrayList();
        ObjectMapper mapper = new ObjectMapper();
        HashMap<String, String> stationsMap;
        try {
            stationsMap = mapper.readValue(Path.of("stations.json").toFile(), HashMap.class);
        }
        catch (IOException e) {
            System.out.println("Error reading stations.json: " + e.getMessage());
            return Stations;
        }
        ArrayList<String> stationNames = new ArrayList<>(stationsMap.values());
        stationNames.sort(Comparator.naturalOrder());
        Stations.addAll(stationNames);
        return Stations;
    }

    public String toString() {
        return name + " (" + id + ")";
    }   
}
