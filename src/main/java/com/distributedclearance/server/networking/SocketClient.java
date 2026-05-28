package com.distributedclearance.server.networking;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketClient {
    public static void sendMessage(String message) {
        try {
            Socket socket = new Socket("localhost", 5000);
            PrintWriter writer = new PrintWriter(socket.getOutputStream(),true);

            writer.println(message);
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}