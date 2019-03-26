package com.homestorage.mymediasync.syncserver;

import android.content.Context;
import android.util.Log;

import com.homestorage.mymediasync.discovery.SyncServerConnectionTask;
import com.homestorage.mymediasync.entity.MediaMetadata;
import com.homestorage.mymediasync.media.MediaMetadataCollectorTask;

import java.net.InetAddress;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class FileSyncResources {

    private final static String CLIENT_ID = "d9d7d5a1-f396-4ea2-8638-8ccc335e8c73";
    private SyncServerConnectionTask syncServerConnectionTask;
    private MediaMetadataCollectorTask mediaMetadataCollectorTask;
    private Optional<InetAddress> inetAddress;
    private List<MediaMetadata> mediaMetadataList;
    private final FileSyncDatabase fileSyncDatabase;

    public FileSyncResources(Context applicationContext) {
        this.fileSyncDatabase = new FileSyncDatabase(applicationContext);
        syncServerConnectionTask = new SyncServerConnectionTask();
        syncServerConnectionTask.execute();
        mediaMetadataCollectorTask = new MediaMetadataCollectorTask();
        mediaMetadataCollectorTask.execute(applicationContext);
    }

    public Optional<InetAddress> getServerAddress() {
        try {
            if (inetAddress != null) {
                if (inetAddress.isPresent()) {
                    return inetAddress;
                } else {
                    //Try again
                    syncServerConnectionTask = new SyncServerConnectionTask();
                    syncServerConnectionTask.execute();
                    inetAddress = syncServerConnectionTask.get();
                    return inetAddress;
                }
            }
            inetAddress = syncServerConnectionTask.get();
            return inetAddress;
        } catch (InterruptedException | ExecutionException e) {
            Log.e("FileSyncResources", "getServerAddress()", e);
            return Optional.empty();
        }
    }

    public List<MediaMetadata> getMediaMetaData() {
        try {
            if (mediaMetadataList == null) {
                mediaMetadataList = mediaMetadataCollectorTask.get();
            }
            return mediaMetadataList;
        } catch (InterruptedException | ExecutionException e) {
            Log.e("FileSyncResources", "getMediaMetaData()", e);
            return null;
        }
    }

    public FileSyncDatabase getFileSyncDatabase() {
        return fileSyncDatabase;
    }

    public String getClientId() {
        return CLIENT_ID;
    }
}
