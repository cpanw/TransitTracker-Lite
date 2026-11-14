package com.example.transittrackerlite;

import java.util.Map;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;

public class StationView {
    
    public static VBox vBoxFactory(HashMap<String, Long> arrivalTimes, Stations station, boolean justOne) {
        VBox root = new VBox();
        if (!justOne) {
            for (Map.Entry<String, Long> entry : arrivalTimes.entrySet()) {
                try {
                    FXMLLoader loader = new FXMLLoader(StationView.class.getResource("SmallStop.fxml"));
                    VBox oneArrival = new VBox();
                    oneArrival.getChildren().add(loader.load());
                    SmallStopController controller = loader.getController();
                    controller.setStation(station);
                    controller.setArrivalTime(entry);
                    root.getChildren().add(oneArrival);
                } catch (IOException e) {
                    System.out.println("Error loading FXML file: " + e.getMessage());
                }
            }
        }
        else if (arrivalTimes.entrySet().iterator().hasNext())
        {
            try {
                FXMLLoader loader = new FXMLLoader(StationView.class.getResource("SmallStop.fxml"));
                VBox oneArrival = new VBox();
                loader.load();
                SmallStopController controller = loader.getController();
                controller.setStation(station);
                controller.setArrivalTime(arrivalTimes.entrySet().iterator().next());
                oneArrival.getChildren().add(loader.getRoot());
                root.getChildren().add(oneArrival);
            } catch (IOException e) {
                System.out.println("Error loading FXML file: " + e.getMessage());
            }
        }
        return root;
    }

    
    public static HashMap<String, Long> getArrivalTimes(String id) {
        HashMap<String, Long> arrivalTimes = new HashMap<>();
        String response = APIProcessing.CallAPI("arrivals-and-departures-for-stop", id);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = null;
        try {
            rootNode = mapper.readTree(response);
        } catch (IOException e) {
            System.out.println("Error reading response JSON: " + e.getMessage());
        }
        JsonNode arrivals = rootNode.path("data").path("entry").path("arrivalsAndDepartures");
        for (int i = 0; i < arrivals.size(); i++) {
            JsonNode item = arrivals.get(i);
            boolean predicted = item.path("predicted").asBoolean();
            String destination = item.path("tripHeadsign").asText();
            String route = item.path("routeShortName").asText();
            if (item != null && item.has("predictedArrivalTime") && item.has("routeId")) {
                arrivalTimes.put(predicted + "," + item.path("routeId").asText() + "," + destination + "," + route, item.path("predictedArrivalTime").asLong());
            }
            else if (item != null && item.has("routeId")) {
                arrivalTimes.put(item.path("routeId").asText(), 0L);
                System.out.println("" + predicted + " " + "N/A");
            }
        }
        HashMap<String, Long> sortedArrivals = arrivalTimes.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1, 
                        LinkedHashMap::new 
                ));

        return sortedArrivals;
    }
    
    
    public static Stations getStopInfo(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }
        String id = "";
        HashMap<String, String> stationsMap = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            stationsMap = mapper.readValue(Path.of("stations.json").toFile(), HashMap.class);
        } catch (IOException e) {
            System.out.println("Error reading stations.json: " + e.getMessage());
            return null;
        }
        for (Map.Entry<String, String> entry : stationsMap.entrySet()) {
            if (Objects.equals(entry.getValue(), name)) {
                id = "1_" + entry.getKey();
                break;
            }
        }
        if (id.isEmpty()) {
            return null;
        }
        String response = APIProcessing.CallAPI("stop", id);
        if (response == null || response.isBlank()) {
            return null;
        }
        ArrayList<String> stops = APIProcessing.parseObjects(response, "staticRouteIds");
        HashMap<String, String> routeNums = new HashMap<>();
        for (String stop : stops) {
            String routeResponse = APIProcessing.CallAPI("route", stop);
            if (routeResponse == null || routeResponse.isBlank()) {
                continue;
            }
            JsonNode rootNode = null;
            try {
                rootNode = mapper.readTree(routeResponse);
            } catch (IOException e) {
                System.out.println("Error reading response JSON: " + e.getMessage());
                continue;
            }
            JsonNode entryNode = rootNode.path("data").path("entry");
            if (entryNode.isMissingNode() || !entryNode.has("nullSafeShortName")) {
                continue;
            }
            String routeNum = entryNode.path("nullSafeShortName").asText();
            routeNums.put(stop, routeNum);
        }
        return new Stations(id, name, routeNums);
    }

}