package com.distributedclearance.gui.screens.student;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.distributedclearance.database.dao.ApprovalDAO;
import com.distributedclearance.database.dao.RequestDAO;
import com.distributedclearance.gui.screens.BaseScreen;
import com.distributedclearance.models.Approval;
import com.distributedclearance.models.ClearanceRequest;
import com.distributedclearance.models.Student;
import com.distributedclearance.models.enums.ApprovalStatus;
import com.distributedclearance.models.enums.RequestStatus;
import com.distributedclearance.server.networking.NotificationClient;
import com.distributedclearance.server.networking.NotificationListener;
import com.distributedclearance.server.networking.SocketClient;

import javafx.application.Platform;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import com.distributedclearance.gui.navigation.SceneManager;

public class StudentDashboard extends BaseScreen implements NotificationListener {

    private final Student student;

    private final RequestDAO requestDAO = new RequestDAO();
    private final ApprovalDAO approvalDAO = new ApprovalDAO();

    private Label overallStatusLabel;
    private ListView<String> notificationHistoryList;
    private TableView<ClearanceRequest> requestHistoryTable;
        private TableView<Approval> approvalTable;
        private Timeline refreshTimeline;

    private int currentRequestId = -1;

    public StudentDashboard(Student student) {
        this.student = student;
        initialize();
    }

