package com.example.m3_projeto_idosos;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.view.View;
import android.widget.Toast;

public class PhotoDetailActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView descriptionTextView;
    private EditText descriptionEditText;
    private TextView locationTextView;
    private TextView timestampTextView;
    private TextView categoryTextView;
    private Button saveButton;
    private boolean isEditing = false;
    private DatabaseHelper dbHelper;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);

        dbHelper = new DatabaseHelper(this);

        imageView = findViewById(R.id.imageView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        locationTextView = findViewById(R.id.locationTextView);
        timestampTextView = findViewById(R.id.timestampTextView);
        categoryTextView = findViewById(R.id.categoryTextView);
        saveButton = findViewById(R.id.saveButton);

        PhotoData photoData = getIntent().getParcelableExtra("PHOTO_DATA");

        ImageView backIcon = findViewById(R.id.backIcon);

        toggleEditMode(isEditing);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();  // Chama o método para voltar à tela anterior
            }
        });

        saveButton.setOnClickListener(v -> {
            if (isEditing) {
                String newDescription = descriptionEditText.getText().toString();
                    photoData.setDescription(newDescription);
                    dbHelper.updatePhotoDescription(photoData);
                    descriptionTextView.setText(newDescription);
                    toggleEditMode(false);
                    Toast.makeText(PhotoDetailActivity.this, "Descrição salva com sucesso", Toast.LENGTH_SHORT).show();
            } else {
                toggleEditMode(true);
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


    private void toggleEditMode(boolean enable) {
        isEditing = enable;
        if (enable) {
            saveButton.setText("SALVAR");
            descriptionEditText.setVisibility(View.VISIBLE);
            descriptionEditText.setEnabled(true);
            descriptionTextView.setVisibility(View.GONE);
            descriptionEditText.setText(descriptionTextView.getText()); // Mostrar o texto atual na EditText
        } else {
            saveButton.setText("EDITAR");
            descriptionEditText.setVisibility(View.GONE);
            descriptionEditText.setEnabled(false);
            descriptionTextView.setVisibility(View.VISIBLE);
            // Não é necessário definir o texto do descriptionTextView aqui, pois ele já foi atualizado ao salvar
        }
    }
}
