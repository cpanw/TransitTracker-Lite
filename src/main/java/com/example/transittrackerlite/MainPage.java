package com.example.transittrackerlite;

import com.pixelduke.control.skin.FXSkins;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import java.io.IOException;
import java.io.InputStream;

public class MainPage extends Application {
    private static Parent root1;
    private static Parent root2;
    private static Scene everything;


    @Override
    public void start(Stage stage) throws Exception {
        InputStream fontStream = getClass().getResourceAsStream("/Monaco.ttf");
        Font font = Font.loadFont(fontStream, 12);

        //root.getStylesheets().add(FXSkins.getStylesheetURL());
        //JMetro jMetro = new JMetro(Style.DARK);
        root1 = FXMLLoader.load(getClass().getResource("HomePage.fxml"));
        System.out.println("Fdsfsdafds");
        stage.setTitle("TransitTracker Lite");
        everything = new Scene(root1);
        stage.setScene(everything);
        stage.show();
    }

    public static void changeToStationView(Stations station, Stage stage) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainPage.class.getResource("/com/example/transittrackerlite/StopView.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            System.out.println("Error loading StopView.fxml: ");
            e.printStackTrace();
            return;
        }
        StationViewController controller = loader.getController();
        if (controller != null) {
            controller.setStationInfo(station);
        }
        Scene scene = new Scene(root);
        stage.setScene(scene);
    }

    public static void changeToMainPage(Stage stage) {
        FXMLLoader loader = new FXMLLoader(MainPage.class.getResource("HomePage.fxml"));
        try {
            loader.load();
            MainPageController controller = loader.getController(); 
            controller.startArrivalTimeUpdates();
            stage.setScene(new Scene(loader.getRoot()));
        } catch (Exception e) {
            e.getMessage();
        }
    }

}
    


