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
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

public class StudentDashboard extends BaseScreen implements NotificationListener {

    private final Student student;

    private final RequestDAO requestDAO = new RequestDAO();
    private final ApprovalDAO approvalDAO = new ApprovalDAO();

    private Label overallStatusLabel;
    private ListView<String> notificationHistoryList;
    private TableView<ClearanceRequest> requestHistoryTable;
    private ListView<String> approvalList;

    private int currentRequestId = -1;

    public StudentDashboard(Student student) {
        this.student = student;
        initialize();
    }

    @Override
    protected void initialize() {

        VBox container = new VBox(20);

        container.setPadding(new Insets(20));
        container.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Student Dashboard");

        title.setStyle(
            "-fx-font-size: 24px;" +
            "-fx-font-weight: bold;"
        );

        Label welcomeLabel =
                new Label("Welcome, " + student.getFullName());

        overallStatusLabel =
                new Label("Overall Clearance Status: PENDING");

        overallStatusLabel.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;"
        );

        Label notificationHistoryTitle =
                new Label("Notification History");

        notificationHistoryTitle.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;"
        );

        notificationHistoryList = new ListView<>();
        notificationHistoryList.setPrefHeight(160);
        notificationHistoryList.setPlaceholder(
                new Label("No notifications yet.")
        );

        Button submitButton =
                new Button("Submit Clearance Request");

        submitButton.setPrefWidth(250);

        Label statusLabel = new Label();

        approvalList = new ListView<>();
        approvalList.setPrefHeight(300);

        Label requestHistoryTitle = new Label("Request History");
        requestHistoryTitle.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;"
        );

        requestHistoryTable = new TableView<>();
        requestHistoryTable.setPrefHeight(220);
        requestHistoryTable.setPlaceholder(
                new Label("No requests submitted yet.")
        );

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

        requestHistoryTable.getColumns().addAll(
            requestIdColumn,
            submittedAtColumn,
            overallRequestStatusColumn
        );

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

        container.getChildren().addAll(
            title,
            welcomeLabel,
            notificationHistoryTitle,
            notificationHistoryList,
            overallStatusLabel,
            submitButton,
            statusLabel,
            requestHistoryTitle,
            requestHistoryTable,
            approvalList
        );

        setCenter(container);

        NotificationClient client = new NotificationClient(this);

        Thread notificationThread = new Thread(client);

        notificationThread.setDaemon(true);
        notificationThread.start();
        loadRequestHistory();
    }

        private void loadRequestHistory() {

            List<ClearanceRequest> requests =
                            requestDAO.getRequestsByStudentId(student.getId());

            ObservableList<ClearanceRequest> historyItems =
                            FXCollections.observableArrayList(requests);

            requestHistoryTable.setItems(historyItems);
        }

    private void loadApprovals() {

        approvalList.getItems().clear();

        List<Approval> approvals =
                approvalDAO.getApprovalsByRequestId(
                        currentRequestId
                );

        for (Approval approval : approvals) {

            approvalList.getItems().add(
                    approval.getDepartment()
                    + " → "
                    + approval.getStatus()
            );
        }

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
        return new Scene(this, 1000, 700);
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