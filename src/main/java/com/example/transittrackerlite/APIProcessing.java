package com.example.transittrackerlite;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;


public class APIProcessing {


    public static String CallAPI(String function, String id) {
        String apiKey = "4b24bb55-0e61-4d29-a7b5-35ae8a9ea06a";
        String apiUrl = "https://api.pugetsound.onebusaway.org/api/where/" + function + "/" + id + "?key=" + apiKey;
        HttpClient client = HttpClient.newHttpClient();
        String responses = null;

        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Accept", "application/json")
                .GET() 
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            responses = response.body();

        } catch (Exception e) {
            System.out.println("invalid HTTP response");
        }
        
        return responses;
    }

    public static ArrayList<String> parseObjects(String jsonResponse, String name) {
        ArrayList<String> stationNames = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            JsonNode root = mapper.readTree(jsonResponse);
            JsonNode listNode = findNodeByName(root, name);
             
            if (listNode != null && listNode.isArray()) {
                List<String> list = new ArrayList<>();
                for (JsonNode item : listNode) {
                    if (item.isTextual()) {
                        list.add(item.asText());
                    } else {
                        list.add(mapper.writeValueAsString(item));
                    }
                }
                stationNames.addAll(list);
            }
        }
        catch (Exception e) {
            System.out.println("Error parsing JSON: " + e.getMessage());
        }
        return stationNames;
    }

    private static JsonNode findNodeByName(JsonNode node, String name) {
        if (node == null) {
            return null;
        }
        if (node.has(name)) {
            return node.get(name);
        }

        if (node.isObject() || node.isContainerNode()) {
            for (JsonNode child : node) {
                JsonNode found = findNodeByName(child, name);
                if (found != null) return found;
            }
        }
        return null;
    }
}



