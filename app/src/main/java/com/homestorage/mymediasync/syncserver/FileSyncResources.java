package com.homestorage.mymediasync.syncserver;

import android.content.Context;
import android.os.AsyncTask;

import com.homestorage.mymediasync.discovery.SyncServerConnectionTask;
import com.homestorage.mymediasync.entity.MediaMetadata;
import com.homestorage.mymediasync.media.MediaMetadataCollectorTask;

import java.net.InetAddress;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class FileSyncResources {

    private final static String CLIENT_ID = "d9d7d5a1-f396-4ea2-8638-8ccc335e8c73";

    private final Context applicationContext;
    private SyncServerConnectionTask syncServerConnectionTask;
    private MediaMetadataCollectorTask mediaMetadataCollectorTask;
    private Optional<InetAddress> inetAddress;
    private List<MediaMetadata> mediaMetadataList;
    private final FileSyncDatabase fileSyncDatabase;

    public FileSyncResources(Context applicationContext) {
        this.applicationContext = applicationContext;
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
                    //Ongoing attempt?
                    if (syncServerConnectionTask.getStatus() != AsyncTask.Status.FINISHED) {
                        inetAddress = syncServerConnectionTask.get();
                        return inetAddress;
                    }
                    //No ongoing attempt, try again
                    syncServerConnectionTask = new SyncServerConnectionTask();
                    syncServerConnectionTask.execute();
                    inetAddress = syncServerConnectionTask.get();
                    return inetAddress;
                }
            }
            inetAddress = syncServerConnectionTask.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            inetAddress = Optional.empty();
        } catch (ExecutionException e) {
            e.printStackTrace();
            inetAddress = Optional.empty();
        }
        return inetAddress;
    }

    public List<MediaMetadata> getMediaMetaData() {
        try {
            if (mediaMetadataList != null) {
                //Task has finished successfully
                return mediaMetadataList;
            } else {
                mediaMetadataList = mediaMetadataCollectorTask.get();
            }
//            if (mediaMetadataCollectorTask.getStatus() == AsyncTask.Status.FINISHED) {
//                //Task has finished, but not successful. Try again
//                mediaMetadataCollectorTask = new MediaMetadataCollectorTask();
//                mediaMetadataCollectorTask.execute(applicationContext);
//                mediaMetadataList = mediaMetadataCollectorTask.get();
//                return mediaMetadataList;
//            }
//            //Ongoing task, calling blocking get
//            mediaMetadataList = mediaMetadataCollectorTask.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return mediaMetadataList;
    }

    public FileSyncDatabase getFileSyncDatabase() {
        return fileSyncDatabase;
    }

    public String getClientId() {
        return CLIENT_ID;
    }
}
