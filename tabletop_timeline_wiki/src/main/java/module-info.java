module me {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;

    opens me.gui to javafx.fxml;
    opens me.gui.controladores to javafx.fxml;
    exports me.gui;
    exports me.gui.controladores;
}