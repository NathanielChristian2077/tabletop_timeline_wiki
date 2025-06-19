module me {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    // requires org.graphstream.core;
    //    requires gs.ui.javafx;

    opens me.gui to javafx.fxml;
    opens me.gui.controladores to javafx.fxml;
    exports me.gui;
    exports me.gui.controladores;
    exports me.modelo.entidades;
    exports me.modelo.abstracts;
    exports me.modelo.enums;
}