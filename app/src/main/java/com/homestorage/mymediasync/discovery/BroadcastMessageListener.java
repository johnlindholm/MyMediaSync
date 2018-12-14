package com.homestorage.mymediasync.discovery;

import java.net.InetAddress;

public interface BroadcastMessageListener {

    void broadcastMessageReceived(InetAddress serverAddress, String message);
}
