package com.distributedclearance.gui.screens.student;

import java.util.List;

import com.distributedclearance.database.dao.ApprovalDAO;
import com.distributedclearance.database.dao.RequestDAO;
import com.distributedclearance.gui.screens.BaseScreen;
import com.distributedclearance.models.Approval;
import com.distributedclearance.models.Student;
import com.distributedclearance.server.networking.NotificationClient;
import com.distributedclearance.server.networking.NotificationListener;
import com.distributedclearance.server.networking.SocketClient;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

public class StudentDashboard extends BaseScreen implements NotificationListener {

    private final Student student;

    private final RequestDAO requestDAO = new RequestDAO();
    private final ApprovalDAO approvalDAO = new ApprovalDAO();

    private Label notificationLabel;
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

        notificationLabel =
                new Label("No notifications yet.");

        Button submitButton =
                new Button("Submit Clearance Request");

        submitButton.setPrefWidth(250);

        Label statusLabel = new Label();

        approvalList = new ListView<>();
        approvalList.setPrefHeight(300);

        submitButton.setOnAction(event -> {

            boolean success =
                    requestDAO.submitRequest(student);

            if (success) {

                currentRequestId =
                        requestDAO.getLatestRequestId(
                                student.getId()
                        );

                SocketClient.sendMessage(
                        currentRequestId + ":"
                        + student.getFullName()
                );

                statusLabel.setText(
                        "Clearance request submitted successfully."
                );

                loadApprovals();

            } else {

                statusLabel.setText(
                        "Failed to submit request."
                );
            }
        });

        container.getChildren().addAll(
                title,
                welcomeLabel,
                notificationLabel,
                submitButton,
                statusLabel,
                approvalList
        );

        setCenter(container);

        NotificationClient client =
                new NotificationClient(this);

        Thread notificationThread =
                new Thread(client);

        notificationThread.setDaemon(true);
        notificationThread.start();
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
    }

    public Scene createScene() {
        return new Scene(this, 1000, 700);
    }

    @Override
    public void onNotificationReceived(String message) {

        Platform.runLater(() -> {

            System.out.println(
                    "UI RECEIVED NOTIFICATION: "
                    + message
            );

            notificationLabel.setText(
                    "NOTIFICATION: " + message
            );
        });

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Platform.runLater(() -> {

            if (currentRequestId != -1) {
                loadApprovals();
            }
        });
    }
}