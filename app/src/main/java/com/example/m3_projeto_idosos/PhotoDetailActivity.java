package com.example.m3_projeto_idosos;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
public class PhotoDetailActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView descriptionTextView;
    private TextView locationTextView;
    private TextView timestampTextView;
    private TextView categoryTextView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);

        imageView = findViewById(R.id.imageView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        locationTextView = findViewById(R.id.locationTextView);
        timestampTextView = findViewById(R.id.timestampTextView);
        categoryTextView = findViewById(R.id.categoryTextView);

        PhotoData photoData = getIntent().getParcelableExtra("PHOTO_DATA");

        ImageView backIcon = findViewById(R.id.backIcon);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();  // Chama o método para voltar à tela anterior
            }
        });

        if (photoData != null) {
            imageView.setImageURI(photoData.getPhotoUri());
            descriptionTextView.setText(photoData.getDescription());
            locationTextView.setText(photoData.getLocationAsString());
            timestampTextView.setText(photoData.getTimestamp());
            categoryTextView.setText(photoData.getCategory());
        }
    }
}
