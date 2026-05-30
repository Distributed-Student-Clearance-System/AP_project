package com.distributedclearance.gui.screens.admin;

import java.util.List;

import com.distributedclearance.database.dao.RequestDAO;
import com.distributedclearance.database.dao.UserDAO;
import com.distributedclearance.gui.screens.BaseScreen;
import com.distributedclearance.models.Admin;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;

public class AdminDashboard extends BaseScreen {
    private final Admin admin;

    private final UserDAO userDAO = new UserDAO();
    private final RequestDAO requestDAO = new RequestDAO();

    private Label totalUsersLabel;
    private Label totalRequestsLabel;
    private Label totalApprovedLabel;
    private Label totalRejectedLabel;
    private Label totalPendingLabel;
    private TextArea detailsArea;

    public AdminDashboard(Admin admin) {
        this.admin = admin;
        initialize();
    }

    @Override
    protected void initialize() {
        VBox container = new VBox(15);
        container.setPadding(new Insets(20));
        container.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Admin Dashboard");

        title.setStyle(
                "-fx-font-size: 24px;" +
                "-fx-font-weight: bold;"
        );

        Label welcomeLabel = new Label("Welcome, " + admin.getFullName());

        totalUsersLabel = new Label();
        totalRequestsLabel = new Label();
        totalApprovedLabel = new Label();
        totalRejectedLabel = new Label();
        totalPendingLabel = new Label();

        styleStatLabel(totalUsersLabel);
        styleStatLabel(totalRequestsLabel);
        styleStatLabel(totalApprovedLabel);
        styleStatLabel(totalRejectedLabel);
        styleStatLabel(totalPendingLabel);

        VBox statsBox = new VBox(10);
        statsBox.setAlignment(Pos.CENTER_LEFT);
        statsBox.getChildren().addAll(
                totalUsersLabel,
                totalRequestsLabel,
                totalApprovedLabel,
                totalRejectedLabel,
                totalPendingLabel
        );

        Button viewUsersButton = new Button("View All Users");
        Button viewRequestsButton = new Button("View All Requests");
        Button refreshButton = new Button("Refresh Statistics");

        viewUsersButton.setPrefWidth(180);
        viewRequestsButton.setPrefWidth(180);
        refreshButton.setPrefWidth(180);

        HBox buttonRow = new HBox(15);
        buttonRow.setAlignment(Pos.CENTER);
        buttonRow.getChildren().addAll(
                viewUsersButton,
                viewRequestsButton,
                refreshButton
        );

        detailsArea = new TextArea();
        detailsArea.setEditable(false);
        detailsArea.setWrapText(true);
        detailsArea.setPrefHeight(260);
        detailsArea.setPromptText("Use the buttons above to view users or requests.");

        viewUsersButton.setOnAction(event -> {
            List<String> users = userDAO.getAllUsersSummary();
            detailsArea.setText(buildSectionText("All Users", users));
        });

        viewRequestsButton.setOnAction(event -> {
            List<String> requests = requestDAO.getAllRequestsSummary();
            detailsArea.setText(buildSectionText("All Requests", requests));
        });

        refreshButton.setOnAction(event -> {
            refreshStatistics();
        });

        container.getChildren().addAll(
                title,
                welcomeLabel,
                statsBox,
                buttonRow,
                detailsArea
        );

        setCenter(container);

        refreshStatistics();
    }

    private void refreshStatistics() {
        totalUsersLabel.setText("Total Users: " + userDAO.getTotalUsers());
        totalRequestsLabel.setText("Total Clearance Requests: " + requestDAO.getTotalRequests());
        totalApprovedLabel.setText("Total Approved Requests: " + requestDAO.getApprovedRequests());
        totalRejectedLabel.setText("Total Rejected Requests: " + requestDAO.getRejectedRequests());
        totalPendingLabel.setText("Total Pending Requests: " + requestDAO.getPendingRequests());
    }

    private void styleStatLabel(Label label) {
        label.setStyle(
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;"
        );
    }

    private String buildSectionText(String title, List<String> items) {
        StringBuilder builder = new StringBuilder(title).append("\n\n");

        if (items == null || items.isEmpty()) {
            builder.append("No records found.");
            return builder.toString();
        }

        for (String item : items) {
            builder.append(item).append("\n");
        }

        return builder.toString();
    }

    public Scene createScene() {
        return new Scene(this, 1000, 700);
    }
}