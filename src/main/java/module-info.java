module org.example.ai_integration {
    requires javafx.controls;
    requires javafx.fxml;
    //added stuff
    requires okhttp3;
    requires com.google.gson;
    requires org.apache.pdfbox;
    requires org.apache.poi.ooxml;

    // sql database
    requires java.sql;
    requires java.net.http;
    requires org.jfree.jfreechart;
    requires java.desktop;

    opens org.example.ai_integration to javafx.fxml;
    exports org.example.ai_integration;
    exports org.example.ai_integration.model;
    opens org.example.ai_integration.model to javafx.fxml;
    exports org.example.ai_integration.controls;
    opens org.example.ai_integration.controls to javafx.fxml;
    

}
