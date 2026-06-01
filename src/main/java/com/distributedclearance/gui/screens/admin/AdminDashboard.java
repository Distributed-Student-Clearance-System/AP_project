package com.distributedclearance.gui.screens.admin;

import com.distributedclearance.database.dao.RequestDAO;
import com.distributedclearance.database.dao.UserDAO;
import com.distributedclearance.gui.navigation.SceneManager;
import com.distributedclearance.gui.screens.BaseScreen;
import com.distributedclearance.models.Admin;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Priority;
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
    private TableView<String> usersTable;
    private TableView<String> requestsTable;

    public AdminDashboard(Admin admin) {
        this.admin = admin;
        initialize();
    }

    @Override
    protected void initialize() {
        VBox container = new VBox(16);
        container.setPadding(new Insets(20));
        container.setFillWidth(true);
        container.getStyleClass().add("dashboard-card");

        Label title = new Label("Admin Dashboard");
        title.getStyleClass().add("app-title");

        Label welcomeLabel = new Label("Welcome, " + admin.getFullName());
        welcomeLabel.getStyleClass().add("welcome-label");

        Label statisticsTitle = new Label("Statistics");
        statisticsTitle.getStyleClass().add("section-title");

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
        statsBox.setFillWidth(true);
        statsBox.getChildren().addAll(
                totalUsersLabel,
                totalRequestsLabel,
                totalApprovedLabel,
                totalRejectedLabel,
                totalPendingLabel
        );
        statsBox.getStyleClass().add("section-card");

        Label usersTitle = new Label("Users Table");
        usersTitle.getStyleClass().add("section-title");

        usersTable = new TableView<>();
        usersTable.setPlaceholder(new Label("No users found."));
        usersTable.getStyleClass().add("dark-table");
        usersTable.setMaxWidth(Double.MAX_VALUE);

        TableColumn<String, String> userSummaryColumn = new TableColumn<>("User Summary");
        userSummaryColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue())
        );
        usersTable.getColumns().add(userSummaryColumn);

        Label requestsTitle = new Label("Requests Table");
        requestsTitle.getStyleClass().add("section-title");

        requestsTable = new TableView<>();
        requestsTable.setPlaceholder(new Label("No requests found."));
        requestsTable.getStyleClass().add("dark-table");
        requestsTable.setMaxWidth(Double.MAX_VALUE);

        TableColumn<String, String> requestSummaryColumn = new TableColumn<>("Request Summary");
        requestSummaryColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue())
        );
        requestsTable.getColumns().add(requestSummaryColumn);

        Button refreshButton = new Button("Refresh Statistics");

        refreshButton.getStyleClass().add("primary-button");

        HBox buttonRow = new HBox(15);
        buttonRow.setFillHeight(true);
        buttonRow.getChildren().add(refreshButton);

        VBox.setVgrow(usersTable, Priority.ALWAYS);
        VBox.setVgrow(requestsTable, Priority.ALWAYS);

        refreshButton.setOnAction(event -> {
            refreshStatistics();
        });

        container.getChildren().addAll(
                title,
                welcomeLabel,
                statisticsTitle,
                statsBox,
                buttonRow,
                usersTitle,
                usersTable,
                requestsTitle,
                requestsTable
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

        usersTable.setItems(FXCollections.observableArrayList(userDAO.getAllUsersSummary()));
        requestsTable.setItems(FXCollections.observableArrayList(requestDAO.getAllRequestsSummary()));
    }

    private void styleStatLabel(Label label) {
        label.setStyle(
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;"
        );
    }

    public Scene createScene() {
        Scene scene = new Scene(this, 1000, 700);
        SceneManager.applyTheme(scene);
        return scene;
    }
}