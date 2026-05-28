package com.distributedclearance.server.networking;

import java.util.Random;

import com.distributedclearance.database.dao.ApprovalDAO;
import com.distributedclearance.models.Approval;
import com.distributedclearance.models.enums.ApprovalStatus;
import com.distributedclearance.models.enums.Department;

public class DistributedApprovalService {

    private final ApprovalDAO approvalDAO =
            new ApprovalDAO();

    public void processApproval(
            int requestId,
            Department department
    ) {

        try {

            System.out.println(
                    department
                    + " processing request "
                    + requestId
            );

            Thread.sleep(3000);

            Approval approval =
                    approvalDAO
                    .getApprovalByRequestAndDepartment(
                            requestId,
                            department
                    );

            if (approval == null) {
                return;
            }

            Random random = new Random();

            boolean approved =
                    random.nextBoolean();

            ApprovalStatus status =
                    approved
                    ? ApprovalStatus.APPROVED
                    : ApprovalStatus.REJECTED;

            approvalDAO.updateApprovalStatus(
                    approval.getId(),
                    status,
                    "Processed automatically"
            );

            String notification =
                    department.name()
                    + " -> "
                    + status.name();

            NotificationSender.send(notification);

            approvalDAO.processFinalRequestStatus(
                    requestId
            );

            NotificationSender.send(
                department
                + " processed request "
                + requestId
                + " -> "
                + status
            );

            System.out.println(
                    department
                    + " finished processing "
                    + requestId
                    + " -> "
                    + status
            );

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}