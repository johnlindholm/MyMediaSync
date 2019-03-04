package com.homestorage.mymediasync.syncserver;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.homestorage.mymediasync.entity.MediaMetadata;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UploadTask extends AsyncTask<Void, Integer, Boolean> {

    private FileSyncResources fileSyncResources;
    private OkHttpClient httpClient;
    private String filesEndpoint;
    private FileSyncDatabase fileSyncDatabase;
    private Gson gson;
    private InetAddress serverAddress;

    public UploadTask(FileSyncResources fileSyncResources) {
        super();
        this.fileSyncResources = fileSyncResources;
        fileSyncDatabase = fileSyncResources.getFileSyncDatabase();
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .setDateFormat("yyyy-MM-dd hh:mm:ss")
                .create();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        serverAddress = fileSyncResources.getServerAddress().orElse(null);
        if (serverAddress == null) {
            Log.e("UploadTask", "Unable to get server address");
            return false;
        }
        List<MediaMetadata> mediaMetadataList = fileSyncResources.getMediaMetaData();
        if (mediaMetadataList == null) {
            Log.e("UploadTask", "Unable to get media metadata");
            return false;
        }
        filesEndpoint = "http://" + serverAddress.getHostAddress() + ":8080/api/v1/files";
        Log.d("UploadTask", "HomeStorage filesEndpoint: \"" + filesEndpoint + "\"");
        httpClient = new OkHttpClient.Builder().build();
        for (MediaMetadata mediaMetadata : mediaMetadataList) {
            if (isCancelled()) {
                Log.w("UploadTask", "Task is cancelled");
                return false;
            }
            uploadMedia(mediaMetadata);
        }
        return true;
    }

    private void uploadMedia(MediaMetadata mediaMetadata) {
        if (fileSyncDatabase.shouldSync(mediaMetadata)) {
            Log.d("UploadTask", "Trying to upload mediaMetadata " + mediaMetadata.getDataUri() +
                    ", Id: " + mediaMetadata.getId() + ", URI:" + mediaMetadata.getDataUri());
            MultipartBody body = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", FileSyncDatabase.getUniqueId(mediaMetadata),
                            RequestBody.create(
                                    MediaType.parse(mediaMetadata.getMediaType()),
                                    new File(mediaMetadata.getDataUri())))
                    .build();
            Request request = new Request.Builder()
                    .header("ClientID", fileSyncResources.getClientId())
                    .url(filesEndpoint)
                    .post(body)
                    .build();
            try {
                Log.d("UploadTask", "Sending file...");
                Response response = httpClient.newCall(request).execute();
                String patchEndpoint = response.header("location").replaceAll("localhost", serverAddress.getHostAddress());
                Log.d("UploadTask", "Response code: " + response.code());
                if (response.code() != 201) {
                    Log.d("UploadTask", "Response message: " + response.message());
                    Log.d("UploadTask", "Response body: " + response.body().string());
                    return;
                }
                Log.d("UploadTask", "Sending metadata to \"" + response.header("location") + "\"");
                request = new Request.Builder()
                        .header("ClientID", fileSyncResources.getClientId())
                        .url(patchEndpoint)
                        .patch(RequestBody.create(MediaType.parse("application/json"), gson.toJson(mediaMetadata)))
                        .build();
                response = httpClient.newCall(request).execute();
                Log.d("UploadTask", "Response code: " + response.code());
                Log.d("UploadTask", "Response body: " + response.body().string());
                if (response.code() != 200) {
                    Log.d("UploadTask", "Response message: " + response.message());
                    return;
                }
                fileSyncDatabase.setSynced(mediaMetadata);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected void onProgressUpdate(Integer... progress) {
//        setProgressPercent(progress[0]);
    }

    protected void onPostExecute(Long result) {
//        showDialog("Downloaded " + result + " bytes");
    }
}
