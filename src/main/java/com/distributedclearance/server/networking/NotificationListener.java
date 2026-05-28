package com.distributedclearance.server.networking;

public interface NotificationListener {

    void onNotificationReceived(
            String message
    );
}