module me {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;

    opens me.gui to javafx.fxml;
    exports me.gui;
}