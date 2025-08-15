package org.example.ai_integration;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import javafx.util.StringConverter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


public class SignupPage extends Application {
    @Override
    public void start(Stage stage) {
        // Define the root node and set the alignment and spacing
        // properties. Also, set the fillWidth property to false
        // so the children are not resized to fill the width of the
        // VBox.
        VBox root = new VBox();
        root.setAlignment(javafx.geometry.Pos.CENTER);
        root.setSpacing(15.0);
        root.setFillWidth(false);
        // Create a TextField, a Label, and an HBox with appropriate

        Label logHead = new Label("Sign Up");
        //Email
        TextField emailtextField = new TextField();
        Label label = new Label("Enter Email");
        emailtextField.setText("name@example.com");
        Label labelFName = new Label("Enter First Name");
        Label labelLName = new Label("Enter Last Name:");



        TextField passtextField = new TextField();
        passtextField.setText("password: ");
        Label labelnew = new Label("Enter Password");
        // The HBox is used to hold the buttons
        VBox vbox = new VBox();
        vbox.setAlignment(javafx.geometry.Pos.CENTER);
        vbox.setSpacing(15.0);
        Button button1 = new Button("Have an account? Log in!");
        Button button2 = new Button("Sign up");
        // Add the buttons to the HBox
        vbox.getChildren().addAll(button1, button2);
        // Add the children to the root vbox
        root.getChildren().addAll(label,emailtextField,labelnew, passtextField, vbox);
        // Define the scene, add to the stage (window) and show the stage
        Scene scene = new Scene(root, 500, 300);
        stage.setScene(scene);
        stage.setTitle("JavaFX Example Scene");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
