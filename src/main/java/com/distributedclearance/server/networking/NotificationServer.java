package com.distributedclearance.server.networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NotificationServer {
    private static final int PORT = 7000;
    private static final List<PrintWriter> clients = new ArrayList<>();

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);

            System.out.println("Notification Server Started...");

            while (true) {
                Socket socket = serverSocket.accept();

                new Thread(() -> {
                    try {
                        BufferedReader reader =
                            new BufferedReader(
                                new InputStreamReader(
                                    socket.getInputStream()
                                )
                                );

                        PrintWriter writer =
                            new PrintWriter(
                                socket.getOutputStream(),
                                true
                            );

                        String firstMessage = reader.readLine();

                        if (firstMessage == null) {
                            socket.close();
                            return;
                        }

                        /*
                         * LISTENER CLIENT
                         */
                        if (firstMessage.equals("LISTENER")) {

                            System.out.println(
                                    "Persistent listener connected."
                            );

                            synchronized (clients) {
                                clients.add(writer);
                            }

                            return;
                        }

                        /*
                         * NORMAL NOTIFICATION MESSAGE
                         */
                        System.out.println(
                            "SERVER RECEIVED: "
                            + firstMessage
                        );

                        System.out.println("BROADCASTING...");

                        synchronized (clients) {

                            Iterator<PrintWriter> iterator =
                                clients.iterator();

                            while (iterator.hasNext()) {

                                PrintWriter client =
                                    iterator.next();

                                try {

                                    client.println(firstMessage);

                                    if (client.checkError()) {
                                        iterator.remove();
                                    }

                                } catch (Exception e) {
                                    iterator.remove();
                                }
                            }
                        }

                        socket.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}