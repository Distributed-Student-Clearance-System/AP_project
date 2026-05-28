package com.distributedclearance;

import com.distributedclearance.gui.navigation.SceneManager;
import com.distributedclearance.gui.screens.auth.LoginScreen;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        SceneManager.setStage(stage);

        LoginScreen loginScreen = new LoginScreen();

        stage.setTitle("Distributed Student Clearance System");

        stage.setScene(loginScreen.createScene());
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}