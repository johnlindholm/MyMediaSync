package com.homestorage.mymediasync.discovery;

import android.util.Log;

import com.homestorage.mymediasync.discovery.BroadcastMessageListener;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class HomeStorageBroadcastListener extends Thread {

    private BroadcastMessageListener broadcastMessageListener;
    private DatagramSocket datagramSocket;

    public HomeStorageBroadcastListener(BroadcastMessageListener broadcastMessageListener) throws SocketException {
        this.broadcastMessageListener = broadcastMessageListener;
        InetAddress broadcastAddress = getBroadcastAddress();
        datagramSocket = new DatagramSocket(45450, broadcastAddress);
    }

    @Override
    public void run() {
        while (true) {
            try {
                DatagramPacket datagramPacket = new DatagramPacket(new byte[1024], 1024);
                Log.d("HomeStorageBroadcastListener", "Waiting for message");
                datagramSocket.receive(datagramPacket);
                String message = new String(datagramPacket.getData(), 0, datagramPacket.getLength());
                Log.d("HomeStorageBroadcastListener", "Message received: " + message);
                Log.d("HomeStorageBroadcastListener", "HomeStorage server: " + datagramPacket.getAddress());
                broadcastMessageListener.broadcastMessageReceived(datagramPacket.getAddress(), message);
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private InetAddress getBroadcastAddress() {
        try {
            Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
            while (enumeration.hasMoreElements()) {
                NetworkInterface networkInterface = enumeration.nextElement();
                if (networkInterface.isLoopback()) {
                    continue;
                }
                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                    if (interfaceAddress.getBroadcast() != null) {
                        return interfaceAddress.getBroadcast();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }
}
