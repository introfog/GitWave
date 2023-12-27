module com.github.introfog.gitwave {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.prefs;
    requires java.naming;
    requires org.slf4j;
    requires ch.qos.logback.classic;
    requires ch.qos.logback.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;

    opens com.github.introfog.gitwave to javafx.fxml;
    opens com.github.introfog.gitwave.model.dto to com.fasterxml.jackson.core, com.fasterxml.jackson.annotation, com.fasterxml.jackson.databind, javafx.base;
    opens com.github.introfog.gitwave.controller to javafx.fxml;

    exports com.github.introfog.gitwave;
}