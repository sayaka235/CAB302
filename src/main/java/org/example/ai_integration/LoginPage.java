/*package org.example.ai_integration;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class LoginPage {
    public Parent createRoot(){
        VBox root = new VBox();
        root.setAlignment(javafx.geometry.Pos.CENTER);
        root.setSpacing(15.0);
        root.setFillWidth(false);
        // Create a TextField, a Label, and an HBox with appropriate
        // text
        Label logHead = new Label("Log In");
        TextField emailTextField = new TextField();
        Label entEmail = new Label("Enter email");
        emailTextField.setText("email:");
        TextField passTextField = new TextField();
        passTextField.setText("password: ");
        Label entpass = new Label("Enter Password");
        // The HBox is used to hold the buttons
        VBox vbox = new VBox();
        vbox.setAlignment(javafx.geometry.Pos.CENTER);
        vbox.setSpacing(15.0);
        Button buttonSignUp = new Button("No account? Sign Up!");
        Button buttonLogIn = new Button("Log in");
        }

        Button buttonSignUp = new Button("No account? Sign Up!");
        Button button2 = new Button("Log in");

        //Action when signup clicked
        buttonSignUp.setOnAction(event ->{

        });
        // Add the buttons to the HBox
        vbox.getChildren().addAll(buttonSignUp, button2);
        // Add the children to the root vbox
        root.getChildren().addAll(logHead, entEmail,emailTextField,entpass, passTextField, vbox);
        // Define the scene, add to the stage (window) and show the stage
        Scene sceneLogin = new Scene(root, 500, 300);

        stage.setScene(sceneLogin);
        stage.setTitle("Quiz AI App");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}*/