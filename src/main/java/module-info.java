module com.github.introfog.rgit {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;

    opens com.github.introfog.rgit to javafx.fxml;
    exports com.github.introfog.rgit;
}