package com.homestorage.mymediasync.media;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import com.homestorage.mymediasync.entity.Location;
import com.homestorage.mymediasync.entity.MediaMetadata;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MediaMetadataCollector {

    private final static String[] imageProjection = new String[]{
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATE_TAKEN,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.DATE_MODIFIED,
            MediaStore.Images.Media.LATITUDE,
            MediaStore.Images.Media.LONGITUDE,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.TITLE,
            MediaStore.Images.Media.DESCRIPTION
    };

    private final static String[] videoProjection = new String[]{
            MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DATE_TAKEN,
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Media.DATE_MODIFIED,
            MediaStore.Video.Media.TAGS,
            MediaStore.Video.Media.LATITUDE,
            MediaStore.Video.Media.LONGITUDE,
            MediaStore.Video.Media.MIME_TYPE,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.TITLE,
            MediaStore.Video.Media.DESCRIPTION,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.RESOLUTION
    };

    /**
     * TODO
     *
     * @param context
     * @param listener
     */
    public static void collectAllMediaMetadata(Context context, MediaMetadataListener listener) {
        Log.d("MediaMetadataCollector", "Collecting media...");
        List<MediaMetadata> mediaMetadataList = new ArrayList<>();
        Cursor cur = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageProjection,
                null, null, null);
        if (cur != null && cur.getCount() > 0) {
            Log.i("MediaMetadataCollector", " query count=" + cur.getCount());
            if (cur.moveToFirst()) {
                do {
                    MediaMetadata mediaMetadata = createPhotoMetadata(cur);
                    mediaMetadataList.add(mediaMetadata);
                } while (cur.moveToNext());
            }
            cur.close();
        }
        cur = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, videoProjection,
                null, null, null);
        if (cur != null && cur.getCount() > 0) {
            Log.i("MediaMetadataCollector", " query count=" + cur.getCount());
            if (cur.moveToFirst()) {
                do {
                    MediaMetadata mediaMetadata = createVideoMetadata(cur);
                    mediaMetadataList.add(mediaMetadata);
                } while (cur.moveToNext());
            }
            cur.close();
        }
        listener.onAllMediaMetadata(mediaMetadataList);
    }

    private static MediaMetadata createPhotoMetadata(Cursor cur) {
        MediaMetadata mediaMetadata = new MediaMetadata();
        mediaMetadata.setDataUri(cur.getString(cur.getColumnIndex(MediaStore.Images.Media.DATA)));
        mediaMetadata.setId(cur.getLong(cur.getColumnIndex(MediaStore.Images.Media._ID)));
        mediaMetadata.setDateTaken(new Date(cur.getLong(cur.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN))));
        mediaMetadata.setDateAdded(new Date(cur.getLong(cur.getColumnIndex(MediaStore.Images.Media.DATE_ADDED))));
        mediaMetadata.setDateModified(new Date(cur.getLong(cur.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED))));
        Location location = new Location();
        location.setLatitude(cur.getDouble(cur.getColumnIndex(MediaStore.Images.Media.LATITUDE)));
        location.setLongitude(cur.getDouble(cur.getColumnIndex(MediaStore.Images.Media.LONGITUDE)));
        mediaMetadata.setLocation(location);
        mediaMetadata.setMediaType(cur.getString(cur.getColumnIndex(MediaStore.Images.Media.MIME_TYPE)));
        mediaMetadata.setSize(cur.getLong(cur.getColumnIndex(MediaStore.Images.Media.SIZE)));
        mediaMetadata.setTitle(cur.getString(cur.getColumnIndex(MediaStore.Images.Media.TITLE)));
        mediaMetadata.setDescription(
                "[" +
                cur.getString(cur.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)) +
                "] " +
                cur.getString(cur.getColumnIndex(MediaStore.Images.Media.DESCRIPTION))
        );

        return mediaMetadata;
    }

    private static MediaMetadata createVideoMetadata(Cursor cur) {
        MediaMetadata mediaMetadata = new MediaMetadata();
        mediaMetadata.setDataUri(cur.getString(cur.getColumnIndex(MediaStore.Video.Media.DATA)));
        mediaMetadata.setId(cur.getLong(cur.getColumnIndex(MediaStore.Video.Media._ID)));
        mediaMetadata.setDateTaken(new Date(cur.getLong(cur.getColumnIndex(MediaStore.Video.Media.DATE_TAKEN))));
        mediaMetadata.setDateAdded(new Date(cur.getLong(cur.getColumnIndex(MediaStore.Video.Media.DATE_ADDED))));
        mediaMetadata.setDateModified(new Date(cur.getLong(cur.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED))));
        Location location = new Location();
        location.setLatitude(cur.getDouble(cur.getColumnIndex(MediaStore.Video.Media.LATITUDE)));
        location.setLongitude(cur.getDouble(cur.getColumnIndex(MediaStore.Video.Media.LONGITUDE)));
        mediaMetadata.setLocation(location);
        mediaMetadata.setMediaType(cur.getString(cur.getColumnIndex(MediaStore.Video.Media.MIME_TYPE)));
        mediaMetadata.setSize(cur.getLong(cur.getColumnIndex(MediaStore.Video.Media.SIZE)));
        mediaMetadata.setTitle(cur.getString(cur.getColumnIndex(MediaStore.Video.Media.TITLE)));
        mediaMetadata.setDescription(
                "[" +
                        cur.getString(cur.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)) +
                        "] " +
                        cur.getString(cur.getColumnIndex(MediaStore.Video.Media.DESCRIPTION))
        );
        mediaMetadata.setDuration(cur.getLong(cur.getColumnIndex(MediaStore.Video.Media.DURATION)));
        mediaMetadata.setResolution(cur.getString(cur.getColumnIndex(MediaStore.Video.Media.RESOLUTION)));
        mediaMetadata.setTags(cur.getString(cur.getColumnIndex(MediaStore.Video.Media.TAGS)));

        return mediaMetadata;
    }
}