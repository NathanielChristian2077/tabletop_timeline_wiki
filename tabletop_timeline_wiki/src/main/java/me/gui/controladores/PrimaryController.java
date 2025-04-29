package me.gui.controladores;

import java.io.IOException;
import javafx.fxml.FXML;
import me.gui.App;

public class PrimaryController {

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }
}
