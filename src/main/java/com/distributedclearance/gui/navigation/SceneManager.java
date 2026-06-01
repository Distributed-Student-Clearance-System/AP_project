package com.distributedclearance.gui.navigation;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {
    private static Stage primaryStage;
    private static final String DARK_THEME_PATH = "/styles/dark-theme.css";

    private SceneManager() {}

    public static void applyTheme(Scene scene) {
        if (scene == null) {
            return;
        }

        var themeUrl = SceneManager.class.getResource(DARK_THEME_PATH);

        if (themeUrl == null) {
            return;
        }

        String stylesheet = themeUrl.toExternalForm();

        if (!scene.getStylesheets().contains(stylesheet)) {
            scene.getStylesheets().add(stylesheet);
        }
    }

    public static void setStage(Stage stage) {
        primaryStage = stage;
    }

    public static void switchScene(Scene scene) {
        applyTheme(scene);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}