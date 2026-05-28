package com.distributedclearance.gui.navigation;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {
    private static Stage primaryStage;
    private SceneManager() {}

    public static void setStage(Stage stage) {
        primaryStage = stage;
    }

    public static void switchScene(Scene scene) {
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}