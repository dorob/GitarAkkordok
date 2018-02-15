package hu.mobwebhf.gitarakkordok.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import hu.mobwebhf.gitarakkordok.Network.NetworkManager;
import hu.mobwebhf.gitarakkordok.R;

public class SendActivity extends AppCompatActivity {
    static final int CAMERA_REQUEST = 1888;
    static final int FILE_REQUEST = 1889;
    private TextView filePathTextView;
    private File photo;
    private Uri uri;
    private Button sendViaEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setTitle(getString(R.string.activity_send));
        final Button sendCamera = (Button) findViewById(R.id.SendCameraButton);
        final Button sendFile = (Button) findViewById(R.id.SendFileButton);
        sendViaEmail = (Button) findViewById(R.id.ViaEmail);
        filePathTextView = (TextView) findViewById(R.id.FilePath);

        sendCamera.setText(R.string.send_camera);
        sendFile.setText(R.string.send_file);
        sendViaEmail.setText(R.string.via_email);

        sendCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (uri != null) {
                    try {
                        File file = new File(uri.getPath());
                        file.getCanonicalFile().delete();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                    try {
                        photo = createImageFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (photo != null) {
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
                    }
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
        });

        sendFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (uri != null) {
                    try {
                        File file = new File(uri.getPath());
                        file.getCanonicalFile().delete();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Intent fileIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                fileIntent.setType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
                String mimes[] = {"application/pdf", "application/msword"};
                fileIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimes);
                startActivityForResult(fileIntent, FILE_REQUEST);
            }
        });

        sendViaEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (NetworkManager.isConnected()) {
                        if (uri != null) {
                            Intent sendIntent = new Intent();
                            sendIntent.setAction(Intent.ACTION_SENDTO);
                            sendIntent.setData(Uri.parse("mailto:"));
                            sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
                            sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"dorobenjamin@gmail.com"});
                            sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Akkord küldés");
                            startActivity(sendIntent);
                        }
                        else
                            Snackbar.make(findViewById(R.id.SendActivity), getString(R.string.no_inserted_file), Snackbar.LENGTH_LONG).show();
                    }
                    else
                        Snackbar.make(findViewById(R.id.SendActivity), getString(R.string.no_connection), Snackbar.LENGTH_LONG).show();
                } catch (InterruptedException | IOException e) {
                    Snackbar.make(findViewById(R.id.SendActivity), getString(R.string.no_connection), Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            filePathTextView.setVisibility(View.VISIBLE);
            filePathTextView.setText("Kép sikeresen betallózva");
            uri = Uri.fromFile(photo);
        }
        else if (requestCode == FILE_REQUEST && resultCode == RESULT_OK) {
            filePathTextView.setVisibility(View.VISIBLE);
            filePathTextView.setText("Dokumentum sikeresen betallózva");
            Uri temp = getFilePath(data.getData());
            if (temp != null) {
                uri = temp;
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private Uri getFilePath(Uri uriData) {
        try {
            InputStream input = getContentResolver().openInputStream(uriData);
            File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "akkord.pdf");
            uri = Uri.fromFile(file);
            OutputStream output = new FileOutputStream(file);
            try {
                byte[] buffer = new byte[4 * 1024];
                int read;
                while ((read = input.read(buffer)) != -1) {
                    output.write(buffer, 0, read);
                }
                output.flush();
            } finally {
                output.close();
                input.close();
                return uri;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        return image;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (uri != null) {
            File file = new File(uri.getPath());
            if (file.exists())
                try {
                    file.getCanonicalFile().delete();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }
}
