module me {
    requires javafx.controls;
    requires javafx.fxml;

    opens me to javafx.fxml;
    exports me;
}
