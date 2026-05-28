package com.distributedclearance.gui.screens.auth;

import com.distributedclearance.gui.navigation.SceneManager;
import com.distributedclearance.gui.screens.BaseScreen;
import com.distributedclearance.gui.screens.admin.AdminDashboard;
import com.distributedclearance.gui.screens.officer.OfficerDashboard;
import com.distributedclearance.gui.screens.student.StudentDashboard;
import com.distributedclearance.models.Admin;
import com.distributedclearance.models.DeptOfficer;
import com.distributedclearance.models.Student;
import com.distributedclearance.models.User;
import com.distributedclearance.server.managers.AuthenticationService;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class LoginScreen extends BaseScreen {

    private final AuthenticationService authService;

    public LoginScreen() {
        authService = new AuthenticationService();
        initialize();
    }

    @Override
    protected void initialize() {
        Label title = new Label("Distributed Student Clearance System");
        title.setStyle(
                "-fx-font-size: 28px;" +
                "-fx-font-weight: bold;"
        );

        Label subtitle = new Label("Login to continue");
        subtitle.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-text-fill: gray;"
        );

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setMaxWidth(300);
        usernameField.setPrefHeight(40);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(300);
        passwordField.setPrefHeight(40);

        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: red;");

        Button loginButton = new Button("Login");
        loginButton.setPrefWidth(300);
        loginButton.setPrefHeight(40);

        loginButton.setStyle(
                "-fx-background-color: #2563eb;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;"
        );

        loginButton.setOnAction(event -> {

            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();

            if (username.isEmpty() || password.isEmpty()) {
                statusLabel.setText("Please fill all fields");
                return;
            }

            User user = authService.login(username, password);

            if (user != null) {
                switch (user.getRole()) {
                    case STUDENT -> {
                        StudentDashboard studentDashboard =
                                new StudentDashboard((Student) user);

                        SceneManager.switchScene(
                                studentDashboard.createScene()
                        );
                    }

                    case OFFICER -> {
                        OfficerDashboard officerDashboard =
                                new OfficerDashboard((DeptOfficer) user);

                        SceneManager.switchScene(
                                officerDashboard.createScene()
                        );
                    }

                    case ADMIN -> {
                        AdminDashboard adminDashboard =
                                new AdminDashboard((Admin) user);

                        SceneManager.switchScene(
                                adminDashboard.createScene()
                        );
                    }
                }

            } else {
                statusLabel.setText("Invalid username or password");
            }
        });

        VBox container = new VBox(15);

        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(40));

        container.getChildren().addAll(
                title,
                subtitle,
                usernameField,
                passwordField,
                loginButton,
                statusLabel
        );

        setCenter(container);
    }

    public Scene createScene() {
        return new Scene(this, 1000, 700);
    }
}