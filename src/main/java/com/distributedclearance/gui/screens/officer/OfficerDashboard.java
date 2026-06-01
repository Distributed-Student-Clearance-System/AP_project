package com.distributedclearance.gui.screens.officer;

import java.util.List;

import com.distributedclearance.database.dao.ApprovalDAO;
import com.distributedclearance.gui.navigation.SceneManager;
import com.distributedclearance.gui.screens.BaseScreen;
import com.distributedclearance.models.DeptOfficer;
import com.distributedclearance.models.OfficerRequestRecord;
import com.distributedclearance.models.enums.ApprovalStatus;
import com.distributedclearance.server.networking.NotificationSender;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Priority;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

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

        VBox container = new VBox(16);
        container.setPadding(new Insets(20));
        container.setFillWidth(true);
        container.getStyleClass().add("dashboard-card");

        Label title = new Label(
            officer.getDepartment() + " Officer Dashboard"
        );

        title.getStyleClass().add("app-title");

        statusLabel = new Label("Select a pending request and choose a decision.");
        statusLabel.getStyleClass().add("status-label");

        VBox header = new VBox(6, title, statusLabel);
        header.setFillWidth(true);

        requestTable = new TableView<>();
        requestTable.setPlaceholder(
            new Label("No pending requests for your department.")
        );
        requestTable.getStyleClass().add("dark-table");
        requestTable.setMaxWidth(Double.MAX_VALUE);

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

        requestTable.getColumns().add(studentNameColumn);
        requestTable.getColumns().add(requestIdColumn);
        requestTable.getColumns().add(currentStatusColumn);

        commentArea = new TextArea();
        commentArea.setPromptText("Enter comment...");
        commentArea.getStyleClass().add("dark-text-area");

        Button approveButton = new Button("Approve");
        Button rejectButton = new Button("Reject");

        approveButton.getStyleClass().add("primary-button");
        rejectButton.getStyleClass().add("danger-button");

        HBox actionButtons = new HBox(12, approveButton, rejectButton);
        actionButtons.setAlignment(Pos.CENTER_LEFT);

        VBox commentSection = new VBox(8, new Label("Officer Comment / Reason"), commentArea, actionButtons);
        commentSection.setFillWidth(true);

        VBox.setVgrow(requestTable, Priority.ALWAYS);
        VBox.setVgrow(commentArea, Priority.SOMETIMES);
        commentArea.setMaxWidth(Double.MAX_VALUE);

        approveButton.setOnAction(event -> handleDecision(ApprovalStatus.APPROVED));

        rejectButton.setOnAction(event -> handleDecision(ApprovalStatus.REJECTED));

        loadPendingRequests(true);

        refreshTimeline = new Timeline(
                new KeyFrame(Duration.seconds(3), event -> loadPendingRequests(true))
        );
        refreshTimeline.setCycleCount(Timeline.INDEFINITE);
        refreshTimeline.play();

        container.getChildren().addAll(
            header,
            requestTable,
            commentSection
        );

        setCenter(container);
    }

        private void loadPendingRequests(boolean preserveSelection) {
            Integer selectedApprovalId = null;

            if (preserveSelection) {
                    OfficerRequestRecord selectedRequest = requestTable.getSelectionModel().getSelectedItem(); 

                    if (selectedRequest != null) {
                        selectedApprovalId = selectedRequest.getApprovalId();
                    }
            }

            List<OfficerRequestRecord> requests =
                    approvalDAO.getPendingRequestsForDepartment(officer.getDepartment());

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

        String comment = commentArea.getText();
        if (comment == null || comment.trim().isEmpty()) {
            comment = "No comment provided.";
        } else {
            comment = comment.trim();
        }

        approvalDAO.updateApprovalStatus(
            selectedRequest.getApprovalId(),
            status,
            comment
        );

        approvalDAO.processFinalRequestStatus(selectedRequest.getRequestId());

        String actionWord = status == ApprovalStatus.APPROVED
            ? "approved"
            : "rejected";

        NotificationSender.send(
            officer.getDepartment().name()
            + " -> "
            + status.name()
            + " | Reason: "
            + comment
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
        Scene scene = new Scene(this, 1000, 700);
        SceneManager.applyTheme(scene);
        return scene;
    }
}