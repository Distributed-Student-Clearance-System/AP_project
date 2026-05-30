package com.distributedclearance.server.networking;

import com.distributedclearance.database.dao.ApprovalDAO;
import com.distributedclearance.models.Approval;
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

            System.out.println(
                    department
                    + " received request "
                    + requestId
                    + " and left it pending for officer review"
            );

        } catch (InterruptedException e) {
                        System.err.println(
                                        "[DistributedApprovalService] Processing interrupted for request "
                                        + requestId
                                        + " at "
                                        + department
                        );
                        Thread.currentThread().interrupt();
        }
    }
}