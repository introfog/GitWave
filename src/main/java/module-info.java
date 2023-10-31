module com.github.introfog.rgit {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.prefs;

    opens com.github.introfog.rgit to javafx.fxml;

    exports com.github.introfog.rgit;
    exports com.github.introfog.rgit.controller;
    opens com.github.introfog.rgit.controller to javafx.fxml;
}