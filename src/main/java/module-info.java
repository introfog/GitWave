module com.github.introfog.rgit {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.prefs;
    requires java.naming;
    requires org.slf4j;
    requires ch.qos.logback.classic;
    requires ch.qos.logback.core;

    opens com.github.introfog.rgit to javafx.fxml;
    opens com.github.introfog.rgit.controller to javafx.fxml;

    exports com.github.introfog.rgit;
    exports com.github.introfog.rgit.controller;
}