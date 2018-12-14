package com.homestorage.mymediasync.syncserver;

import android.content.Context;
import android.util.Log;

import com.homestorage.mymediasync.discovery.BroadcastMessageListener;
import com.homestorage.mymediasync.discovery.HomeStorageBroadcastListener;
import com.homestorage.mymediasync.entity.MediaMetadata;
import com.homestorage.mymediasync.media.MediaMetadataCollector;
import com.homestorage.mymediasync.media.MediaMetadataListener;

import java.net.InetAddress;
import java.net.SocketException;
import java.util.List;

public class FileSyncHandlerThread extends Thread implements BroadcastMessageListener, MediaMetadataListener {

    private Context applicationContext;
    private InetAddress serverAddress;
    private HomeStorageBroadcastListener homeStorageBroadcastListener;
    private FileSyncDatabase fileSyncDatabase;

    public FileSyncHandlerThread(Context applicationContext) {
        this.applicationContext = applicationContext;
        fileSyncDatabase = new FileSyncDatabase(applicationContext);
    }

    @Override
    public void run() {
        try {
            homeStorageBroadcastListener = new HomeStorageBroadcastListener(this);
            homeStorageBroadcastListener.start();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        while (true) {
            try {
                waitForServerAddress();
                MediaMetadataCollector.collectAllMediaMetadata(applicationContext, this);
                //Sleep 10 seconds
                sleep(10 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void waitForServerAddress() throws InterruptedException {
        while (serverAddress == null) {
            Log.d("FileSyncHandlerThread", "Waiting for server address");
            wait();
            Log.d("FileSyncHandlerThread", "Woke up, serverAddress: " + serverAddress);
        }
    }

    private synchronized void setServerAddress(InetAddress serverAddress) {
        this.serverAddress = serverAddress;
        notify();
    }

    @Override
    public void broadcastMessageReceived(InetAddress serverAddress, String message) {
        setServerAddress(serverAddress);
    }

    @Override
    public void onAllMediaMetadata(List<MediaMetadata> allMediaMetadata) {
        new UploadTask(fileSyncDatabase, serverAddress).execute(allMediaMetadata.stream().toArray(MediaMetadata[]::new));
    }
}
