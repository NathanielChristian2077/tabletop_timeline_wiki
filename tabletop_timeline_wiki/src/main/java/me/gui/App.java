package me.gui;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

/**
 * Classe principal que inicia a aplicação e carrega a tela de login.
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        System.out.println("Tentando carregar: " + App.class.getResource("Login.fxml"));
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Login.fxml"));
        Parent root = fxmlLoader.load();

        //Transição fade in
        FadeTransition fadein = new FadeTransition(Duration.millis(1200));
        fadein.setFromValue(0.0);
        fadein.setToValue(1.0);
        fadein.play();

        Scene scene = new Scene(root, 640, 480);
        scene.getStylesheets().add(getClass().getResource("/css/login.css").toExternalForm());
        stage.setTitle("RPG Campaign Manager - Login");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
