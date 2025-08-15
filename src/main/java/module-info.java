module org.example.ai_integration {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    //added stuff
    requires okhttp3;
    requires com.google.gson;
    requires org.apache.pdfbox;
    requires org.apache.poi.ooxml;

    opens org.example.ai_integration to javafx.fxml;
    exports org.example.ai_integration;

}
