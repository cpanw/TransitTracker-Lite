module com.example.transittrackerlite {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires org.jfxtras.styles.jmetro;
    requires com.pixelduke.fxskins;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires org.json;
    requires javafx.base;
    requires transitive javafx.graphics;

    opens com.example.transittrackerlite to javafx.fxml;
    exports com.example.transittrackerlite;
}