package com.example.warehub;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Report extends Fragment {

    private EditText etFileName;
    private Button btnGenerate;
    private ImageButton btnShare;
    private DatabaseHelper databaseHelper;

    private static final int PERMISSION_REQUEST_CODE = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_report, container, false);

        etFileName = view.findViewById(R.id.et_file_name);
        btnGenerate = view.findViewById(R.id.btn_generate);
        btnShare = view.findViewById(R.id.btn_share);
        databaseHelper = new DatabaseHelper(getContext());

        // Check for WRITE_EXTERNAL_STORAGE permission
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Request permission if not already granted
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }

        btnGenerate.setOnClickListener(v -> {
            String fileName = etFileName.getText().toString();
            if (fileName.isEmpty()) {
                Toast.makeText(getContext(), "Please enter a file name", Toast.LENGTH_SHORT).show();
            } else {
                // Ensure the file name has a .csv extension
                if (!fileName.endsWith(".csv")) {
                    fileName += ".csv";
                }
                // Check permission before generating the file
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    exportDatabaseToCSV(fileName);
                } else {
                    // Request permission if not granted yet
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                }
            }
        });

        btnShare.setOnClickListener(v -> {
            String fileName = etFileName.getText().toString();
            if (fileName.isEmpty()) {
                Toast.makeText(getContext(), "Please enter a file name to share", Toast.LENGTH_SHORT).show();
            } else {
                // Ensure the file name has a .csv extension
                if (!fileName.endsWith(".csv")) {
                    fileName += ".csv";
                }
                shareFile(fileName);
            }
        });

        return view;
    }

    private void exportDatabaseToCSV(String fileName) {
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (!downloadsDir.exists()) {
            downloadsDir.mkdirs();
        }

        File file = new File(downloadsDir, fileName);
        try {
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            SQLiteDatabase db = databaseHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.getTableName(), null);
            csvWrite.writeNext(cursor.getColumnNames());

            while (cursor.moveToNext()) {
                String[] data = {
                        cursor.getString(0), // ID
                        cursor.getString(1), // PRODUCT_NAME
                        cursor.getString(2), // PRODUCT_CODE
                        cursor.getString(3), // QUANTITY
                        cursor.getString(4)  // PRICE
                };
                csvWrite.writeNext(data);
            }
            csvWrite.close();
            cursor.close();

            // Show download notification after the report is generated
            showDownloadNotification(file);

            Toast.makeText(getContext(), "Report generated successfully!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error occurred while generating the report", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDownloadNotification(File file) {
        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);

        // Create an Intent to open the downloaded file
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri fileUri = FileProvider.getUriForFile(getContext(), "com.example.warehub.fileprovider", file);
        intent.setDataAndType(fileUri, "text/csv"); // Change the MIME type if necessary
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // Grant permission to read the URI

        // Create a PendingIntent for the notification
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("download_channel", "File Downloads", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), "download_channel")
                .setSmallIcon(R.drawable.ic_file_download)
                .setContentTitle("Download complete")
                .setContentText("File saved: " + file.getName())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent); // Set the PendingIntent here

        notificationManager.notify(1, builder.build());
    }

    private void shareFile(String fileName) {
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(downloadsDir, fileName);

        if (!file.exists()) {
            Toast.makeText(getContext(), "File not found", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri fileUri = FileProvider.getUriForFile(getContext(), "com.example.warehub.fileprovider", file);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/csv");
        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "Share CSV file"));
    }

    // Handle the result of permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "Permission granted. You can now generate the report.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Permission denied. Cannot generate report without storage permission.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
