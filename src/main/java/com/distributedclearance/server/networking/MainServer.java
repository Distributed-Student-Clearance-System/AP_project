package com.distributedclearance.server.networking;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MainServer {

    private static final int PORT = 5000;

    public static void main(String[] args) {

        try {

            ServerSocket serverSocket =
                    new ServerSocket(PORT);

            System.out.println(
                    "Main Server Started..."
            );

            while (true) {

                Socket clientSocket =
                        serverSocket.accept();

                System.out.println(
                        "Client connected."
                );

                ClientHandler handler =
                        new ClientHandler(clientSocket);

                Thread thread =
                        new Thread(handler);

                thread.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}