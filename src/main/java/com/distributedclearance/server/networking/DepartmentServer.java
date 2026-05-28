package com.distributedclearance.server.networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import com.distributedclearance.models.enums.Department;

public class DepartmentServer {

    private final int port;
    private final String departmentName;

    public DepartmentServer(int port, String departmentName) {
        this.port = port;
        this.departmentName = departmentName;
    }

    public void startServer() {

        try {
            ServerSocket serverSocket = new ServerSocket(port);

            System.out.println(
                    departmentName
                    + " Server running on port "
                    + port
            );

            while (true) {

                Socket socket = serverSocket.accept();

                BufferedReader reader =
                        new BufferedReader(
                                new InputStreamReader(
                                        socket.getInputStream()
                                )
                        );

                String message = reader.readLine();

                System.out.println(
                        "[" + departmentName + "] "
                        + "Received: "
                        + message
                );

                String[] parts = message.split(":");

                int requestId = Integer.parseInt(parts[0]);

                DistributedApprovalService service =
                        new DistributedApprovalService();

                service.processApproval(
                        requestId,
                        Department.valueOf(departmentName)
                );

                System.out.println(
                        departmentName
                        + " finished processing "
                        + requestId
                );

                NotificationSender.send(
                        departmentName
                        + " processed request "
                        + requestId
                );

                socket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}