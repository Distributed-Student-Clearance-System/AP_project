package com.distributedclearance.gui.screens.officer;

import java.util.List;

import com.distributedclearance.database.dao.ApprovalDAO;
import com.distributedclearance.gui.screens.BaseScreen;
import com.distributedclearance.models.Approval;
import com.distributedclearance.models.OfficerRequestRecord;
import com.distributedclearance.models.DeptOfficer;
import com.distributedclearance.models.enums.ApprovalStatus;
import com.distributedclearance.server.networking.NotificationSender;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TextArea;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class OfficerDashboard extends BaseScreen {

    private final DeptOfficer officer;

    private final ApprovalDAO approvalDAO = new ApprovalDAO();

        private TableView<OfficerRequestRecord> requestTable;
        private Label statusLabel;
        private TextArea commentArea;
                private Timeline refreshTimeline;

    public OfficerDashboard(DeptOfficer officer) {
        this.officer = officer;
        initialize();
    }

    @Override
    protected void initialize() {

        VBox container = new VBox(15);
        container.setPadding(new Insets(20));
        container.setAlignment(Pos.TOP_CENTER);

        Label title = new Label(
                officer.getDepartment() + " Officer Dashboard"
        );

        title.setStyle(
                "-fx-font-size: 24px;" +
                "-fx-font-weight: bold;"
        );

        statusLabel = new Label("Select a pending request and choose a decision.");

        requestTable = new TableView<>();
        requestTable.setPrefHeight(320);
        requestTable.setPlaceholder(
                new Label("No pending requests for your department.")
        );

        TableColumn<OfficerRequestRecord, String> studentNameColumn =
                new TableColumn<>("Student Name");
        studentNameColumn.setCellValueFactory(
                new PropertyValueFactory<>("studentName")
        );

        TableColumn<OfficerRequestRecord, Integer> requestIdColumn =
                new TableColumn<>("Request ID");
        requestIdColumn.setCellValueFactory(
                new PropertyValueFactory<>("requestId")
        );

        TableColumn<OfficerRequestRecord, String> currentStatusColumn =
                new TableColumn<>("Current Status");
        currentStatusColumn.setCellValueFactory(
                new PropertyValueFactory<>("status")
        );

        requestTable.getColumns().addAll(
                studentNameColumn,
                requestIdColumn,
                currentStatusColumn
        );

        commentArea = new TextArea();
        commentArea.setPromptText("Enter comment...");
        commentArea.setPrefHeight(120);

        Button approveButton = new Button("Approve");
        Button rejectButton = new Button("Reject");

        approveButton.setPrefWidth(120);
        rejectButton.setPrefWidth(120);

        approveButton.setStyle(
                "-fx-background-color: green;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;"
        );

        rejectButton.setStyle(
                "-fx-background-color: red;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;"
        );

        approveButton.setOnAction(event -> handleDecision(ApprovalStatus.APPROVED));

        rejectButton.setOnAction(event -> handleDecision(ApprovalStatus.REJECTED));

        HBox buttonContainer = new HBox(15);
        buttonContainer.setAlignment(Pos.CENTER);

        buttonContainer.getChildren().addAll(
                approveButton,
                rejectButton
        );

        loadPendingRequests(true);

        refreshTimeline = new Timeline(
                new KeyFrame(Duration.seconds(3), event -> loadPendingRequests(true))
        );
        refreshTimeline.setCycleCount(Timeline.INDEFINITE);
        refreshTimeline.play();

        container.getChildren().addAll(
                title,
                statusLabel,
                requestTable,
                commentArea,
                buttonContainer
        );

        setCenter(container);
    }

        private void loadPendingRequests(boolean preserveSelection) {

                Integer selectedApprovalId = null;

                if (preserveSelection) {
                        OfficerRequestRecord selectedRequest =
                                        requestTable.getSelectionModel().getSelectedItem();

                        if (selectedRequest != null) {
                                selectedApprovalId = selectedRequest.getApprovalId();
                        }
                }

        List<OfficerRequestRecord> requests =
                approvalDAO.getPendingRequestsForDepartment(
                        officer.getDepartment()
                );

        requestTable.getItems().setAll(requests);

                if (preserveSelection && selectedApprovalId != null) {
                        for (int index = 0; index < requests.size(); index++) {
                                OfficerRequestRecord request = requests.get(index);

                                if (request.getApprovalId() == selectedApprovalId) {
                                        requestTable.getSelectionModel().select(index);
                                        requestTable.scrollTo(index);
                                        return;
                                }
                        }
                }

                requestTable.getSelectionModel().clearSelection();
    }

    private void handleDecision(ApprovalStatus status) {

        OfficerRequestRecord selectedRequest =
                requestTable.getSelectionModel().getSelectedItem();

        if (selectedRequest == null) {
            statusLabel.setText("Please select a request first.");
            return;
        }

        approvalDAO.updateApprovalStatus(
                selectedRequest.getApprovalId(),
                status,
                commentArea.getText()
        );

        approvalDAO.processFinalRequestStatus(selectedRequest.getRequestId());

        String actionWord = status == ApprovalStatus.APPROVED
                ? "approved"
                : "rejected";

        NotificationSender.send(
                officer.getDepartment().name()
                + " officer "
                + officer.getFullName()
                + " "
                + actionWord
                + " request "
                + selectedRequest.getRequestId()
        );

        statusLabel.setText(
                "Request "
                + selectedRequest.getRequestId()
                + " "
                + actionWord
                + " successfully."
        );

        commentArea.clear();
        loadPendingRequests(true);
    }

    public Scene createScene() {
        return new Scene(this, 1000, 700);
    }
}