package me.gui.controladores;

import java.io.IOException;
import javafx.fxml.FXML;
import me.gui.App;

public class SecondaryController {

    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }
}