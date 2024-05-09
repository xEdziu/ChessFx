module eddy.chessfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires batik.transcoder;
    requires javafx.swing;


    opens eddy.chessfx to javafx.fxml;
    exports eddy.chessfx;
    exports eddy.chessfx.ui;
    opens eddy.chessfx.ui to javafx.fxml;
}