package com.homestorage.mymediasync;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;

import com.homestorage.mymediasync.syncserver.FileSyncResources;
import com.homestorage.mymediasync.syncserver.UploadTask;

public class MainActivity extends AppCompatActivity {

    private final static int PERMISSION_READ_EXTERNAL_STORAGE = 123;
    private final static int PERMISSION_INTERNET = 124;

    private FileSyncResources fileSyncResources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermissions();

        fileSyncResources = new FileSyncResources(getApplicationContext());
        final Button syncButton = findViewById(R.id.syncButton);
        syncButton.setOnClickListener(v -> startFileSync());
    }

    private void startFileSync() {
        new UploadTask(fileSyncResources).execute();
    }

    private void checkPermissions() {
        checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, PERMISSION_READ_EXTERNAL_STORAGE);
        checkPermission(Manifest.permission.INTERNET, PERMISSION_INTERNET);
    }

    private void checkPermission(String permission, int constant) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(permission)) {
                // Explain to the user why we need to read the contacts
            } else {
                requestPermissions(new String[]{permission}, constant);
            }
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        Log.d("MyMediaSync", "requestCode: " + requestCode);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted.
        } else {
            // User refused to grant permission.
        }
    }

}
