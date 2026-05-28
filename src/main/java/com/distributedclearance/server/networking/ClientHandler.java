package com.distributedclearance.server.networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        try {

            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    socket.getInputStream()
                            )
                    );

            String message =
                    reader.readLine();

            System.out.println(
                    "Main Server Received: "
                    + message
            );

            forwardToDepartment(
                    "FINANCE",
                    6001,
                    message
            );

            forwardToDepartment(
                    "LIBRARY",
                    6002,
                    message
            );

            forwardToDepartment(
                    "REGISTRAR",
                    6003,
                    message
            );

            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void forwardToDepartment(
            String department,
            int port,
            String message
    ) {

        try {

            Socket deptSocket = new Socket("localhost", port);

            PrintWriter writer =
                    new PrintWriter(
                            deptSocket.getOutputStream(),
                            true
                    );

            writer.println(message);
            deptSocket.close();

            System.out.println("Forwarded to " + department);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}