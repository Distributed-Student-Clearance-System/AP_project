package com.distributedclearance.server.networking;

import java.io.PrintWriter;
import java.net.Socket;

public class NotificationSender {

    public static void send(String message) {

        try {
            System.out.println("SENDING NOTIFICATION: " + message);

            Socket socket = new Socket("localhost", 7000);

            PrintWriter writer =
                    new PrintWriter(socket.getOutputStream(), true);

            writer.println(message);

            writer.close();
            socket.close();

            System.out.println("NOTIFICATION SENT");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}