module com.group17.seg {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.graphics;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.jetbrains.annotations;
    requires java.desktop;
    requires java.xml.bind;
    requires com.sun.xml.bind;
    requires javafx.swing;

    opens com.group17.seg.model to java.xml.bind;
    opens com.group17.seg to javafx.fxml;
    exports com.group17.seg;
    exports com.group17.seg.model;
    exports com.group17.seg.utility to javafx.graphics;
}