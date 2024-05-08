module eddy.chessfx {
    requires javafx.controls;
    requires javafx.fxml;


    opens eddy.chessfx to javafx.fxml;
    exports eddy.chessfx;
    exports eddy.chessfx.ui;
    opens eddy.chessfx.ui to javafx.fxml;
}