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
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

public class LoginScreen extends BaseScreen {

    private final AuthenticationService authService;

    public LoginScreen() {
        authService = new AuthenticationService();
        initialize();
    }

    @Override
    protected void initialize() {
        Label title = new Label("Distributed Student Clearance System");
        title.getStyleClass().addAll("app-title", "login-title");
        title.setWrapText(true);
        title.setAlignment(Pos.CENTER);
        title.setTextAlignment(TextAlignment.CENTER);
        title.setMaxWidth(560);

        Label subtitle = new Label("Login to continue");
        subtitle.getStyleClass().add("subtitle-label");

        Label usernameIcon = new Label("👤");
        usernameIcon.getStyleClass().add("input-icon");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setPrefWidth(360);
        usernameField.getStyleClass().add("form-input");
        usernameField.setMaxWidth(Double.MAX_VALUE);

        HBox usernameGroup = new HBox(10, usernameIcon, usernameField);
        usernameGroup.getStyleClass().add("input-group");

        Label passwordIcon = new Label("🔒");
        passwordIcon.getStyleClass().add("input-icon");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setPrefWidth(360);
        passwordField.getStyleClass().add("form-input");
        passwordField.setMaxWidth(Double.MAX_VALUE);

        HBox passwordGroup = new HBox(10, passwordIcon, passwordField);
        passwordGroup.getStyleClass().add("input-group");

        Label statusLabel = new Label();
        statusLabel.getStyleClass().add("error-label");
        statusLabel.setWrapText(true);
        statusLabel.setMaxWidth(Double.MAX_VALUE);

        Button loginButton = new Button("Login");
        loginButton.setPrefWidth(360);
        loginButton.getStyleClass().addAll("primary-button", "login-button");
        loginButton.setMaxWidth(Double.MAX_VALUE);

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

                    default -> statusLabel.setText("Unsupported user role.");
                }

            } else {
                statusLabel.setText("Invalid username or password");
            }
        });

        VBox loginCard = new VBox(14);

        loginCard.setAlignment(Pos.CENTER);
        loginCard.setPadding(new Insets(36));
        loginCard.setPrefWidth(620);
        loginCard.setMaxWidth(620);
        loginCard.getStyleClass().addAll("auth-card", "login-card");

        loginCard.getChildren().addAll(
            title,
            subtitle,
            usernameGroup,
            passwordGroup,
            loginButton,
            statusLabel
        );

        StackPane root = new StackPane(loginCard);
        root.setAlignment(Pos.CENTER);
        setCenter(root);
    }

    public Scene createScene() {
        Scene scene = new Scene(this, 1000, 700);
        SceneManager.applyTheme(scene);
        return scene;
    }
}