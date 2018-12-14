package com.homestorage.mymediasync.syncserver;

import android.content.Context;
import android.util.Log;

import com.homestorage.mymediasync.entity.MediaMetadata;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class FileSyncDatabase {

    private static final String FILE_DATA_SYNC_STATUS_DIR = "FileSyncStatus";
    private File fileSyncStatusDir;
    private boolean CLEAR_ALL = false;

    public FileSyncDatabase(Context applicationContext) {
        fileSyncStatusDir = applicationContext.getDir(FILE_DATA_SYNC_STATUS_DIR, Context.MODE_PRIVATE);
        //TODO write to database instead
        if (CLEAR_ALL) {
            Arrays.stream(fileSyncStatusDir.listFiles()).forEach(File::delete);
            Log.d("FileSyncDatabase", "fileSyncStatusDir.delete(): " + fileSyncStatusDir.delete());
        }
    }

    public boolean setSynced(MediaMetadata mediaMetadata) throws IOException {
        if (CLEAR_ALL) {
            return false;
        }
        File file = new File(fileSyncStatusDir, getUniqueId(mediaMetadata));
        Log.d("FileSyncDatabase", "Trying to create file: " + file.getAbsolutePath());
        return file.createNewFile();
    }

    public boolean shouldSync(MediaMetadata mediaMetadata) {
        if (CLEAR_ALL) {
            return false;
        }
        File file = new File(fileSyncStatusDir, getUniqueId(mediaMetadata));
        return file.exists() == false;
    }

    public static String getUniqueId(MediaMetadata photo) {
        return (photo.getDataUri()).replaceAll("/", "_");
    }

}
