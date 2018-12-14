package com.homestorage.mymediasync.media;

import com.homestorage.mymediasync.entity.MediaMetadata;

import java.util.List;

public interface MediaMetadataListener {

    void onAllMediaMetadata(List<MediaMetadata> allMediaMetadata);

}