package com.distributedclearance.gui.screens.admin;

import com.distributedclearance.gui.screens.BaseScreen;
import com.distributedclearance.models.Admin;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class AdminDashboard extends BaseScreen {
    private final Admin admin;

    public AdminDashboard(Admin admin) {
        this.admin = admin;
        initialize();
    }

    @Override
    protected void initialize() {
        Label title = new Label("Admin Dashboard");

        title.setStyle(
                "-fx-font-size: 24px;" +
                "-fx-font-weight: bold;"
        );

        Label welcomeLabel = new Label("Welcome, " + admin.getFullName());

        VBox container = new VBox(20);

        container.setAlignment(Pos.CENTER);
        container.getChildren().addAll(title, welcomeLabel);
        setCenter(container);
    }

    public Scene createScene() {
        return new Scene(this, 1000, 700);
    }
}