    @Override
    protected void initialize() {
        VBox content = new VBox(18);
        content.setPadding(new Insets(20));
        content.setFillWidth(true);
        content.getStyleClass().add("dashboard-card");

        Label title = new Label("Student Dashboard");
        title.getStyleClass().add("app-title");

        Label welcomeLabel = new Label("Welcome, " + student.getFullName());
        welcomeLabel.getStyleClass().add("welcome-label");

        overallStatusLabel = new Label("Overall Clearance Status: PENDING");
        overallStatusLabel.getStyleClass().add("status-label");

        VBox header = new VBox(6, title, welcomeLabel, overallStatusLabel);
        header.setFillWidth(true);

        Label notificationHistoryTitle = new Label("Notification History");
        notificationHistoryTitle.getStyleClass().add("section-title");

        notificationHistoryList = new ListView<>();
        notificationHistoryList.setPlaceholder(
                new Label("No notifications yet.")
        );
        notificationHistoryList.getStyleClass().add("data-list");
        notificationHistoryList.setMaxWidth(Double.MAX_VALUE);

        Button submitButton = new Button("Submit Clearance Request");
        submitButton.getStyleClass().add("primary-button");
        submitButton.setMaxWidth(Double.MAX_VALUE);

        Label statusLabel = new Label();
        statusLabel.getStyleClass().add("status-label");

        Label approvalDetailsTitle = new Label("Department Approval Details");
        approvalDetailsTitle.getStyleClass().add("section-title");

        approvalTable = new TableView<>();
        approvalTable.setPlaceholder(
                new Label("No approval details available yet.")
        );
        approvalTable.getStyleClass().add("dark-table");
        approvalTable.setMaxWidth(Double.MAX_VALUE);

        TableColumn<Approval, String> departmentColumn =
                new TableColumn<>("Department");
        departmentColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getDepartment().name()
                )
        );

        TableColumn<Approval, String> statusColumn =
                new TableColumn<>("Status");
        statusColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getStatus().name()
                )
        );

        TableColumn<Approval, String> commentColumn =
                new TableColumn<>("Officer Comment / Reason");
        commentColumn.setCellValueFactory(cellData -> {
            String comment = cellData.getValue().getComment();
            if (comment == null || comment.trim().isEmpty()) {
                comment = "No comment provided.";
            }

            return new SimpleStringProperty(comment);
        });

        approvalTable.getColumns().add(departmentColumn);
        approvalTable.getColumns().add(statusColumn);
        approvalTable.getColumns().add(commentColumn);

        Label requestHistoryTitle = new Label("Request History");
        requestHistoryTitle.getStyleClass().add("section-title");

        requestHistoryTable = new TableView<>();
        requestHistoryTable.setPlaceholder(
                new Label("No requests submitted yet.")
        );
        requestHistoryTable.getStyleClass().add("dark-table");
        requestHistoryTable.setMaxWidth(Double.MAX_VALUE);

        TableColumn<ClearanceRequest, Integer> requestIdColumn =
                new TableColumn<>("Request ID");
        requestIdColumn.setCellValueFactory(
                new PropertyValueFactory<>("requestId")
        );

        TableColumn<ClearanceRequest, String> submittedAtColumn =
                new TableColumn<>("Submission Date");

        submittedAtColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(
                    formatSubmittedAt(
                            cellData.getValue().getSubmittedAt()
                    )
            )
        );

        TableColumn<ClearanceRequest, String> overallRequestStatusColumn =
                new TableColumn<>("Overall Status");

        overallRequestStatusColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        formatOverallRequestStatus(
                                cellData.getValue().getStatus()
                        )
                )
        );

                requestHistoryTable.getColumns().add(requestIdColumn);
                requestHistoryTable.getColumns().add(submittedAtColumn);
                requestHistoryTable.getColumns().add(overallRequestStatusColumn);

                VBox notificationSection = new VBox(8, notificationHistoryTitle, notificationHistoryList);
                notificationSection.setFillWidth(true);

                VBox requestSection = new VBox(8, requestHistoryTitle, requestHistoryTable);
                requestSection.setFillWidth(true);

                VBox approvalSection = new VBox(8, approvalDetailsTitle, approvalTable);
                approvalSection.setFillWidth(true);

                VBox actionSection = new VBox(10, submitButton, statusLabel);
                actionSection.setFillWidth(true);

        submitButton.setOnAction(event -> {
            boolean success = requestDAO.submitRequest(student);

            if (success) {
                currentRequestId = requestDAO.getLatestRequestId(student.getId());

                SocketClient.sendMessage(
                    currentRequestId + ":"
                    + student.getFullName()
                );

                statusLabel.setText("Clearance request submitted successfully.");

                loadApprovals();
                loadRequestHistory();

            } else {
                statusLabel.setText("Failed to submit request.");
            }
        });

        content.getChildren().addAll(
                header,
                notificationSection,
                actionSection,
                requestSection,
                approvalSection
        );

        VBox.setVgrow(notificationHistoryList, Priority.ALWAYS);
        VBox.setVgrow(requestHistoryTable, Priority.ALWAYS);
        VBox.setVgrow(approvalTable, Priority.ALWAYS);

        setCenter(content);

        NotificationClient client = new NotificationClient(this);

        Thread notificationThread = new Thread(client);

        notificationThread.setDaemon(true);
        notificationThread.start();
        loadRequestHistory();
        refreshTimeline = new Timeline(
                new KeyFrame(Duration.seconds(5), event -> {
                    loadRequestHistory();
                    if (currentRequestId != -1) {
                        loadApprovals();
                    }
                })
        );
        refreshTimeline.setCycleCount(Timeline.INDEFINITE);
        refreshTimeline.play();
    }

    private void loadRequestHistory() {

        List<ClearanceRequest> requests =
                requestDAO.getRequestsByStudentId(student.getId());

        ObservableList<ClearanceRequest> historyItems =
                FXCollections.observableArrayList(requests);

        requestHistoryTable.setItems(historyItems);
    }

    private void loadApprovals() {

        approvalTable.getItems().clear();

        if (currentRequestId == -1) {
            overallStatusLabel.setText("Overall Clearance Status: PENDING");
            return;
        }

        List<Approval> approvals =
                approvalDAO.getApprovalsByRequestId(
                        currentRequestId
                );

        ObservableList<Approval> approvalItems =
                FXCollections.observableArrayList(approvals);

        approvalTable.setItems(approvalItems);

                updateOverallStatus(approvals);
        }

        private void updateOverallStatus(List<Approval> approvals) {
                String overallStatus = "PENDING";

                if (approvals != null && !approvals.isEmpty()) {

                        boolean allApproved = true;

                        for (Approval approval : approvals) {

                                if (approval.getStatus() == ApprovalStatus.REJECTED) {
                                        overallStatus = "REJECTED";
                                        allApproved = false;
                                        break;
                                }

                                if (approval.getStatus() != ApprovalStatus.APPROVED) {
                                        allApproved = false;
                                }
                        }

                        if (allApproved) {
                                overallStatus = "CLEARED";
            }
                }

                overallStatusLabel.setText(
                                "Overall Clearance Status: " + overallStatus
                );
    }

        private String formatSubmittedAt(LocalDateTime submittedAt) {
                if (submittedAt == null) {
                        return "N/A";
                }

                return submittedAt.format(
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                );
        }

        private String formatOverallRequestStatus(RequestStatus status) {
                if (status == null) {
                        return "PENDING";
                }

                if (status == RequestStatus.FULLY_APPROVED) {
                        return "CLEARED";
                }

                return status.name();
        }

    public Scene createScene() {
                Scene scene = new Scene(this, 1000, 700);
                SceneManager.applyTheme(scene);
                return scene;
    }

    @Override
    public void onNotificationReceived(String message) {

        Platform.runLater(() -> {

            String timestamp = LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("HH:mm:ss")
            );

            String notificationEntry =
                    "[" + timestamp + "] " + message;

            System.out.println(
                    "UI RECEIVED NOTIFICATION: "
                    + notificationEntry
            );

            notificationHistoryList.getItems().add(0, notificationEntry);

                        loadRequestHistory();

            if (currentRequestId != -1) {
                loadApprovals();
            }
        });
    }
}