package com.homestorage.mymediasync.discovery;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Optional;

public class SyncServerConnectionTask extends AsyncTask<Void, Long, Optional<InetAddress>> {

    @Override
    protected Optional<InetAddress> doInBackground(Void... voids) {
        InetAddress broadcastAddress = getBroadcastAddress();
        DatagramSocket datagramSocket;
        try {
            datagramSocket = new DatagramSocket(45450, broadcastAddress);
        } catch (SocketException e) {
            e.printStackTrace();
            return Optional.empty();
        }
        DatagramPacket datagramPacket = new DatagramPacket(new byte[1024], 1024);
        Log.d("HomeStorageBroadcastListener", "Waiting for message");
        try {
            datagramSocket.receive(datagramPacket);
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
        String message = new String(datagramPacket.getData(), 0, datagramPacket.getLength());
        Log.d("SyncServerConnectionTask", "Server address: " + datagramPacket.getAddress().toString() + ", message: " + message);
        return Optional.of(datagramPacket.getAddress());
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
