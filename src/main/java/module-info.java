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


    opens org.example.ai_integration to javafx.fxml;
    exports org.example.ai_integration;

}
