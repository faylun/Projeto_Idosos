package com.example.m3_projeto_idosos;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PhotoDetailActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView descriptionTextView;
    private TextView timestampTextView;
    private TextView locationTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);

        imageView = findViewById(R.id.imageView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        timestampTextView = findViewById(R.id.timestampTextView);
        locationTextView = findViewById(R.id.locationTextView);

        // Receber dados do Intent
        PhotoData photoData = getIntent().getParcelableExtra("PHOTO_DATA");

        Log.i("gaby","initical data: " + photoData.getTimestamp());
        // Exibir dados na UI
        if (photoData != null) {

            imageView.setImageURI(photoData.getPhotoUri());
            descriptionTextView.setText(photoData.getDescription());
            Log.i("gaby","data: " + photoData.getTimestamp());
            timestampTextView.setText(photoData.getTimestamp());
            String location = "Lat: " + photoData.getLatitude() + ", Long: " + photoData.getLongitude();
            locationTextView.setText(location);
        }
    }
}
