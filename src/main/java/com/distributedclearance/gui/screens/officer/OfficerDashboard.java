package com.distributedclearance.gui.screens.officer;

import java.util.List;

import com.distributedclearance.database.dao.ApprovalDAO;
import com.distributedclearance.gui.screens.BaseScreen;
import com.distributedclearance.models.Approval;
import com.distributedclearance.models.DeptOfficer;
import com.distributedclearance.models.enums.ApprovalStatus;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class OfficerDashboard extends BaseScreen {

    private final DeptOfficer officer;

    private final ApprovalDAO approvalDAO = new ApprovalDAO();

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

        ListView<String> approvalList = new ListView<>();
        approvalList.setPrefHeight(300);

        TextArea commentArea = new TextArea();
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

        List<Approval> approvals = approvalDAO.getPendingApprovals(officer.getDepartment());

        for (Approval approval : approvals) {

            approvalList.getItems().add(
                    "Approval ID: " +
                    approval.getId() +
                    " | Request ID: " +
                    approval.getRequestId()
            );
        }

        approveButton.setOnAction(event -> {

            int selectedIndex =
                    approvalList.getSelectionModel()
                            .getSelectedIndex();

            if (selectedIndex >= 0) {

                Approval selectedApproval =
                        approvals.get(selectedIndex);

                approvalDAO.updateApprovalStatus(
                        selectedApproval.getId(),
                        ApprovalStatus.APPROVED,
                        commentArea.getText()
                );

                approvalDAO.processFinalRequestStatus(selectedApproval.getRequestId());

                approvalList.getItems().remove(selectedIndex);
                approvals.remove(selectedIndex);

                commentArea.clear();
            }
        });

        rejectButton.setOnAction(event -> {

            int selectedIndex =
                    approvalList.getSelectionModel()
                            .getSelectedIndex();

            if (selectedIndex >= 0) {

                Approval selectedApproval =
                        approvals.get(selectedIndex);

                approvalDAO.updateApprovalStatus(
                        selectedApproval.getId(),
                        ApprovalStatus.REJECTED,
                        commentArea.getText()
                );

                approvalDAO.processFinalRequestStatus(selectedApproval.getRequestId());

                approvalList.getItems().remove(selectedIndex);
                approvals.remove(selectedIndex);

                commentArea.clear();
            }
        });

        HBox buttonContainer = new HBox(15);
        buttonContainer.setAlignment(Pos.CENTER);

        buttonContainer.getChildren().addAll(
                approveButton,
                rejectButton
        );

        container.getChildren().addAll(
                title,
                approvalList,
                commentArea,
                buttonContainer
        );

        setCenter(container);
    }

    public Scene createScene() {
        return new Scene(this, 1000, 700);
    }
}