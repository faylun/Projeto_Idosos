package com.example.m3_projeto_idosos;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class PhotoDetailActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView descriptionTextView;
    private TextView timestampTextView;
    private TextView locationTextView;
    private TextView categoryTextView; // Novo TextView para exibir a categoria

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);

        imageView = findViewById(R.id.imageView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        timestampTextView = findViewById(R.id.timestampTextView);
        locationTextView = findViewById(R.id.locationTextView);
        categoryTextView = findViewById(R.id.categoryTextView); // Referência ao TextView da categoria

        // Receber dados do Intent
        PhotoData photoData = getIntent().getParcelableExtra("PHOTO_DATA");

        // Exibir dados na UI
        if (photoData != null) {
            imageView.setImageURI(photoData.getPhotoUri());
            descriptionTextView.setText(photoData.getDescription());
            timestampTextView.setText(photoData.getTimestamp());
            String location = "Lat: " + photoData.getLatitude() + ", Long: " + photoData.getLongitude();
            locationTextView.setText(location);
            categoryTextView.setText("Categoria: " + photoData.getCategory()); // Exibir a categoria
        }
    }
}
