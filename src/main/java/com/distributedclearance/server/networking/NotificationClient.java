package com.distributedclearance.server.networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class NotificationClient implements Runnable {
    private final NotificationListener listener;

    public NotificationClient(NotificationListener listener) {
        this.listener = listener;
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket("localhost", 7000);

            PrintWriter writer =
                new PrintWriter(
                        socket.getOutputStream(),
                        true
                );

            /*
             * REGISTER AS LISTENER
             */
            writer.println("LISTENER");

            BufferedReader reader =
                new BufferedReader(
                    new InputStreamReader(
                            socket.getInputStream()
                    )
                );
            String message;

            while ((message = reader.readLine()) != null) {
                System.out.println(
                        "CLIENT RECEIVED: "
                        + message
                );

                listener.onNotificationReceived(
                        message
                );
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}